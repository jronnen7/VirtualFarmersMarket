package com.onnen.virtualfarmersmarket;

import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.TextView;
import android.widget.Toast;

public class LoginAct extends Activity implements OnClickListener {

	private SharedPreferences sharedPrefs;
	private Editor editor;
	private String userName;
	private String password;
	
	private EditText userNameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private TextView forgotPassword;
	private TextView createAnAccount;
	private boolean canExit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_act);
		InitViews();
		
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
	}
	
	private void InitViews() {
		canExit = false;
		
		loginButton = (Button) findViewById(R.id.login_act_login_button); 
		userNameEditText = (EditText) findViewById(R.id.login_act_user_name_edit_text);
		passwordEditText = (EditText) findViewById(R.id.login_act_password_edit_text);
		createAnAccount = (TextView) findViewById(R.id.login_act_create_account_text_view);
		forgotPassword = (TextView) findViewById(R.id.login_act_forgot_passowrd_text_view);
		
		loginButton.setOnClickListener(this);		
		createAnAccount.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == loginButton.getId()) {
			userName = userNameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			SendServerUserNameAndPassword();
	//		editor = sharedPrefs.edit();
	//		editor.putString("userName", "value");
	//		editor.putString("password", "value");		
	//		editor.commit();	
		} else if(v.getId() == forgotPassword.getId()) {
			startActivity(new Intent(LoginAct.this, ForgotPasswordAct.class));
			
		} else if(v.getId() == createAnAccount.getId()) {
			startActivity(new Intent(LoginAct.this, CreateAccountAct.class));
		}
	}
	
	private void SendServerUserNameAndPassword() {
		/* TODO */			
		// for now lets just start the main activity
		startActivity(new Intent(LoginAct.this, MainActivity.class));
	}
	
	@Override
	public void onBackPressed() {
		 if(canExit) {
			 	canExit = false;
			 	moveTaskToBack(true);
			}else  {
				Toast.makeText(this,"Press back again to exit.", Toast.LENGTH_LONG).show();
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
