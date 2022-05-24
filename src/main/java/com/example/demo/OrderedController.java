package com.example.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OrderedController {
	@Autowired
	HttpSession session;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	OrderedRepository orderedRepository;
	@Autowired
	OrderedDetailRepository orderDetailRepository;

	// 購入画面表示
	@GetMapping("/order")
	public ModelAndView index(ModelAndView mv) {
		Cart cart = getCartFromSession();

		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());
		mv.addObject("customerInfo", session.getAttribute("customerInfo"));

		mv.setViewName("purchase");
		return mv;
	}

	// 購入確認画面
	@PostMapping("/order/confirm")
	public ModelAndView confirm(ModelAndView mv, @RequestParam("name") String name,
			@RequestParam("address") String address, @RequestParam("tel") String tel,
			@RequestParam("email") String email, @RequestParam("payment") String payment) {
		Cart cart = getCartFromSession();
		mv.addObject("name", name);
		mv.addObject("address", address);
		mv.addObject("tel", tel);
		mv.addObject("email", email);
		mv.addObject("payment", payment);
		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());
		mv.setViewName("confirm");
		return mv;
	}

	// 購入処理
	@PostMapping("/order/doOrder/{payment}")
	public ModelAndView doOrder(
			ModelAndView mv, 
			@PathVariable(name = "payment") String payment) {
		Customer customer = (Customer) session.getAttribute("customerInfo");
		Cart cart = getCartFromSession();
		Ordered order = new Ordered(customer.getCode(), new Date(), cart.getTotal(), payment);
		Long customerTotal=customer.getTotal();
		customer.setTotal(customerTotal+cart.getTotal());
		customerRepository.save(customer);
		int orderCode = orderedRepository.saveAndFlush(order).getCode();

		Map<Integer, Item> items = cart.getItems();
		List<OrderedDetail> orderDetails = new ArrayList<>();
		
		for (Item item : items.values()) {
			orderDetails.add(new OrderedDetail(orderCode, item.getCode(), item.getQuantity()));
			
		}
		
		orderDetailRepository.saveAll(orderDetails);
		session.setAttribute("cart", new Cart());
		// 画面返却用注文番号を設定
		mv.addObject("orderNumber", orderCode);
		mv.setViewName("ordered");
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
