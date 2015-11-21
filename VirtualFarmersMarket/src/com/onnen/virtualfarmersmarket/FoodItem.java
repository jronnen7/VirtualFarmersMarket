package com.onnen.virtualfarmersmarket;

import android.graphics.Bitmap;

// used to store all the information about one item of food
public class FoodItem { 

	public FoodItem(String name, String perUnit, double price, String location, Bitmap imageFile, String description) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.location = location;
		this.imageFile = imageFile;
		this.perUnit = perUnit;
	}
	
	public String name;
	public String description;
	public String perUnit;
	public Double price;
	public String location;
	public Bitmap imageFile;
	
}
