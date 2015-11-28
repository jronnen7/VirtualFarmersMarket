package com.onnen.virtualfarmersmarket;

import com.onnen.virtualfarmersmarket.utils.AppUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginAct extends Activity {

	private SharedPreferences sharedPrefs;
	private Editor editor;
	private String userName;
	private String password;
	
	EditText userNameEditText;
	EditText passwordEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);
	
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
//		editor = sharedPrefs.edit();
//		editor.putString("userName", "value");
//		editor.putString("password", "value");		
//		editor.commit();		
		
		
		Button loginButton = (Button) findViewById(R.id.login_act_login_button); 
		userNameEditText = (EditText) findViewById(R.id.login_act_user_name_edit_text);
		passwordEditText = (EditText) findViewById(R.id.login_act_password_edit_text);
		
		
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = userNameEditText.getText().toString();
				password = passwordEditText.getText().toString();
				SendServerUserNameAndPassword();
			}

			private void SendServerUserNameAndPassword() {
				/* TODO */			
				// for now lets just start the main activity
				StartMainActivity();
			}
		});
		
	}
	
	private void StartMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}
}
