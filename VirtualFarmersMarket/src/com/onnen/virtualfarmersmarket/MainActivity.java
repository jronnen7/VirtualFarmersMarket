package com.onnen.virtualfarmersmarket;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationListener {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor editor;	
	private CharSequence mTitle;
	private CacheingEngine cache;
	private LocationManager m_lm;
	private FragMenuTracker menu;
	
	private MainListFrag mainListView;
	private MainMapFrag mainMapView;
	private EditProfileFrag editProfileView;
	private SellAnItemFrag sellAnItemView;
	
	private boolean canExit;
	
	public MainActivity() {
		menu = new FragMenuTracker(3);
		canExit = false;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		m_lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		m_lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		cache = CacheingEngine.getInstance();

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		Fragment f = null;
		if(position == 0) {
			mainListView = MainListFrag.GetInstance(this);
			f = mainListView;
		}else if(position == 1) {
			mainMapView = MainMapFrag.GetInstance();
			f = mainMapView;
		}else if(position == 2) {
			editProfileView = EditProfileFrag.GetInstance();
			f = editProfileView;
		}else if(position == 3) {
			sellAnItemView = SellAnItemFrag.GetInstance(this);
			f = sellAnItemView;
		}
		
		if(f != null) {
			menu.Track(f);
			fragmentManager.beginTransaction().replace(R.id.container, f )
			.commit();
		}
		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onLocationChanged(Location location) {
		Double latitude = location.getLatitude();
		Double longitude = location.getLongitude();
		cache.Add("latitude", latitude);
		cache.Add("longitude", longitude);
		if(sharedPrefs != null) {
			editor = sharedPrefs.edit();
			editor.putString("latitude", latitude.toString());
			editor.putString("longitude", longitude.toString());
			editor.commit();
		}
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }
	@Override
	public void onProviderEnabled(String provider) { }
	@Override
	public void onProviderDisabled(String provider) { }

	
	@Override
	public void onBackPressed() {
		Fragment f = menu.BackTrack();
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if(f != null) {
			fragmentManager.beginTransaction().replace(R.id.container, f )
			.commit();
		}else if(canExit) {
		 	canExit = false;
		 	moveTaskToBack(true);
		}else  {
			Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_LONG).show();
			canExit = true;
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
			   public void run() {
				   canExit = false;
			   }
			}, 5000);
		}
	}

}
