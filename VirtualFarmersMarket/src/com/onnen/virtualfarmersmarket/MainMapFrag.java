package com.onnen.virtualfarmersmarket;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainMapFrag extends Fragment{

	private static MainMapFrag singleton;
	private Context mContext;
	private MainMapFrag(Context context) {
		this.mContext = context;
	}

	public static MainMapFrag GetInstance(Context context) {
		if(singleton == null) {
			singleton = new MainMapFrag(context);
		}return singleton;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main_map_frag, container, false);
		
//	    GoogleMap map = ((MapFragment) ((Activity) mContext).getFragmentManager().findFragmentById(R.id.map))
//	            .getMap();
	    
		return rootView;
	}
}
