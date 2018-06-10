package com.nopeya.delta.test;

import com.nopeya.delta.core.SqlHelper;
import com.nopeya.delta.demo.bean.Book;

public class SqlHelperTest {

	public static void main(String[] args) {
		SqlHelper sqlHelper = new SqlHelper(Book.class);
		System.out.println(sqlHelper.delete().andWhere("code = 2"));
		System.out.println(sqlHelper.reset().update().set("bookname", "as").andWhere("1"));
		System.out.println(sqlHelper.reset().list());
		System.out.println(sqlHelper.reset().init("id", 1).init("bookname", "asd").add());
		System.out.println();
	}
}
