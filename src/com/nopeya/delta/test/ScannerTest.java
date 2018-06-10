package com.nopeya.delta.test;

import java.io.File;

import javax.common.utils.ClassUtils;
import javax.common.utils.ClassUtils.Handler;

public class ScannerTest {

	public static void main(String[] args) throws Exception {
		ClassUtils.scan("com.delta", new Handler() {
			
			@Override
			public void handle(File file, String path) {
				System.out.println(path);
			}
			
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".class");
			}
		});
	}

}
