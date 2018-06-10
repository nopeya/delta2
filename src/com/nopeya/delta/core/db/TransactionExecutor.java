package com.nopeya.delta.core.db;
/**
 * 事务处理器
 * NopeYa 2017年7月26日
 */
public class TransactionExecutor {
	/**
	 * 执行一次事务
	 * @param sqls
	 */
	public void transaction(String...sqls) {
		new DbHelper().transaction(sqls);
	}
}
