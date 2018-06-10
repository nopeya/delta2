package com.nopeya.delta.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 储存表信息类
 * NopeYa 2017年7月23日
 */
public class TableInfo {
	
	/**
	 * 表名
	 *   必须和数据库内对应的表名一致
	 */
	private String table;

	/**
	 * 主键 （单主键）
	 *   必须和数据库内对应的主键一致
	 */
	private String fk;

	/**
	 * 字段
	 *   必须和数据库内对应的字段一致 <Field, name>
	 */
	private Map<Field, String> fields;
	
	/**
	 * 子资源列表
	 */
	private List<Class<?>> subResources;
	/**
	 * 初始化
	 * 	table默认为 ""
	 * 	fk默认为"id"
	 */
	public TableInfo() {
		table = "";
		fk = "id";
		fields = new HashMap<>();
		subResources = new ArrayList<>();
	}
	
	/**
	 * 设置表名
	 *   表名统一为小写字符串
	 * @param clazz
	 */
	public void setTable(Class<?> clazz) {
		this.table = clazz.getSimpleName().toLowerCase();
	}
	public void setTable(String table) {
		this.table = table.toLowerCase();
	}
	
	/**
	 * 设置主键
	 *   主键统一为小写字符串
	 * @param field
	 */
	public void setFK(Field field) {
		this.fk = field.getName().toLowerCase();
	}
	public void setFK(String fieldName) {
		this.fk = fieldName.toLowerCase();
	}

	/**
	 * 添加一个字段
	 *   所有字段均为小写的字符串
	 * @param field
	 */
	public void addField(Field field) {
		this.fields.put(field, field.getName().toLowerCase());
	}
	public void addField(Field field, String fieldName) {
		this.fields.put(field, fieldName.toLowerCase());
	}
	
	/**
	 * 添加一个子资源
	 * @param sub
	 */
	public void addSubResource(Class<?> sub) {
		subResources.add(sub);
	}
	
	public String getTable() {
		return table;
	}
	public String getFk() {
		return fk;
	}
	public Map<Field, String> getFields() {
		return fields;
	}
}
