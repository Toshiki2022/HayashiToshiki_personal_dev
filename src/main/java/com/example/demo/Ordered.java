package com.example.demo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ordered")
public class Ordered {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code")
	private Integer code;

	@Column(name = "customer_code")
	private Integer customerCode;

	@Column(name = "ordered_date")
	private Date orderedDate;

	@Column(name = "total_price")
	private Long totalPrice;

	@Column(name = "payment")
	private String payment;

	public Ordered(Integer code, Integer customerCode, Date orderedDate, Long totalPrice,String payment) {
		this(customerCode, orderedDate, totalPrice,payment);
		this.code = code;
	}

	public Ordered(Integer customerCode, Date orderedDate, Long totalPrice,String payment) {
		this.customerCode = customerCode;
		this.orderedDate = orderedDate;
		this.totalPrice = totalPrice;
		this.payment = payment;
	}

	public Ordered() {
		super();
	}
	public Integer getCode() {
		return code;
	}

	public Integer getCustomerCode() {
		return customerCode;
	}

	public Date getOrderedDate() {
		return orderedDate;
	}

	public Long getTotalPrice() {
		return totalPrice;
	}

	public String getPayment() {
		return payment;
	}
}
