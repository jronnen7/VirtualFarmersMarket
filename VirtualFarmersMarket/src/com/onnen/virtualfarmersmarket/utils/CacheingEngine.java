package com.onnen.virtualfarmersmarket.utils;

import java.util.HashMap;

public class CacheingEngine {
	private static CacheingEngine instance;

    private HashMap<String,byte[]> list;
    private HashMap<String,Double> doubleList;
	public static CacheingEngine getInstance() {
		if (instance == null) {
		  instance = new CacheingEngine();
		}
		return instance;
	 }

	 private CacheingEngine() {
		 list = new HashMap<String, byte[]>();
		 doubleList = new HashMap<String, Double>();
	 }

	public synchronized void Add(String key, byte[] value) {
		list.put(key, value);	
	}
	public synchronized void Add(String key, double value) {
		doubleList.put(key, value);	
	}
	
	
	public byte[] Get(String key) {
		return list.get(key);	
	}
	public double GetDouble(String key) {
		return doubleList.get(key);	
	}

	public static void SetNull() {
		if(instance!=null) {
			instance = null;
		}
	}

	 

}
