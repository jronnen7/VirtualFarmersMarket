package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;
import com.onnen.virtualfarmersmarket.utils.HttpPost;
import com.onnen.virtualfarmersmarket.utils.MyResult;
import com.onnen.virtualfarmersmarket.utils.SecurePassword;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

public class SplashAct extends Activity {

	private SharedPreferences sharedPrefs;
	private String userEmail;
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
		SendServerUserName();
		
	}

	private void Init() {
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
		userEmail = sharedPrefs.getString("userEmail",null);
		password = sharedPrefs.getString("password",null);
		
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

	private void SendServerUserName() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.LOGIN_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmApiKey", AppUtils.APP_API_KEY));
		parametersList.add(new Pair<String,String>("vfmEmail", userEmail));

		new HttpPost(new SplashServerResult()).execute(parametersList);
	}
	
	private void StartLoginAct() {
		Intent i = new Intent(this,LoginAct.class);
		startActivity(i);
	}
	
	private class SplashServerResult implements IResultHandler {

		@Override
		public int onResult(String result) {
			int ret = MyResult.RESULT_OK;
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
					if(rootObject.getString("vfmData").equalsIgnoreCase("true")) {
						String dbHash = rootObject.getString("vfmKeyValue");
						if(dbHash.equalsIgnoreCase("null")) {
							startActivity(new Intent(SplashAct.this, LoginAct.class));
						}
						else {
							SecurePassword sp = SecurePassword.getInstance();
							if(sp.Matches(password, dbHash)) {
								startActivity(new Intent(SplashAct.this, MainActivity.class));
							} else {
								startActivity(new Intent(SplashAct.this, LoginAct.class));
							}
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					ret = MyResult.ERROR;
					startActivity(new Intent(SplashAct.this, LoginAct.class));
				} catch (Exception e) {
					e.printStackTrace();
					ret = MyResult.ERROR;
					startActivity(new Intent(SplashAct.this, LoginAct.class));
				}
			}
			return ret;
		}

		@Override
		public void onError(int resultError) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}
