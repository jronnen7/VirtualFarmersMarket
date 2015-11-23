package com.onnen.virtualfarmersmarket.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class CsvStringToMap {

	public CsvStringToMap () {
		
	}
	public ArrayList<HashMap<String,String>> GetMaps(String str) {
		ArrayList<HashMap<String,String>> ret = new ArrayList<HashMap<String, String>>();
		
		String lines[] = str.split("\\r?\\n");
		String keys[] = null;
		if(lines.length > 0) {
			keys = lines[0].split(",");
		}
			
		for(int i=1;i<lines.length;i++) {
			String values[] = lines[i].split(",");
			HashMap<String, String> temp = new HashMap<String,String>();
			for(int j=0;j<keys.length && keys != null;j++) {
				temp.put(keys[j], values[j]);
			}
			ret.add(temp);
		}
		
		return ret;
	}

}
