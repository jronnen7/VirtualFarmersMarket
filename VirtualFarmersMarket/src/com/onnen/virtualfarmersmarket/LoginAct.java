package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.constants.MyResult;
import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.HttpGet;
import com.onnen.virtualfarmersmarket.utils.HttpPost;
import com.onnen.virtualfarmersmarket.utils.SecurePassword;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginAct extends Activity implements OnClickListener {

	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor editor;
	private String userName;
	private String password;
	
	private EditText userNameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private TextView forgotPassword;
	private TextView createAnAccount;
	private boolean canExit;
	
	private ServiceHandler mServiceHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		String emailStr = i.getStringExtra("email");
		
		mServiceHandler = new ServiceHandler();
		
		setContentView(R.layout.login_act);
		InitViews();
		
		if(emailStr!=null) {
			userNameEditText.setText(emailStr);
			passwordEditText.requestFocus();
		}
		
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
		Intent i;
		if(v.getId() == loginButton.getId()) {
			userName = userNameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			SendServerUserName();
			
			
		} else if(v.getId() == forgotPassword.getId()) {
			i = new Intent(LoginAct.this, ForgotPasswordAct.class);
			AddEmail(i);
			startActivity(i);
			
		} else if(v.getId() == createAnAccount.getId()) {
			i = new Intent(LoginAct.this, CreateAccountAct.class);
			AddEmail(i);
			startActivity(i);
		}
	}
	
	private void AddEmail(Intent i) {
		if(userNameEditText.getText().toString() != null) {
			boolean isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(userNameEditText.getText().toString()).matches();
			if(isValidEmail) {
				i.putExtra("email", userNameEditText.getText().toString());
			}
		}
	}

	private void SendServerUserName() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.LOGIN_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmApiKey", AppUtils.APP_API_KEY));
		parametersList.add(new Pair<String,String>("vfmEmail", userNameEditText.getText().toString()));

		new HttpPost(this,new LoginHandler()).execute(parametersList);
	}
	
	private class LoginHandler implements IResultHandler {
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
							Toast.makeText(LoginAct.this, "Incorrect Password", Toast.LENGTH_LONG).show();
						}
						else {
							SecurePassword sp = SecurePassword.getInstance();
							if(sp.Matches(passwordEditText.getText().toString(), dbHash)) {
								startActivity(new Intent(LoginAct.this, MainActivity.class));
								
								editor = sharedPrefs.edit();
								editor.putString("userEmail", userNameEditText.getText().toString());
								editor.putString("password", passwordEditText.getText().toString());
								editor.commit();
							} else {
								Toast.makeText(LoginAct.this, "Incorrect Password", Toast.LENGTH_LONG).show();
							}
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					ret = MyResult.ERROR;
					Toast.makeText(LoginAct.this, "Incorrect Password", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
					ret = MyResult.ERROR;
					Toast.makeText(LoginAct.this, "Incorrect Password", Toast.LENGTH_LONG).show();
				}
			}
			return ret;
		}

		@Override
		public void onError(int resultError) {

		}
	}
}
