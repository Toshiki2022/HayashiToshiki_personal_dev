package com.example.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/*目標
 *割引された値段で購入できるよう購入処理を更新
→ OrderedController.javaの修正


 ・一度に複数購入しても総計に反映されないバグ
 ・重複したログイン、パスがあるとログインできないバグ
 ・カートの中身がある状態で履歴にの詳細を押すと空になるバグ
 ・購入した後ランクが更新されるとエラーになる
 
*/
@Controller
public class ItemController {
	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	OrderedRepository orderedRepository;
	@Autowired
	OrderedDetailRepository orderedDetailRepository;

	// 全商品表示
	@GetMapping("/item")
	public ModelAndView items(ModelAndView mv, @RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "sort", defaultValue = "") String sort) {

		List<Item> itemList = null;

		// 全商品表示
		if (keyword.equals("") && sort.equals("")) {
			itemList = itemRepository.findAll();
			// 文字入力なしの昇順降順
		} else if (keyword.equals("") && "price_asc".equals(sort)) {
			itemList = itemRepository.findAllByOrderByPrice();
		} else if (keyword.equals("") && "price_desc".equals(sort)) {
			itemList = itemRepository.findAllByOrderByPriceDesc();
		}
		// 文字入力がある場合
		if (!keyword.equals("") && sort.equals("")) {
			itemList = itemRepository.findByNameLike("%" + keyword + "%");
		} else if (!keyword.equals("") && "price_asc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPrice("%" + keyword + "%");
		} else if (!keyword.equals("") && "price_desc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPriceDesc("%" + keyword + "%");
			;
		}

		// タイムセール中商品の値段を更新
		double timeSell = ItemController.judgeTimeSell();

		timeSellprice(timeSell);
		session.setAttribute("timeSell", timeSell);
		mv.addObject("sort", sort);
		mv.addObject("keyword", keyword);
		mv.addObject("item", itemList);

		mv.setViewName("item");
		return mv;
	}

	// ジャンル画面遷移
	@PostMapping("/item/genre")
	public ModelAndView itemsByGenre(ModelAndView mv, @RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "sort", defaultValue = "") String sort,
			@RequestParam(name = "categoryCode", defaultValue = "") Integer categoryCode) {

		List<Item> itemList = null;

		itemList = itemRepository.findAllByCategoryCode(categoryCode);

		if (keyword.equals("") && sort.equals("")) {
			itemList = itemRepository.findAllByCategoryCode(categoryCode);
			// 文字入力なしの昇順降順
		} else if (keyword.equals("") && "price_asc".equals(sort)) {
			itemList = itemRepository.findAllByCategoryCodeOrderByPrice(categoryCode);
		} else if (keyword.equals("") && "price_desc".equals(sort)) {
			itemList = itemRepository.findAllByCategoryCodeOrderByPriceDesc(categoryCode);
		}

		// 文字入力がある場合
		if (!keyword.equals("") && sort.equals("")) {
			itemList = itemRepository.findByNameLike("%" + keyword + "%");
		} else if (!keyword.equals("") && "price_asc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPrice("%" + keyword + "%");
		} else if (!keyword.equals("") && "price_desc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPriceDesc("%" + keyword + "%");
		}
		// タイムセール中商品の値段を更新
		double timeSell = ItemController.judgeTimeSell();
		timeSellprice(timeSell);
		
		mv.addObject("sort", sort);
		mv.addObject("keyword", keyword);
		mv.addObject("categoryCode", categoryCode);
		mv.addObject("item", itemList);
		mv.setViewName("genre");
		return mv;
	}

	// 商品詳細画面に遷移
	@GetMapping("/item/{code}")
	public ModelAndView showItem(ModelAndView mv, @PathVariable(name = "code") int code) {

		int flag = 0;
		// アイテム
		List<Item> itemList = itemRepository.findByCode(code);

		Item itemCategorycode = itemRepository.getByCode(code);
		Integer categoryCode = itemCategorycode.getCategoryCode();

		List<Category> categoryList = categoryRepository.findByCode(categoryCode);

		Customer customer = (Customer) session.getAttribute("customerInfo");
		//timeSellにはdouble型の数字1,0.1が入る
		double timeSell = ItemController.judgeTimeSell();
		int selectItemPrice =itemCategorycode.getPrice() ;
		timeSellprice(timeSell);
		//タイムセール割引
		
		if(timeSell<1.0) {
			 selectItemPrice=(int)(selectItemPrice-(selectItemPrice*timeSell));
			}
		
		// ランクに応じて割引＆フラグ設定
		long total = customer.getTotal();
		if (total >= 200000) {
			flag = 3;// Gold
			selectItemPrice = (int) ((int) selectItemPrice - (selectItemPrice * 0.1));
		} else if (total >= 100000 && total < 200000) {
			flag = 2;// Silver
			selectItemPrice = (int) ((int) selectItemPrice - (selectItemPrice * 0.05));
		} else if (total >= 50000 && total < 100000) {
			flag = 1;// Blonze
			selectItemPrice = (int) ((int) selectItemPrice -(selectItemPrice * 0.03));
		}
		
		
		mv.addObject("RankPrice", selectItemPrice);
		mv.addObject("flag", flag);
		mv.addObject("item", itemList);
		mv.addObject("genre", categoryList);
		mv.setViewName("detail");
		return mv;
	}

	// 注文履歴へ遷移
	@GetMapping("/item/history")
	public ModelAndView history(ModelAndView mv) {

		Customer customer = (Customer) session.getAttribute("customerInfo");
		List<Ordered> ordered = orderedRepository.findAllByCustomerCode(customer.getCode());

		mv.addObject("ordered", ordered);
		mv.setViewName("orderhistory");
		return mv;
	}
	// 注文履歴詳細
	@GetMapping("/item/orderhistorydetail/{code}")
	public ModelAndView historydetail(ModelAndView mv, @PathVariable("code") Integer code) {

		session.removeAttribute("orderhistory");
		Cart orderhistory = (Cart) session.getAttribute("orderhistory");
		if (orderhistory == null) {
			orderhistory = new Cart();
			session.setAttribute("orderhistory", orderhistory);
		}

		List<OrderedDetail> orderDetail = orderedDetailRepository.findAllByOrderedCode(code);
		for (OrderedDetail lists : orderDetail) {
			Item item = itemRepository.findById(lists.getItemCode()).get();
			orderhistory.addCart(item, lists.getQuantity());
		}

		mv.addObject("items", orderhistory.getItems());
		mv.addObject("total", orderhistory.getTotal());
		mv.setViewName("orderhistorydetail");
		return mv;
	}

	// カートの中身ページへ遷移
	@GetMapping("/item/cart")
	public ModelAndView cart(ModelAndView mv) {

		mv.setViewName("cart");
		return mv;
	}

	

	public static double judgeTimeSell() {

		// 時刻のみフォーマット
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("HHmm");
		// 現在時刻
		LocalDateTime currentDate = LocalDateTime.now();
		String tmp = currentDate.format(sdf);
		int nowTime = Integer.parseInt(tmp);

		if (nowTime >= 1300 && nowTime <= 1700) {
			return 0.1;
		} else if (nowTime >= 1700 && nowTime <= 1800) {
			return 0.1;
		}
		return 1.0;
	}

	public void timeSellprice(double timeSell) {

		List<Item> itemList = itemRepository.findAll();
		//全商品割引
		for (Item item : itemList) {
			if (timeSell < 1.0) {
				item.setPrice((int) (item.getPrice() - (item.getPrice() * timeSell)));
			} else {
				item.setPrice((int) (item.getPrice()));
			}
		}	
	}

}
