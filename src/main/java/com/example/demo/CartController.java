package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public ModelAndView addCart(ModelAndView mv,
			@PathVariable(name = "code") int code,
			@RequestParam(name = "quantity") int quantity) {
		Cart cart = getCartFromSession();
		
		Item item = itemRepository.findById(code).get();
		cart.addCart(item, quantity);
		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());

		mv.setViewName("cart");
		return mv;
	}
	@RequestMapping("/cart/delete/{code}")
	public ModelAndView deleteCart(
			ModelAndView mv, 
			@PathVariable(name = "code") int code) {

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
}