package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

public class Cart {
	@Autowired
	HttpSession session;
	// Integerをインデックスにし、Itemクラスを記録。
	private Map<Integer, Item> items = new HashMap<>();
	private long total;

	public Map<Integer, Item> getItems() {
		return items;
	}

	public long getTotal() {
		return total;
	}

	public void addCart(Item item, int quantity) {
		Item existedItem = items.get(item.getCode());
		if (existedItem == null) {
			item.setQuantity(quantity);
			items.put(item.getCode(), item);
		} else {
			existedItem.setQuantity(existedItem.getQuantity() + quantity);
		}
		recalcTotal();
	}

	public void deleteCart(int itemCode) {
		items.remove(itemCode);
		recalcTotal();
	}

	private void recalcTotal() {
		total = 0;
		for (Item item : items.values()) {
			total += item.getPrice() * item.getQuantity();
		}
	}

}
