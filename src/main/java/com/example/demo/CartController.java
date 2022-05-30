package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CartController {
	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository itemRepository;

//カートに入れる
	@PostMapping("/cart/add/{code}")
	public ModelAndView addCart(
			ModelAndView mv, 
			@PathVariable(name = "code") int code,
			@RequestParam(name = "quantity") int quantity) {
		
		if (session.getAttribute("customerInfo") == null) {
			return ItemController.illigalAccess();
		}
		
		Cart cart = getCartFromSession();
		Customer customer = (Customer) session.getAttribute("customerInfo");
		Item item = itemRepository.findById(code).get();
		// タイムセール割引、ランク割引
		double timeSell = ItemController.judgeTimeSell();
		// 選択商品の値段
		int selectItemPrice = item.getPrice();
		// タイムセール処理

		if (timeSell < 1.0) {
			selectItemPrice = (int) (selectItemPrice - (selectItemPrice * timeSell));
		}

		// ランク割引
		long total = customer.getTotal();
		if (total >= 200000) {
			selectItemPrice = (int) ((int) selectItemPrice - (selectItemPrice * 0.1));
		} else if (total >= 100000 && total < 200000) {
			selectItemPrice = (int) ((int) selectItemPrice - (selectItemPrice * 0.05));
		} else if (total >= 50000 && total < 100000) {
			selectItemPrice = (int) ((int) selectItemPrice - (selectItemPrice * 0.03));
		}

		item.setPrice(selectItemPrice);
		cart.addCart(item, quantity);
		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());

		mv.setViewName("cart");
		return mv;
	}
//カートから削除
	@GetMapping("/cart/delete/{code}")
	public ModelAndView deleteCart(
			ModelAndView mv, 
			@PathVariable(name = "code") int code) {
		
		if (session.getAttribute("customerInfo") == null) {
			return ItemController.illigalAccess();
		}
		
		Cart cart = getCartFromSession();

		cart.deleteCart(code);

		mv.addObject("items", cart.getItems());
		mv.addObject("total", cart.getTotal());

		mv.setViewName("cart");
		return mv;
	}

	private Cart getCartFromSession() {
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}
		return cart;
	}
	//直接リンク防止
	@GetMapping("/cart/add/{code}")
	public ModelAndView illegalAddCart() {
		return ItemController.illigalAccess();
	}
}