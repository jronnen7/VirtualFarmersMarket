package com.onnen.virtualfarmersmarket;

import android.graphics.Bitmap;

// used to store all the information about one item of food
public class FoodItem { 

	public FoodItem(String entryId, String name, String perUnit, String price, String location, Bitmap imageFile, String description) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.location = location;
		this.imageFile = imageFile;
		this.perUnit = perUnit;
		this.entryId = entryId;
	}
	
	public FoodItem(String entryId, String name, String perUnit, String price, String location, String description, String userId, String userName) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.location = location;
		this.imageFile = null;
		this.perUnit = perUnit;
		this.entryId = entryId;
		this.userId = userId;
		this.userName = userName;
	}

	public String name;
	public String description;
	public String perUnit;
	public String price;
	public String location;
	public String entryId;
	public String userId;
	public String userName;

	public Bitmap imageFile;
	
}
