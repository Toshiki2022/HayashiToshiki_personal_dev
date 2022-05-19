package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "category")
public class Category {
	@Id
	@Column(name = "code")
	private Integer code;
	
	@Column(name = "name")
	private String name;

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}