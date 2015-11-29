package com.onnen.virtualfarmersmarket;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainMapFrag extends Fragment{

	private static MainMapFrag singleton;
	private GoogleMap map;
	private CacheingEngine cache;
	private MainMapFrag() {
		this.cache = CacheingEngine.getInstance();
	}

	public static MainMapFrag GetInstance() {
		if(singleton == null) {
			singleton = new MainMapFrag();
		}return singleton;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = null;
		rootView = inflater.inflate(R.layout.main_map_frag, container, false);
		return rootView;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_google_map);
		map = mapFrag.getMap();
		map.setMyLocationEnabled(true);
		
		Double lat = cache.GetDouble("latitude");
		Double longitude = cache.GetDouble("longitude");
		if(lat != null && longitude != null) {
			LatLng point = new LatLng(lat, longitude);
			if(point != null) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,10));
			}
		}
	}

	public static void SetNull() {
		if(singleton != null) {
			singleton = null;
		}
	}
}
