package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	// 商品コード検索用
	List<Item> findByCode(Integer code);

	//ジャンル検索用
	Integer findCategorycodeByCode(Integer code);
	// 価格の昇順
	List<Item> findAllByOrderByPrice();

	//価格の降順
	List<Item> findAllByOrderByPriceDesc();

	// 文字検索
	List<Item> findByNameLike(String keyword);
	// 文字検索＆昇順降順
	List<Item> findByNameLikeOrderByPrice(String keyword);
	
	List<Item> findByNameLikeOrderByPriceDesc(String keyword);

	
}
