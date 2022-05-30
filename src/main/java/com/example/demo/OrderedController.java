package com.example.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
		
		if (session.getAttribute("customerInfo") == null) {
			return ItemController.illigalAccess();
		}
		
		Cart cart = getCartFromSession();

		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());
		mv.addObject("customerInfo", session.getAttribute("customerInfo"));

		mv.setViewName("purchase");
		return mv;
	}

	// 購入確認画面
	@PostMapping("/order/confirm")
	public ModelAndView confirm(
			ModelAndView mv, 
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "address", defaultValue = "") String address,
			@RequestParam(name = "tel", defaultValue = "") String tel,
			@RequestParam(name = "email", defaultValue = "") String email, 
			@RequestParam("payment") String payment) {

		if (session.getAttribute("customerInfo") == null) {
			return ItemController.illigalAccess();
		}
		String nameStrip = name.strip();
		String addressStrip = address.strip();
		String telStrip = tel.strip();
		String emailStrip = email.strip();

		Cart cart = getCartFromSession();

		if (nameStrip.equals("") || addressStrip.equals("") || telStrip.equals("") || emailStrip.equals("")) {

			mv.addObject("errorFlag", 1);
			mv.addObject("total", cart.getTotal());
			mv.addObject("items", cart.getItems());
			mv.setViewName("purchase");
			return mv;
		}

		// 半角で入力されているか
		if (AccountController.fullWidthCheck(telStrip) || AccountController.fullWidthCheck(emailStrip)) {

			mv.addObject("errorFlag", 2);
			mv.addObject("total", cart.getTotal());
			mv.addObject("items", cart.getItems());
			mv.setViewName("purchase");
			return mv;
		}
		// 数字が入力されているか
		if (AccountController.numericCheck(telStrip)) {
			mv.addObject("errorFlag", 3);
			mv.addObject("total", cart.getTotal());
			mv.addObject("items", cart.getItems());
			mv.setViewName("purchase");
			return mv;
		}

		session.setAttribute("name", nameStrip);
		session.setAttribute("address", addressStrip);
		session.setAttribute("tel", telStrip);
		session.setAttribute("email", emailStrip);
		session.setAttribute("payment", payment);
		
		mv.addObject("total", cart.getTotal());
		mv.addObject("items", cart.getItems());
		mv.setViewName("confirm");
		return mv;
	}

	// 購入処理
	@PostMapping("/order/doOrder")
	public ModelAndView doOrder(ModelAndView mv) {
		if (session.getAttribute("customerInfo") == null) {
			return ItemController.illigalAccess();
		}
		Customer customer = (Customer) session.getAttribute("customerInfo");
		// 購入画面で送り先等に変更があった場合DB更新
		if (!(customer.getName().equals((String) session.getAttribute("name")))) {
			customer.setName((String) session.getAttribute("name"));
		}
		if (!(customer.getName().equals((String) session.getAttribute("address")))) {
			customer.setAddress((String) session.getAttribute("address"));
		}
		if (!(customer.getName().equals((String) session.getAttribute("tel")))) {
			customer.setTel((String) session.getAttribute("tel"));
		}
		if (!(customer.getName().equals((String) session.getAttribute("email")))) {
			customer.setEmail((String) session.getAttribute("email"));
		}

		Cart cart = getCartFromSession();
		Ordered order = new Ordered(customer.getCode(), new Date(), cart.getTotal(),
				(String) session.getAttribute("payment"));

		// 顧客のこれまでの購入金額を更新
		Long customerTotal = customer.getTotal();
		customer.setTotal(customerTotal + cart.getTotal());
		customerRepository.save(customer);
		// 注文内容をDBに保存
		int orderCode = orderedRepository.saveAndFlush(order).getCode();

		Map<Integer, Item> items = cart.getItems();
		List<OrderedDetail> orderDetails = new ArrayList<>();
		// 注文詳細をDBに記録
		for (Item item : items.values()) {
			orderDetails.add(new OrderedDetail(orderCode, item.getCode(), item.getQuantity()));
		}
		orderDetailRepository.saveAll(orderDetails);
		// 在庫数更新
		Integer stockQuantity = 0;

		for (OrderedDetail orderedDetails : orderDetails) {
			// 購入数取得
			Integer numberOfOrders = orderedDetails.getNum();
			// 商品コード取得
			Integer itemCode = orderedDetails.getItemCode();

			// 商品コードに一致する商品のインスタンス生成
			Item orderedItem = itemRepository.findByCode(itemCode);
			// 在庫数更新
			stockQuantity = orderedItem.getQuantity();
			stockQuantity = stockQuantity - numberOfOrders;
			orderedItem.setQuantity(stockQuantity);
			itemRepository.save(orderedItem);
		}
		Customer customerId = customerRepository.findById(customer.getCode()).get();
		session.setAttribute("customerInfo", customerId);

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

	// 直接リンク防止用
	@GetMapping("/order/confirm")
	public ModelAndView illegalAddCart() {
		return ItemController.illigalAccess();
	}

	@GetMapping("/order/doOrder")
	public ModelAndView illegalDoOrder() {
		return ItemController.illigalAccess();
	}
}
