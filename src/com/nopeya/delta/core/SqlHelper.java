package com.nopeya.delta.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL语句组装类
 * NopeYa 2017年7月23日
 */
public class SqlHelper {
	/* 组装模式     0 select |  1 insert |  2 update | 3 delete */
	private static final int MODE_SELECT = 0;
	private static final int MODE_INSERT = 1;
	private static final int MODE_UPDATE = 2;
	private static final int MODE_DELETE = 3;
	/* 排序  逆序 DESC | 顺序 ASC */
	public static final int ORDER_DESC = 0;
	public static final int ORDER_ASC = 1;
	/* 初始bean类 */
	private Class<?> clazz; 
	// 当前组装模式
	private int currentMode = 0;
	// 最终SQL语句
	private StringBuffer sql;
	// insert字段初始化集
	private HashMap<String, Object> initors;
	// update的set集
	private HashMap<String, String> sets;
	// where 条件集
	private List<String> wheres;
	// 排序字段集  <field, 0|1>
	private HashMap<String, String> orders;
	// 是否分页
	private boolean isLimit = false;
	// 分页偏移量和行数
	private int offset=0, rows=0;
	/**
	 * 初始化
	 * @param bean
	 */
	public SqlHelper(Class<?> bean) {
		this.clazz = bean;
		this.sql = new StringBuffer();
		this.wheres = new ArrayList<String>();
		this.sets = new HashMap<String, String>();
		this.initors = new HashMap<String, Object>();
		this.orders = new HashMap<String, String>();
	}
	/**
	 * 查询
	 * @return
	 */
	public SqlHelper list() {
		this.setMode(MODE_SELECT);
		return this;
	}
	/**
	 * 添加
	 * @return
	 */
	public SqlHelper add() {
		this.setMode(MODE_INSERT);
		return this;
	}
	/**
	 * 更新
	 * @return
	 */
	public SqlHelper update() {
		this.setMode(MODE_UPDATE);
		return this;
	}
	/**
	 * 删除
	 * @return
	 */
	public SqlHelper delete() {
		this.setMode(MODE_DELETE);
		return this;
	}
	/**
	 * insert子句里的字段和值构造器
	 * @param field
	 * @param value
	 * @return
	 */
	public SqlHelper init(String field, Object value){
		if (isField(field)) {
			initors.put(field, value);
		} else {
			throw new RuntimeException("'" + getTable() + "'表内未找到字段'" + field + "', 请检查拼写是否正确.");
		}
		return this;
	}
	/**
	 * set子句
	 * @param set
	 * @return
	 */
	public SqlHelper set(String field, Object value) {
		if (!isField(field)) {
			throw new RuntimeException("'" + getTable() + "'表内未找到字段'" + field + "', 请检查拼写是否正确.");
		} else {
			this.sets.put(field, "'" + value + "'");
		}
		return this;
	}
	/**
	 * where子句
	 * @param where
	 * @return
	 */
	public SqlHelper andWhere(String where) {
		if (null != where && !"".equals(where.trim())) {
			wheres.add(" and " + where);
		}
		return this;
	}
	
	public SqlHelper orWhere(String where) {
		if (null != where && !"".equals(where.trim())) {
			wheres.add(" or " + where);
		}
		return this;
	}
	/**
	 * 添加排序字段
	 * @param field
	 * @param o
	 * @return
	 */
	public SqlHelper orderBy(String field, int o) {
		if (!isField(field)) {
			throw new RuntimeException("'" + getTable() + "'表内未找到字段'" + field + "', 请检查拼写是否正确.");
		} else if (o != SqlHelper.ORDER_ASC && o != SqlHelper.ORDER_DESC) {
			throw new RuntimeException("'未找到排序定义'" + o + "', 请检查值.");
		} else {
			this.orders.put(field, o == SqlHelper.ORDER_ASC ? "asc" : "desc");
		}
		return this;
	}
	
	/**
	 * 分页
	 * @param offset 偏移量
	 * @param rows 行数
	 * @return
	 */
	public SqlHelper limit(int offset, int rows) {
		this.rows = rows;
		this.offset = offset;
		this.isLimit = true;
		return this;
	}
	/**
	 * 清除构造器使用记录
	 * @return
	 */
	public SqlHelper reset() {
		this.sql = new StringBuffer("");
		this.wheres.clear();
		this.sets.clear();
		this.initors.clear();
		this.orders.clear();
		this.isLimit = false;
		return this;
	}
	@Override 
	public String toString() {
		this.sql = new StringBuffer();
		switch (currentMode) {
			case MODE_SELECT:
				generateList();
				break;
			case MODE_INSERT:
				generateInsert();
				break;
			case MODE_UPDATE:
				generateUpdate();
				break;
			case MODE_DELETE:
				generateDelete();
				break;
			default:
				break;
		}
		return sql.toString();
	}
	/**
	 * 设置当前组装模式
	 * @param mode
	 */
	private void setMode(int mode) {
		if (this.currentMode != mode) {
			this.sql = new StringBuffer();
			this.currentMode = mode;
		}
	}
	/**
	 * 生成select语句
	 */
	private void generateList() {
		sql.append("select ");
		sql.append(getFields().values().stream().collect(Collectors.joining(", ")));
		sql.append(" from " + getTable());
		sql.append(" where 1 = 1");
		wheres.forEach(sql::append);
		if (orders.size() > 0) {
			List<String> orderList = new ArrayList<String>();
			orders.forEach((x, y) -> {
				orderList.add(x + " " + y);
			});
			sql.append(" order by ");
			sql.append(orderList.stream().collect(Collectors.joining(", ")));
		}
		if (isLimit) {
			sql.append(" limit " + this.offset + ", " + this.rows);
		}
	}
	/**
	 * 生成insert语句
	 */
	private void generateInsert() {
		if (initors.size() > 0) {
			sql.append("insert into " + getTable());
			sql.append(" (" + initors.keySet().stream().collect(Collectors.joining(", ")) + ")");
			sql.append(" values (");
			sql.append(initors.values().stream().map(x-> "'" + x + "'").collect(Collectors.joining(", ")));
			sql.append(")");
		} else throw new RuntimeException("insert语句键值对构造器为空, 请添加initors.");
	}
	/**
	 * 生成update语句
	 */
	private void generateUpdate() {
		if (wheres.size() > 0 && sets.size() > 0 ) {
			List<String> setList = new ArrayList<String>();
			sets.forEach((x, y) -> {
				setList.add(x + " = " + y);
			});
			sql.append("update " + getTable() + " set ");
			sql.append(setList.stream().collect(Collectors.joining(", ")));
			sql.append(" where 1 = 1");
			wheres.forEach(sql::append);
		} else throw new RuntimeException("update语句缺少where条件或set语句, 请检查.");
	}
	/**
	 * 生成delete语句
	 */
	private void generateDelete() {
		if (wheres.size() > 0) {
			sql.append("delete from " + getTable());
			sql.append(" where 1 = 1");
			wheres.forEach(sql::append);
		} else throw new RuntimeException("delete语句缺少where条件, 请添加.");
	}
	/**
	 * 获取表名
	 * @param clazz
	 * @return
	 */
	private String getTable() {
		return Scanner.tableList.get(clazz).getTable();
	}
	/**
	 * 获取主键
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getFK() {
		return Scanner.tableList.get(clazz).getFk();
	}
	/**
	 * 获取字段集
	 * @return
	 */
	private Map<Field, String> getFields() {
		return Scanner.tableList.get(clazz).getFields();
	}

	/**
	 * 判断是否是字段
	 * @param field
	 * @return
	 */
	private boolean isField(String field) {
		if (getFields().isEmpty()) {return false;}
		for (String value : getFields().values()){
			if (value.equals(field)){
				return true;
			}
		}
		return false;
	}
}
