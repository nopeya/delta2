package com.nopeya.delta.core.db;

import java.sql.SQLException;

/**
 * 数据增删改中间层
 * NopeYa 2017年7月25日
 */
public class DataSetter {
	/**
	 * 根据SQL语句和可选参数列表, 执行insert|update|delete操作
	 * @param sql
	 * @param params
	 * @return
	 */
	public int execute(boolean needKey, String sql, Object...params) {
		System.out.println("[Delta]: do sql:" + sql);
		int r = 0;
		DbHelper helper = new DbHelper();
		try {
			if (needKey) {
				r = helper.updateAndReturnKey(sql, params);
			} else {
				r = helper.update(sql, params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			helper.close();
		}
		return r;
	}
}
