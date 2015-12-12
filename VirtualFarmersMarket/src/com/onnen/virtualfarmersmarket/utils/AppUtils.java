package com.onnen.virtualfarmersmarket.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.FoodItem;

public class AppUtils {
	public static final String DOWNLOAD_LIST_REQ_ID = "304";
	public static final String serverUrl = "http://vfm.lsp.goozmo.com/";
	public static final String APP_PREFERENCES = "AppPrefs";
	public static final String APP_API_KEY = "TFR5f6GTY162DFHD2332GSQU72OOTF";
	public static final String CREATE_ACCOUNT_REQ_ID = "102";
	public static final String LOGIN_REQ_ID = "101";
	public static final String RESET_PASSWORD_REQ_ID = "100";
	public static final String GET_PROFILE_INFO_REQ_ID = "200";
	public static final String SAVE_PROFILE_INFO_REQ_ID = "201";
	
	
	public static ArrayList<HashMap<String, String>> GetData(JSONObject rootObject) throws JSONException {
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		
		String vfmdata = rootObject.getString("vfmData");

		if (vfmdata.equalsIgnoreCase("true")) {
		
			JSONArray jsonarray = rootObject.getJSONArray("vfmLoc");
			 
			for (int i = 0; i < jsonarray.length(); i++) {
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				JSONObject jsonobject = jsonarray.getJSONObject(i);
				 
				 String userId = jsonobject.getString("userId");
				 String userName = jsonobject.getString("userName");
				 String productName = jsonobject.getString("productName");
				 String price = jsonobject.getString("price");
				 String perUnit = jsonobject.getString("perUnit");
				 String desc = jsonobject.getString("desc");
				 String latitude = jsonobject.getString("latitude");
				 String longitude = jsonobject.getString("longitude");
				 String entryId = jsonobject.getString("entryId");
				map.put("userId", userId);
				map.put("userName", userName);
				map.put("productName", productName);
				map.put("price", price);
				map.put("perUnit", perUnit);
				map.put("desc", desc);
				map.put("latitude", latitude);
				map.put("longitude", longitude);
				map.put("entryId", entryId);
				ret.add(map);
				 
			}
		}
		
		return ret;
	}
	
	public static ArrayList<FoodItem> GetFoodList(ArrayList<HashMap<String, String>> data, LatLongToAddress l2a) {
		ArrayList<FoodItem> ret = new ArrayList<FoodItem>();
		
		for(int i=0; i< data.size(); i++) {
			l2a.SetLocation(Double.valueOf(data.get(i).get("latitude")), Double.valueOf(data.get(i).get("longitude")));
			ret.add(new FoodItem(data.get(i).get("entryId"),data.get(i).get("productName"), data.get(i).get("perUnit"), data.get(i).get("price"), l2a.ToAddress(), data.get(i).get("desc"), data.get(i).get("userId"), data.get(i).get("userName")));
		}
		
		return ret;
	}

	public static String BuildImageUrlFromEntryId(int entryId) {
		return BuildImageUrlFromEntryId(String.valueOf(entryId));
	}

	public static String BuildImageUrlFromEntryId(String entryId) {
		return new String(serverUrl + "images/" + entryId + ".jpg");
	}

}
