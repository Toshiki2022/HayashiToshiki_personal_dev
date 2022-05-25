package com.example.demo;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccountController {

	@Autowired
	HttpSession session;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ItemRepository itemRepository;

	// ログイン画面
	@GetMapping("/")
	public String login() {
		session.invalidate();
		return "login";
	}

	// ログイン処理
	@PostMapping(value = "/login")
	public ModelAndView doLogin(@RequestParam("loginId") String id, @RequestParam("password") String password,
			ModelAndView mv) {
		// 名前が空の場合にエラーとする
		if (id == null || id.length() == 0) {
			mv.addObject("message", "IDを入力してください");
			mv.setViewName("login");
			return mv;
		}
		// パスワードが空の場合にエラーとする
		if (password == null || password.length() == 0) {
			mv.addObject("message", "パスワードを入力してください");
			mv.setViewName("login");
			return mv;
		}

		// DBにIDとPASSがあるかチェック
		Customer customerId = customerRepository.findById(id);
		Customer customerPass = customerRepository.findByPassword(password);
		if (Objects.isNull(customerId) && Objects.isNull(customerPass)) {
			mv.addObject("message", "IDかパスワードが違います");
			mv.setViewName("login");
			return mv;
		}

		// セッションスコープにログイン名とカテゴリ情報を格納する
		session.setAttribute("customerInfo", customerId);
		session.setAttribute("categories", categoryRepository.findAll());

		// タイムセール割引価格設定
		double timeSell = ItemController.judgeTimeSell();
		session.setAttribute("timeSell", timeSell);

		mv.addObject("customerInfo", session.getAttribute("customerInfo"));
		mv.addObject("sort", "");
		mv.addObject("keyword", "");
		
		List<Item> itemList = itemRepository.findAll();
		//タイムセール中商品の値段を更新
		for (Item item : itemList) {
			if (timeSell < 1.0) {
				item.setPrice((int) (item.getPrice()-(item.getPrice() * timeSell)));
			} else {
				item.setPrice((int) (item.getPrice()));
			}
		}
		
		mv.addObject("item", itemList);
		mv.setViewName("item");
		return mv;
	}

	// 新規会員登録画面
	@GetMapping("/new")
	public String signUpView() {
		session.invalidate();
		return "signup";
	}

	// 新規会員登録処理
	@PostMapping("/signup")
	public ModelAndView signUp(ModelAndView mv, @RequestParam("id") String id,
			@RequestParam("password") String password, @RequestParam("name") String name,
			@RequestParam("address") String address, @RequestParam("tel") String tel,
			@RequestParam("email") String email) {

		Customer DuplicateId = customerRepository.findById(id);
		if (Objects.isNull(DuplicateId)) {

			Customer entity = new Customer(null, id, name, email, password, address, tel, (long) 0);
			customerRepository.save(entity);
			mv.addObject("message", "登録完了");
			mv.setViewName("/login");
			return mv;
		}
		mv.addObject("message", "ログインIDが重複しています。");
		mv.setViewName("signup");
		return mv;

	}


}
