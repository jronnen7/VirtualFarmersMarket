package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
	private HashMap<Marker, MyMarkerData> markersData;
	
	private MainMapFrag() {
		markersData = new HashMap<Marker, MyMarkerData>();
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
		
		
		setUpMapMarkerListener();
		
		ArrayList<MyMarkerData> tempMarkerData = new ArrayList<MyMarkerData>();

		tempMarkerData.add(new MyMarkerData("Brasil", null, Double.parseDouble("39.971390"), Double.parseDouble("-105.495507")));
		tempMarkerData.add(new MyMarkerData("United States", null, Double.parseDouble("39.976179"), Double.parseDouble("-105.260379")));
		tempMarkerData.add(new MyMarkerData("Canada", null , Double.parseDouble("39.975110"), Double.parseDouble("-105.260379")));
		plotMarkers(tempMarkerData);
	}

	public static void SetNull() {
		if(singleton != null) {
			singleton = null;
		}
	}
	
	
	 private void setUpMapMarkerListener() {
            if (map != null)  {
            	map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
                    {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }
	 }
	
	
	private void plotMarkers(ArrayList<MyMarkerData> data) {
	    if(data.size() > 0)
	    {
	        for (MyMarkerData iter : data) {

	            // Create user marker with custom icon and other options
	            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(iter.getLatitude(), iter.getLongitude()));
//	            markerOption.icon(BitmapDescriptorFactory.fromBitmap(iter.getIcon()));

	            Marker currentMarker = map.addMarker(markerOption);
	            markersData.put(currentMarker, iter);

	            map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
	        }
	    }
	}
	
	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
	{
	    public MarkerInfoWindowAdapter() {
	    }

	    @Override
	    public View getInfoWindow(Marker marker) {
	        return null;
	    }

	    @Override
	    public View getInfoContents(Marker marker) {
	        View v  = getActivity().getLayoutInflater().inflate(R.layout.map_marker_info, null);

	        MyMarkerData data = markersData.get(marker);

	        // GET DATA AND SET OPTIONS

	        return v;
	    }
	}
}
