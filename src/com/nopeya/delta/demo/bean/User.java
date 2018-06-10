package com.nopeya.delta.demo.bean;

import com.nopeya.delta.core.annotation.FK;
import com.nopeya.delta.core.annotation.Field;
import com.nopeya.delta.core.annotation.Table;

@Table
public class User {
	/**
	 * 序号
	 */
	@FK @Field 
	private String id;
	/**
	 * 姓名
	 */
	@Field("name") private String name;
	/**
	 * 电话
	 */
	@Field private String tel;
	/**
	 * 编码
	 */
	@Field private String code;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
}
