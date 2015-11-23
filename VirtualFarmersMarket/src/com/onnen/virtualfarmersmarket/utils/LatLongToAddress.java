package com.onnen.virtualfarmersmarket.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class LatLongToAddress {
	
	private Context parentInstance;
	private List<Address> addresses;
	private String address;
	private String city;
	
	public LatLongToAddress(Context parentInstance) {
		// try to convert lat and longitude to address
		this.parentInstance = parentInstance;
	}
	
	public void SetLocation(Double lat, Double longitude) {
		
		Geocoder geocoder;
		try {
			geocoder = new Geocoder(parentInstance, Locale.getDefault());
			addresses = geocoder.getFromLocation(lat, longitude, 1);
			if(addresses.size() > 0 ) {
				address = addresses.get(0).getAddressLine(0);
				city = addresses.get(0).getAddressLine(1);
			}
		} catch (IOException e) {
			address = lat.toString();
			city = longitude.toString();
		}
	}

	private String getAddress() {
		return address;
	}

	private String getCity() {
		return city;
	}

	public String ToAddress() {
		// TODO Auto-generated method stub
		return getAddress() + '\n' + getCity();
	}
}
