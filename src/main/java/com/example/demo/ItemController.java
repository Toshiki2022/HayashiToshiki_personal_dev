package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/*今日の目標
 * 

・商品詳細ページの完成
 * 
*/
@Controller
public class ItemController {
	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	CategoryRepository categoryRepository;
	// 全商品表示
	@GetMapping("/item")
	public ModelAndView items(
			ModelAndView mv, 
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "sort", defaultValue = "") String sort) {
		
		List<Item> itemList = null;
		
		//全商品表示
		if(keyword.equals("")&&sort.equals("")) {
			itemList = itemRepository.findAll();
		//文字入力なしの昇順降順
		}else if(keyword.equals("")&&"price_asc".equals(sort)) {
			itemList = itemRepository.findAllByOrderByPrice();
		}else if(keyword.equals("")&&"price_desc".equals(sort)) {
			itemList = itemRepository.findAllByOrderByPriceDesc();
		}
		//文字入力がある場合
		if(!keyword.equals("")&&sort.equals("")) {
			itemList=itemRepository.findByNameLike("%"+keyword+"%");
		}else if(!keyword.equals("")&&"price_asc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPrice("%"+keyword+"%");
		}else if(!keyword.equals("")&&"price_desc".equals(sort)) {
			itemList = itemRepository.findByNameLikeOrderByPriceDesc("%"+keyword+"%");;
		}

		mv.addObject("sort", sort);
		mv.addObject("keyword", keyword);
		mv.addObject("item", itemList);

		mv.setViewName("item");
		return mv;
	}
	//商品詳細画面に遷移
	@GetMapping("/item/{code}")
	public ModelAndView showItem(
			ModelAndView mv,
			@PathVariable(name = "code") int code) {
		List<Item> itemList = itemRepository.findByCode(code);
		
		mv.addObject("item",itemList);

		mv.setViewName("detail");
		return mv;
	}
	//注文履歴へ遷移
	@GetMapping("/item/history")
	public ModelAndView history(
			ModelAndView mv) {
		
		mv.setViewName("orderhistory");
		return mv;
	}
	//カートの中身ページへ遷移
	@GetMapping("/item/cart")
	public ModelAndView cart(
			ModelAndView mv) {
		
		mv.setViewName("cart");
		return mv;
	}

}
