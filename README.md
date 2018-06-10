# delta
  delta是一个简单的ORM组件，主要用于基于Java注解完成对资源的映射，另外还封装了一些基本的JDBC操作。
  
## 使用准备
  1. 将delta-x-x.jar文件拷贝到项目WebRoot/WEB-INF/lib目录下  
  2. 在src根目录下创建properties配置文件
  
## 使用
  1. 创建POJO，使用注解标注表属性
  
   ```java
@Table
public class Book {
	@FK @Field private int id;
	@Field private String bookname;
	@Field private String isbn;
	@Field private float price;
}
   ```
   
  2. 创建Dao文件
  
  ```java
public class BookDao extends BaseDao<Book> {}
  ```
  
  3. 使用BookDao操作Book资源
  
    ```java
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
	}
    ```
