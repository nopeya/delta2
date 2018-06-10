package com.nopeya.delta.test;

import java.util.Arrays;

import com.nopeya.delta.demo.BookDao;
import com.nopeya.delta.demo.bean.Book;

public class DaoTest {

	/**
	 * Test
	 * @param args
	 */
	public static void main(String[] args) {
		BookDao dao = new BookDao();
//		dao.list().get().forEach(DaoTest::print);
//		dao.reset().list().andWhere("isbn=?").get("3").forEach(DaoTest::print);
		Book book = new Book();
		book.setIsbn("8875-89876");
		book.setPrice(60);
		int id = dao.insert(book);
		Book newbook = dao.getByFK(id);
		DaoTest.print(newbook);
	}
	
	/**
	 * print
	 * @param bean
	 */
	public static void print(Object bean) {
		Arrays.asList(bean.getClass().getDeclaredFields()).forEach(y -> {
			y.setAccessible(true);
			try {
				System.out.print(String.format("%-20s", y.get(bean)));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});		System.out.println();
	}
}
