package com.nopeya.delta.core;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.common.utils.ClassUtils;
import javax.common.utils.ClassUtils.Handler;

import com.nopeya.delta.core.annotation.FK;
import com.nopeya.delta.core.annotation.SubResource;
import com.nopeya.delta.core.annotation.Table;
/**
 * 实体类扫描器
 * 	获取bean包里的实体类，并提取表的基本信息
 * NopeYa 2017年7月23日
 */
public class Scanner {
	private static Config config = new Config("db");
	private static String BEAN_PACKAGE_PATH = config.get("beanPath");
	public static HashMap<Class<?>, TableInfo> tableList = new HashMap<Class<?>, TableInfo>();
	/**
	 * 加载实体类信息
	 */
	static {
		List<Class<?>> classList = getClasses(BEAN_PACKAGE_PATH);
		classList.stream()
				.filter((x) -> x.isAnnotationPresent(Table.class))
				// 提取出表信息
				.forEach((x) -> {
					TableInfo tableInfo = new TableInfo();
					tableList.put(x, tableInfo);
					// 设置表名
					String table = x.getAnnotation(Table.class).value();
					if ("".equals(table)) {
						// 如果注解为空，则使用类名作为表名
						tableInfo.setTable(x);
					} else {
						tableInfo.setTable(table);
					}
					// 设置字段和子资源
					Field[] fields = x.getDeclaredFields();
					for (Field field : fields) {
						if (field.isAnnotationPresent(com.nopeya.delta.core.annotation.Field.class)) {
							String fieldName = field.getAnnotation(com.nopeya.delta.core.annotation.Field.class).value().trim();
							if (fieldName.isEmpty()) {
								// 如果注解为空，则使用字段名作为列名
								tableInfo.addField(field);
								if (field.isAnnotationPresent(FK.class)) {
									tableInfo.setFK(field);
								}
							} else {
								tableInfo.addField(field, fieldName);
								if (field.isAnnotationPresent(FK.class)) {
									tableInfo.setFK(fieldName);
								}
							}
						} else if (field.isAnnotationPresent(com.nopeya.delta.core.annotation.SubResource.class)) {
							Class<?> sub = field.getAnnotation(SubResource.class).value();
							tableInfo.addSubResource(sub);
						}
					}
				});
	}
	/**
     * 从包package中获取所有的Class（不考虑jar包情况）
     * @param pack
     * @return
	 * @throws Exception 
     */
    public static List<Class<?>> getClasses(String packageName) {
        //第一个class类的集合
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ClassUtils.scan(packageName, new Handler() {
			
			@Override
			public void handle(File file, String className) {
				try {
					classes.add(Class.forName(className.replaceAll(".class", "")));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				return file.getName().endsWith(".class");
			}
		});
        return classes;
    }
}
