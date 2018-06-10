package com.nopeya.delta.core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.nopeya.delta.core.Config;

public class DbHelper {
	private static Config config = new Config("db");
	
	private final static String className = config.get("className").trim();
	private final static String url = config.get("url").trim();
	private final static String user = config.get("user").trim();
	private final static String password = config.get("password").trim();
	private static Connection conn;
	private static Statement stat;
	private static PreparedStatement ppStat;
	private static ResultSet resultSet;
	
	/**
	 * 加载驱动
	 */
	static {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接
	 */
	public void connect() {
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 简单查询
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public ResultSet select(String sql, Object...params) throws SQLException {
		resultSet = null;
		connect();
		ppStat = conn.prepareStatement(sql);
		if (params != null) {
			int i = 1;
			for (Object param : params) {
				ppStat.setObject(i++, param);
			}
		}
		resultSet = ppStat.executeQuery();
		return resultSet;
	}
	
	/**
	 * 简单更新
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException 
	 */
	public int update(String sql, Object...params) throws SQLException {
		int r = 0;
		connect();
		ppStat = conn.prepareStatement(sql);
		if (params != null) {
			int i = 1;
			for (Object param : params) {
				ppStat.setObject(i++, param);
			}
		}
		r = ppStat.executeUpdate();
		return r;
	}
	/**
	 * 获取新插入的key
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public int updateAndReturnKey(String sql, Object...params) throws SQLException {
		int r = 0;
		connect();
		ppStat = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		if (params != null) {
			int i = 1;
			for (Object param : params) {
				ppStat.setObject(i++, param);
			}
		}
		ppStat.execute();
		ResultSet rs = ppStat.getGeneratedKeys();
		if (rs != null&&rs.next()) {  
		    r=rs.getInt(1);  
		} 
		return r;
	}
	
	/**
	 * 简单事务
	 * @param sqls
	 */
	public void transaction(String...sqls){
		if (sqls != null) {
			connect();
			try {
				conn.setAutoCommit(false);
				stat = conn.createStatement();
				Arrays.asList(sqls).forEach(x -> {
						try {
							stat.execute(x);
						} catch (SQLException e) {
							e.printStackTrace();
						}
				});
				conn.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
				try {
					conn.rollback();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	/**
	 * 释放资源
	 * @param items
	 */
	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (ppStat != null) {
				ppStat.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}	
	}
}
