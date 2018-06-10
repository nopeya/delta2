package com.nopeya.delta.core;

import java.util.ResourceBundle;

public class Config {
	private transient ResourceBundle config;
	
	public Config(String location) {
		config = ResourceBundle.getBundle(location);
	}
	
	public String get(String key) {
		if (null != config) {
			return config.getString(key);
		}
		throw new RuntimeException("Error config. Check the configuration. key:" + key);
	}
}
