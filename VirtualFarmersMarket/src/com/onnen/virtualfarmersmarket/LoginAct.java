package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
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
			SendServerUserNameAndPassword();
			
			
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

	private void SendServerUserNameAndPassword() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.LOGIN_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmApiKey", AppUtils.APP_API_KEY));
		parametersList.add(new Pair<String,String>("vfmEmail", userNameEditText.getText().toString()));

		new LoginTask().execute(parametersList);
	}
	
	private class LoginTask extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		private ProgressDialog pd;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(LoginAct.this);
			pd.setTitle("Please wait...");

			pd.show();

		}
		@Override
		protected String doInBackground(List<Pair<String,String>>... params) {
			parameters = params[0];
			return mServiceHandler.makeServiceCall(AppUtils.serverUrl, ServiceHandler.GET,
					parameters);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
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
							SecurePassword pwdEncrypter = SecurePassword.getInstance();
							String dbPassword = pwdEncrypter.DecryptPassword(dbHash);
							if(dbPassword.contentEquals(passwordEditText.getText())) {
								startActivity(new Intent(LoginAct.this, MainActivity.class));
							} else {
								Toast.makeText(LoginAct.this, "Incorrect Password", Toast.LENGTH_LONG).show();
							}
						}
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
					startActivity(new Intent(LoginAct.this, MainActivity.class));
				} catch (Exception e) {
					e.printStackTrace();
					startActivity(new Intent(LoginAct.this, MainActivity.class));
				}
			}

		}
	}
}
