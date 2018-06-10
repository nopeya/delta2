package com.nopeya.delta.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据获取中间件
 * @author NopeYa
 * 2017年7月22日
 */
public class DataGetter {
	/**
	 * 根据SQL语句和参数获取列表， 需实现一个结果处理器
	 * @param sql
	 * @param processor
	 * @param params
	 */
	public void list(String sql, Processor processor, Object...params) {
		System.out.println("[Delta]: do sql:" + sql);
		DbHelper helper = new DbHelper();
		try(ResultSet rs = helper.select(sql, params)) {
			processor.process(rs);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			helper.close();
		}
	}
	/**
	 * 结果处理器， 对返回的结果集进行处理
	 */
	public interface Processor {
		public void process(ResultSet r);
	}
}