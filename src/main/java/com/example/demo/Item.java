package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class Item {
	@Id
	@Column(name = "code")
	private Integer code;

	@Column(name = "category_code")
	private Integer categoryCode;

	@Column(name = "name")
	private String name;

	@Column(name = "price")
	private Integer price;
	
	@Column(name = "quantity")
	private Integer quantity;
	
	@Column(name = "image")
	private String image;

	public Integer getCode() {
		return code;
	}

	public Integer getCategoryCode() {
		return categoryCode;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public Integer getPrice() {
		return price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
