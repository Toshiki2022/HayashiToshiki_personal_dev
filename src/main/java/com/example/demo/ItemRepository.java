package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	// 商品コード検索用
	Item findByCode(Integer code);
//ジャンル検索用
	List<Item> findAllByCategoryCode(Integer categoryCode);
	
	//ジャンル検索用
	Item findCategorycodeByCode(Integer categoryCode);
	
	List<Item> findAllByOrderByCodeAsc();
	// 価格の昇順
	List<Item> findAllByOrderByPrice();
//ジャンルごとの価格の昇順
	List<Item> findAllByCategoryCodeOrderByPrice(Integer categoryCode);
	//価格の降順
	List<Item> findAllByOrderByPriceDesc();
//ジャンルごとの価格の降順
	List<Item> findAllByCategoryCodeOrderByPriceDesc(Integer categoryCode);
	// 文字検索
	List<Item> findByNameLikeOrderByCategoryCodeAsc(String keyword);
	// 文字検索＆昇順降順
	List<Item> findByNameLikeOrderByPrice(String keyword);
	
	List<Item> findByNameLikeOrderByPriceDesc(String keyword);
	
	List<Item> findAllByCategoryCodeOrderByImageAsc(Integer categoryCode);

	
}
