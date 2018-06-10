package com.nopeya.delta.demo.bean;

import com.nopeya.delta.core.annotation.FK;
import com.nopeya.delta.core.annotation.Field;
import com.nopeya.delta.core.annotation.Table;

@Table
public class Book {
	/**
	 * 序号
	 */
	@FK @Field private int id;
	/**
	 * 书名
	 */
	@Field private String bookname;
	/**
	 * 编码
	 */
	@Field private String isbn;
	
	@Field private float price;
	
	

	public Book() {
		super();
	}

	public Book(int id, String bookname, String isbn, float price) {
		super();
		this.id = id;
		this.bookname = bookname;
		this.isbn = isbn;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	
}
