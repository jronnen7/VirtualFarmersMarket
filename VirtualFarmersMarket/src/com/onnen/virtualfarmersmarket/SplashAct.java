package com.onnen.virtualfarmersmarket;

import java.util.Timer;
import java.util.TimerTask;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashAct extends Activity {

	private SharedPreferences sharedPrefs;
	private String userName;
	private String password;
	
	@Override 
	protected void onResume() {
		super.onResume();
//		StartLoginAct();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_act);	
		
		Init();

		if(userName != null && password != null) {
			SendServerUserNameAndPassword();
		} else {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
			   public void run() {
				   StartLoginAct();
			   }
			}, 1700);
		}
	}

	private void Init() {
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
		String userName = sharedPrefs.getString("userName",null);
		String password = sharedPrefs.getString("password",null);
		
		SetAnyPreviousSingletonsNull();
	}

	private void SetAnyPreviousSingletonsNull() {
		// this is for the logout case if user logs out we need to set
		// these singletons null for various reasons 
		MainMapFrag.SetNull();
		MainListFrag.SetNull();
		EditProfileFrag.SetNull();
		CacheingEngine.SetNull();
	}

	private void SendServerUserNameAndPassword() {
		/* TODO */
	}
	
	private void StartLoginAct() {
		Intent i = new Intent(this,LoginAct.class);
		startActivity(i);
	}
	
	
}
