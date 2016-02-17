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
import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainMapFrag extends Fragment{

	private static MainMapFrag singleton;
	private SharedPreferences sharedPrefs;
	private GoogleMap map;
	private CacheingEngine cache;
	private HashMap<Marker, MyMarkerData> markersData;
	private LinearLayout farmDataLayout, farmDataWrapper;
	private TextView desc;
	private Animation animHideFarmData, animShowFarmData, animFarmTitleShow, animFarmTitleHide;
	private RelativeLayout farmTitle;
	private Boolean isDataShown;
	private MainMapFrag() {
		markersData = new HashMap<Marker, MyMarkerData>();
		this.cache = CacheingEngine.getInstance();	
		isDataShown = false;
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
		Init();
		SetListeners();
		
		// get markers for now we are faking them
		ArrayList<MyMarkerData> tempMarkerData = new ArrayList<MyMarkerData>();

		tempMarkerData.add(new MyMarkerData("Great Farms", null, Double.parseDouble("39.971390"), Double.parseDouble("-105.495507")));
		tempMarkerData.add(new MyMarkerData("Epic Sensation", null, Double.parseDouble("39.973109"), Double.parseDouble("-105.260379")));
		tempMarkerData.add(new MyMarkerData("Really Un-Organic Produce", null , Double.parseDouble("39.975110"), Double.parseDouble("-105.263379")));
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
	    if(data.size() > 0) {
	        for (MyMarkerData iter : data) {

	            // Create user marker with custom icon and other options
	            MarkerOptions markerOption = new MarkerOptions().position(new LatLng(iter.getLatitude(), iter.getLongitude()));

	            Marker currentMarker = map.addMarker(markerOption);
	            markersData.put(currentMarker, iter);

	            map.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
	        }
	    }
	}
	
	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
	    public MarkerInfoWindowAdapter() {
	    }

	    @Override
	    public View getInfoWindow(Marker marker) {
	    	return null;
	    }

	    @Override
	    public View getInfoContents(Marker marker) {
//	    	farmDataLayout.setVisibility(View.VISIBLE);
	    	farmTitle.startAnimation(animFarmTitleShow);
//	    	farmDataLayout.startAnimation(animShowFarmData);
	        MyMarkerData data = markersData.get(marker);
	        
	        desc.setText(Html.fromHtml("<h2>"+data.getLabel()+"</h2>"));
	    	return null;
	    }
	}
	
	

	private void Init() {
		SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_google_map);
		this.sharedPrefs = getActivity().getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);
		
		map = mapFrag.getMap();
		map.setMyLocationEnabled(true);
		Double lat = null;
		Double longitude = null;
		
		try {
			lat = cache.GetDouble("latitude");
			longitude = cache.GetDouble("longitude");
		} catch (Exception e) {
			String strLat = sharedPrefs.getString("latitude", "");
			String strLong = sharedPrefs.getString("longitude", "");
			lat = Double.valueOf(strLat);
			longitude = Double.valueOf(strLong);
		}
		if(lat != null && longitude != null) {
			LatLng point = new LatLng(lat, longitude);
			if(point != null) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,10));
			}
		}
		
		farmDataWrapper = (LinearLayout) getActivity().findViewById(R.id.main_map_frag_hidden_wrapper);
		farmDataLayout = (LinearLayout) getActivity().findViewById(R.id.map_marker_info);
		animHideFarmData = AnimationUtils.loadAnimation( getContext(), R.anim.popup_hide);
		animHideFarmData.setAnimationListener(new AnimationListener (){

			@Override
			public void onAnimationStart(Animation animation) {
				farmDataWrapper.setVisibility(View.GONE);
				farmDataLayout.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				farmDataWrapper.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			
		});
		animShowFarmData = AnimationUtils.loadAnimation( getContext(), R.anim.popup_show);
		animShowFarmData.setAnimationListener(new AnimationListener (){

			@Override
			public void onAnimationStart(Animation animation) {
				farmDataLayout.setVisibility(View.VISIBLE);
				farmDataWrapper.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) { }

			@Override
			public void onAnimationRepeat(Animation animation) { }
			
		});
		
		farmTitle = (RelativeLayout) getActivity().findViewById(R.id.map_marker_info_title);
		animFarmTitleShow = AnimationUtils.loadAnimation( getContext(), R.anim.popup_show);
		animFarmTitleHide =  AnimationUtils.loadAnimation( getContext(), R.anim.popup_hide);
		animFarmTitleShow.setAnimationListener(new AnimationListener (){

			@Override
			public void onAnimationStart(Animation animation) {
				farmDataWrapper.setVisibility(View.VISIBLE);
				farmTitle.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) { }

			@Override
			public void onAnimationRepeat(Animation animation) { }
			
		});
		animFarmTitleHide.setAnimationListener(new AnimationListener (){

			@Override
			public void onAnimationStart(Animation animation) {
				farmTitle.setVisibility(View.GONE);
				farmDataWrapper.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				farmDataWrapper.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
			
		});
		
		desc = (TextView) getActivity().findViewById(R.id.map_marker_info_desc);
		
	}
	private void SetListeners() {
		ImageView closeBtn = (ImageView) getActivity().findViewById(R.id.map_marker_info_close_btn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isDataShown) {
					farmDataWrapper.startAnimation(animHideFarmData);
					isDataShown = false;
				} else {
					farmTitle.startAnimation(animFarmTitleHide);  
				}
			}
        });
		farmTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				farmDataLayout.startAnimation(animShowFarmData);
				isDataShown = true;
			}
		});
		
		setUpMapMarkerListener();
	}
	



}
