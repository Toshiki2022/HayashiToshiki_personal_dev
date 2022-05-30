package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ordered_detail")
public class OrderedDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code")
	private Integer code;

	@Column(name = "ordered_code")
	private Integer orderedCode;

	@Column(name = "item_code")
	private Integer itemCode;

	@Column(name = "num")
	private Integer num;
	
	public OrderedDetail(Integer code, Integer orderedCode, Integer itemCode, Integer num) {
		this(orderedCode, itemCode, num);
		this.code = code;
	};

	public OrderedDetail(Integer orderedCode, Integer itemCode, Integer num) {
		super();
		this.orderedCode = orderedCode;
		this.itemCode = itemCode;
		this.num = num;
	};

	public OrderedDetail(Integer orderCode, Item item) {
		this(orderCode, item.getCode(), item.getQuantity());
	};

	public OrderedDetail() {
		super();
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Integer getOrderedCode() {
		return orderedCode;
	}

	public void setOrderedCode(Integer orderedCode) {
		this.orderedCode = orderedCode;
	}

	public Integer getItemCode() {
		return itemCode;
	}

	public void setItemCode(Integer itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}



	
}

