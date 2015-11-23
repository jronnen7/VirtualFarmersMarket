package com.onnen.virtualfarmersmarket.utils;

import java.util.HashMap;

public class CacheingEngine {
	private static CacheingEngine instance;

    private HashMap<String,byte[]> list;
	public static CacheingEngine getInstance() {
		if (instance == null) {
		  instance = new CacheingEngine();
		}
		return instance;
	 }

	 private CacheingEngine() {
		 list = new HashMap<String, byte[]>();
	 }

	public void Add(String key, byte[] value) {
		list.put(key, value);	
	}
	public byte[] Get(String key) {
		return list.get(key);	
	}
	 

}
