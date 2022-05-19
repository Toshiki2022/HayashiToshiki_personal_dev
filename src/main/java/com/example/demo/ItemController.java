package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {
	@Autowired
	HttpSession session;
	@Autowired
	ItemRepository itemRepository;

	// 全商品表示
	@GetMapping("/item")
	public ModelAndView items(
			ModelAndView mv,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "price_asc", defaultValue = "") String price_asc,
			@RequestParam(name = "price_desc", defaultValue = "") String price_desc) {
		
		List<Item> itemList = itemRepository.findAll();
		mv.addObject("item", itemList);

		mv.setViewName("item");
		return mv;
	}
	
	
}
