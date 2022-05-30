package com.example.demo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "code")
	private Integer code;

	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "address")
	private String address;

	@Column(name = "tel")
	private String tel;

	@Column(name = "total")
	private Long total;

    //コンストラクタ
	public Customer() {

	}
	public Customer(Integer code,String id,String name,String email,String password,String address,String tel,Long total) {
		this(id,name,email,password,address,tel);
		this.code=code;
		this.total=total;
	};
	public Customer(String id,String name,String email,String password,String address,String tel) {
		super();
		this.id=id;
		this.name=name;
		this.email=email;
		this.password=password;
		this.address=address;
		this.tel=tel;
	};


	public Integer getCode() {
		return code;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getAddress() {
		return address;
	}

	public String getTel() {
		return tel;
	}

	public Long getTotal() {
		return total;
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public void setTotal(Long total) {
		this.total = total;
	}

}
