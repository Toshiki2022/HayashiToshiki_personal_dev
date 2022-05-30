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

		if (!Objects.isNull(customerId)) {
			if ((customerId.getPassword()).equals(password)) {
				// セッションスコープにログイン名とカテゴリ情報を格納する
				session.setAttribute("customerInfo", customerId);
				session.setAttribute("categories", categoryRepository.findAll());

				// タイムセール割引価格設定
				double timeSell = ItemController.judgeTimeSell();
				session.setAttribute("timeSell", timeSell);

				mv.addObject("customerInfo", session.getAttribute("customerInfo"));
				mv.addObject("sort", "");
				mv.addObject("keyword", "");

				List<Item> itemList = itemRepository.findAllByOrderByCodeAsc();
				// タイムセール中商品の値段を更新
				for (Item item : itemList) {
					if (timeSell < 1.0) {
						item.setPrice((int) (item.getPrice() - (item.getPrice() * timeSell)));
					} else {
						item.setPrice((int) (item.getPrice()));
					}
				}
				mv.addObject("item", itemList);
				mv.setViewName("item");
				return mv;
			} else {
				mv.addObject("message", "パスワードが違います");
				mv.setViewName("login");
				return mv;
			}
		} else {
			mv.addObject("message", "IDが違います");
			mv.setViewName("login");
			return mv;
		}

	}

	// 新規会員登録画面
	@GetMapping("/new")
	public String signUpView() {
		session.invalidate();
		return "signup";
	}

	// 新規会員登録処理
	@PostMapping("/signUp")
	public ModelAndView signUp(
			ModelAndView mv, 
			@RequestParam(name = "id", defaultValue = "") String id,
			@RequestParam(name = "password", defaultValue = "") String password,
			@RequestParam(name = "name", defaultValue = "") String name,
			@RequestParam(name = "address", defaultValue = "") String address,
			@RequestParam(name = "tel", defaultValue = "") String tel,
			@RequestParam(name = "email", defaultValue = "") String email) {

		String idStrip = id.strip();
		String passStrip = password.strip();
		String nameStrip = name.strip();
		String addressStrip = address.strip();
		String telStrip = tel.strip();
		String emailStrip = email.strip();

		// 各未入力チェック
		if (idStrip == "" || idStrip.length() == 0 || passStrip == "" || passStrip.length() == 0 || nameStrip == ""
				|| nameStrip.length() == 0 || addressStrip == "" || addressStrip.length() == 0 || telStrip == ""
				|| telStrip.length() == 0 || emailStrip == "" || emailStrip.length() == 0) {
			mv.addObject("errorFlag", 1);
			mv.setViewName("signup");
			return mv;
		}

		// 半角で入力されているか
		if (fullWidthCheck(password) || fullWidthCheck(tel) || fullWidthCheck(email)) {
			mv.addObject("errorFlag", 2);
			mv.setViewName("signup");
			return mv;
		}
		//数字が入力されているか
		if(numericCheck(telStrip)) {
			mv.addObject("errorFlag", 3);
			mv.setViewName("signup");
			return mv;
		}
		// DBにIDが既に登録されているか
		Customer DuplicateId = customerRepository.findById(id);
		if (Objects.isNull(DuplicateId)) {

			Customer entity = new Customer(null, idStrip, nameStrip, emailStrip, passStrip, addressStrip, telStrip,
					(long) 0);
			customerRepository.save(entity);
			mv.addObject("message", "登録完了");
			mv.setViewName("/login");
			return mv;
		}
		mv.addObject("errorFlag", 4);
		mv.setViewName("signup");
		return mv;

	}

	// 数字がどうかチェック
	public static boolean numericCheck(String num) {
		boolean isNumeric = false;
		for (int i = 0; i < num.length(); i++) {
			if (!Character.isDigit(num.charAt(i))) {
				isNumeric = true;
			}
		}
		return isNumeric;
	}

//全角文字が含まれているかチェック
	public static boolean fullWidthCheck(String input) {
		char[] chars = input.toCharArray();
		boolean judge = false;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if ((c <= '\u007e') || // 英数字

					(c == '\u00a5') || // \記号

					(c == '\u203e') || // ~記号

					(c >= '\uff61' && c <= '\uff9f') // 半角カナ
			) {
			}else {
				judge = true;
			}
		}
		return judge;
	}

}
