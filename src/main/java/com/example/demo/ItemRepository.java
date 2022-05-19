package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
	List<Item> findByCategoryCode(Integer categoryCode);
	
	List<Item> findByCode(Integer code);
	
	List<Item> findByNameContaining(String keyword);
	
	List<Item> findByPriceGreaterThanEqual(Integer min);
	
	List<Item> findByPriceLessThanEqual(Integer max);
	
	List<Item> findByPriceBetween(Integer min,Integer max);
	
	List<Item> findByNameContainingAndPriceGreaterThanEqual(String keyword,Integer min);
	
	List<Item> findByNameContainingAndPriceLessThanEqual(String keyword,Integer max);
	
	List<Item> findByNameContainingAndPriceBetween(String keyword,Integer min,Integer max);
}
