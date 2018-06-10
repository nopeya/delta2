package com.nopeya.delta.core;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.common.utils.GenericsUtils;

import com.nopeya.delta.core.annotation.FK;
import com.nopeya.delta.core.annotation.Field;
import com.nopeya.delta.core.db.DataGetter;
import com.nopeya.delta.core.db.DataSetter;
import com.nopeya.delta.core.db.TransactionExecutor;

public class BaseDao<T> {
	/**
	 * 排序  逆序 DESC | 顺序 ASC
	 */
	protected static final int ORDER_DESC = SqlHelper.ORDER_DESC;
	protected static final int ORDER_ASC = SqlHelper.ORDER_ASC;
	/**
	 * SQL语句构造器
	 */
	private SqlHelper sqlHelper;
	// sql语句寄存器，用于提交事务
	private ArrayList<String> sqlRegister;
	// 是否自动重置sqlHelper
	private boolean AUTO_RESET_WITH_REGISTER = false;
	private List<T> list;
	private Class<?> clazz;
	private TableInfo tableInfo;
	private String fk;
	
	/*
	 * 构造器
	 */
	public BaseDao() {
		clazz = GenericsUtils.getSuperClassGenricType(getClass(), 0);
		tableInfo = Scanner.tableList.get(clazz);
		fk = tableInfo.getFk();
		this.sqlHelper = new SqlHelper(clazz);
		this.sqlRegister = new ArrayList<String>();
		this.list = new ArrayList<>();
	}
	
	/**
	 * 查询模式
	 * @return
	 */
	public BaseDao<T> list() {
		this.sqlHelper.list();
		return this;
	}
	/**
	 * 添加模式
	 * @return
	 */
	public BaseDao<T> add() {
		this.sqlHelper.add();
		return this;
	}
	/**
	 * 更新模式
	 * @return
	 */
	public BaseDao<T> update() {
		this.sqlHelper.update();
		return this;
	}
	/**
	 * 删除模式
	 * @return
	 */
	public BaseDao<T> delete() {
		this.sqlHelper.delete();
		return this;
	}
	/**
	 * insert子句里的字段和值构造器
	 * @param field
	 * @param value
	 * @return
	 */
	public BaseDao<T> init(String field, Object value){
		sqlHelper.init(field, value);
		return this;
	}
	/**
	 * set子句
	 * @param set
	 * @return
	 */
	public BaseDao<T> set(String field, Object value) {
		sqlHelper.set(field, value);
		return this;
	}
	/**
	 * where子句
	 * @param where
	 * @return
	 */
	public BaseDao<T> andWhere(String where) {
		sqlHelper.andWhere(where);
		return this;
	}
	public BaseDao<T> orWhere(String where) {
		sqlHelper.orWhere(where);
		return this;
	}
	/**
	 * 分页
	 * @param offset 偏移量(从0开始)
	 * @param rows 行数
	 * @return
	 */
	public BaseDao<T> limit(int offset, int rows) {
		sqlHelper.limit(offset, rows);
		return this;
	}
	/**
	 * 添加排序字段
	 * @param field
	 * @param o
	 * @return
	 */
	public BaseDao<T> orderBy(String field, int o) {
		sqlHelper.orderBy(field, o);
		return this;
	}
	/**
	 * 寄存一条SQL语句
	 * 	<p>寄存后视需求调用reset()重置SqlHelper
	 *  <p>调用setAutoReset()设置AUTO_RESET_WITH_REGISTER属性为真
	 *  <br>可自动随register()方法重置
	 * @return
	 */
	public BaseDao<T> register(String tempSql) {
		if (null != tempSql && !"".equals(tempSql.trim())) {
			sqlRegister.add(tempSql);
			if (AUTO_RESET_WITH_REGISTER) {
				this.reset();
			}
		}
		return this;
	}
	/**
	 * 设置是否自动重置
	 * @param auto
	 */
	public void setAutoReset(boolean autoWithRegister) {
		this.AUTO_RESET_WITH_REGISTER = autoWithRegister;
	}
	/**
	 * 清除构造器使用记录
	 * 		new sql()
	 *		wheres.clear();
	 *		sets.clear();
	 *		initors.clear();
	 *		orders.clear();
	 * @return
	 */
	public BaseDao<T> reset() {
		list.clear();
		sqlHelper.reset();
		return this;
	}
	/**
	 * 清空sql语句寄存器
	 * @return
	 */
	public BaseDao<T> clearRegister() {
		this.clearRegister();
		return this;
	}
	
	/**
	 * 获取查询结果
	 * @param params 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> get(Object...params) {
		DataGetter getter = new DataGetter();
		getter.list(sqlHelper.toString(), rs -> {
			try {
				while(rs.next()) {
					T entity;
					try {
						// 强转一个新实例
						entity = (T) clazz.newInstance();
						// 将实例添加进列表
						list.add(entity);
						// 遍历属性，为实例的属性赋值
						Arrays.asList(clazz.getDeclaredFields()).forEach(field -> {
							// 根据属性和注解获取对应的字段名
							if (field.isAnnotationPresent(Field.class)) {
								String fieldName = field.getAnnotation(Field.class).value().trim();
								if (fieldName.isEmpty()) {
									fieldName = field.getName();
								}
								try {
									// 设置属性访问权限
									field.setAccessible(true);
//									// 获取类型
//									Object value = field.getType().cast(rs.getString(fieldName));
//					                // 赋值
//									field.set(entity, value);
									field.set(entity, rs.getObject(fieldName));
								} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
									e.printStackTrace();
								}
							}
						});
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, params);
		return list;
	}
	
	/**
	 * 执行增、删、改
	 * @param params
	 * @return
	 */
	public int execute(Object...params) {
		int r = 0;
		DataSetter setter = new DataSetter();
		r = setter.execute(false, sqlHelper.toString(), params);
		return r;
	}
	/**
	 * 执行增，并获得key
	 * @param params
	 * @return
	 */
	public int insertForKey(Object...params) {
		int r = 0;
		DataSetter setter = new DataSetter();
		r = setter.execute(true, sqlHelper.toString(), params);
		return r;
	}
	/**
	 * 执行事务
	 */
	public void transaction() {
		TransactionExecutor executor = new TransactionExecutor();
		executor.transaction(sqlRegister.stream().toArray(String[]::new));
	}
	
	public int insert(T object) {
		if (!clazz.isAssignableFrom(object.getClass())) {
			throw new RuntimeException("[error]: 插入的类型不匹配。");
		}
		
		Map<java.lang.reflect.Field, String> fields = tableInfo.getFields();
		this.reset().add();
		Arrays.asList(object.getClass().getDeclaredFields()).forEach(field -> {
			if (field.isAnnotationPresent(FK.class)) {
				return;
			}
			field.setAccessible(true);
			try {
				Object value = field.get(object);
				if (value != null) {
					String fieldName = fields.get(field);
					this.init(fieldName, value);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		return this.insertForKey();
	}
	
	public T getByFK(Object id) {
		this.reset().list();
		this.andWhere(this.fk + "=" + id.toString());
		this.get();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	public int updateByFK(T object, Object fk) {
		if (!clazz.isAssignableFrom(object.getClass())) {
			throw new RuntimeException("[error]: 修改的类型不匹配。");
		}
		Map<java.lang.reflect.Field, String> fields = tableInfo.getFields();
		this.reset().update();
		Arrays.asList(object.getClass().getDeclaredFields()).forEach(field -> {
			try {
				field.setAccessible(true);
				if (field.isAnnotationPresent(FK.class)) {
					return;
				}
				Object value = field.get(object);
				if (value != null) {
					String fieldName = fields.get(field);
					this.set(fieldName, value);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		this.andWhere(this.fk + "=" + fk);
		return this.execute();
	}
	
	public int deleteByFk(Object id) {
		return this.reset().delete().andWhere(fk + "=" + id).execute();	
	}
}
