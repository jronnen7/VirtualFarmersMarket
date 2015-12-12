package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.SecurePassword;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateAccountAct extends Activity {

	private Button submitButton;
	private CheckBox acceptTermsCheckbox;
	private TextView viewTerms;
	private TextView acceptTermsText;
	private EditText email;
	private EditText firstName;
	private EditText lastName;
	private EditText password;
	private EditText verifiedPassword;
	
	private PasswordValidator validator;
	
	private ServiceHandler mServiceHandler;
	private Dialog d;

	
	private String hashedPassword;
	private SharedPreferences sharedPrefs;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		String emailStr = i.getStringExtra("email");
		setContentView(R.layout.create_account_act);
		InitViews();
		email.setText(emailStr);
		sharedPrefs = getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
	}

	private void InitViews() {
		validator = new PasswordValidator();
		mServiceHandler = new ServiceHandler();
		submitButton = (Button) findViewById(R.id.create_account_submit_button);
		acceptTermsCheckbox = (CheckBox) findViewById(R.id.create_account_accept_terms_checkbox);
		acceptTermsText = (TextView) findViewById(R.id.create_account_accept_terms_text_view);
		email = (EditText) findViewById(R.id.create_account_email);
		firstName = (EditText) findViewById(R.id.create_account_first_name);
		lastName = (EditText) findViewById(R.id.create_account_last_name);
		password = (EditText) findViewById(R.id.create_account_password);
		verifiedPassword = (EditText) findViewById(R.id.create_account_verified_password);
		viewTerms = (TextView) findViewById(R.id.create_account_view_terms);
		
		CreateAccountClickListener l = new CreateAccountClickListener();
		submitButton.setOnClickListener(l);
		viewTerms.setOnClickListener(l);
		acceptTermsText.setOnClickListener(l);
	}
	
	private boolean ValidInformation() {
		boolean ret = true;
		
		if(firstName.getText().toString().matches("")) {
			firstName.setError(null);
			firstName.setError("Please enter your first name");
			firstName.requestFocus();
			ret = false;
		}else if(lastName.getText().toString().matches("")) {
			lastName.setError(null);
			lastName.setError("Please enter your last name");
			lastName.requestFocus();
			ret = false;
		}else if(email.getText().toString().matches("")) {
			email.setError(null);
			email.setError("Please enter email");
			email.requestFocus();
			ret = false;
		}else if(!validator.IsPasswordValid(
				password.getText().toString(), 
				verifiedPassword.getText().toString())) {
			password.setError(null);
			password.setError(validator.GetErrorString());
			password.requestFocus();
			ret = false;
		}else if(!acceptTermsCheckbox.isChecked()) {
			acceptTermsText.setError(null);
			acceptTermsText.setError("Please accept terms and agreement");
			ret = false;
		} 
		
		// finally lets validate the email we already checked for null
		if(ret == true) {
			ret = android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches();
			if(ret != true) {
				email.setError(null);
				email.setError("Please enter valid email");
				email.requestFocus();
			}
		}
			
		return ret;
	}
	
	private class CreateAccountClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.create_account_submit_button:
				if(ValidInformation()) {
					PostInfoToServer();
				}
				break;
			case R.id.create_account_view_terms:
				d = new Dialog(CreateAccountAct.this);
				d.setContentView(R.layout.terms_and_conditions_dialog);
				d.setTitle("Terms & Conditions");
				d.setCancelable(true);
				Button okButton = (Button) d.findViewById(R.id.terms_and_conditions_dialog_submit_button);
				okButton.setOnClickListener(this);
				d.show();
				break;
			case R.id.terms_and_conditions_dialog_submit_button:
				d.dismiss();
				d = null;
				break;
			case R.id.create_account_accept_terms_text_view:
				acceptTermsCheckbox.setChecked(!acceptTermsCheckbox.isChecked());
				break;
			default:
				break;
			}
		}
	}
	


	private void PostInfoToServer() {
		SecurePassword pwdEncrypter = SecurePassword.getInstance();
		try {
			hashedPassword = pwdEncrypter.EncryptPassword(password.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.CREATE_ACCOUNT_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmApiKey", AppUtils.APP_API_KEY));
		
		parametersList.add(new Pair<String,String>("vfmUserName", lastName.getText().toString() + ":" + firstName.getText().toString()));
		parametersList.add(new Pair<String,String>("vfmEmail", email.getText().toString()));
		parametersList.add(new Pair<String,String>("vfmPassword", hashedPassword));

		new CreateAccountUploader().execute(parametersList);
	}
	
	
	private class CreateAccountUploader extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		private ProgressDialog pd;
		private Dialog d;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(CreateAccountAct.this);
			pd.setTitle("Creating your Account");
			pd.setMessage("Please wait...");
			pd.show();
		}
		@Override
		protected String doInBackground(List<Pair<String,String>>... params) {
			parameters = params[0];
			return mServiceHandler.makeServiceCall(AppUtils.serverUrl, ServiceHandler.POST,
					parameters);
		}

		/*
		 * INSERT INTO accounts (userid,name,email,password, joindate) 
		 * VALUES('56667164b0526','Onnen,Jared','jared@goozmo.com','phWd4yLhdyAE8L6iQj9Jyw==', '12/08/15 0:57.56')
		 * */
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
					if(rootObject.getString("vfmData").equalsIgnoreCase("true")) {
	
						String userId=rootObject.getString("vfmUserId");		
						if(!userId.equalsIgnoreCase("-1")) {
							
							editor = sharedPrefs.edit();
							editor.putString("userId", userId);
							editor.putString("password", hashedPassword);
							editor.putString("firstName", firstName.getText().toString());
							editor.putString("lastName", lastName.getText().toString());
							
							editor.commit();	
							
							
							startActivity(new Intent(CreateAccountAct.this, MainActivity.class));
						} else {
							Toast.makeText(CreateAccountAct.this, "Account Already found.", Toast.LENGTH_LONG).show();
							
					       	d = new Dialog(CreateAccountAct.this); // create dialog
							d.setContentView(R.layout.create_account_dialog); // set background
							
							Button loginButton = (Button) d.findViewById(R.id.create_account_dialog_login_button);
							Button forgotPassword = (Button) d.findViewById(R.id.create_account_dialog_forgot_password);
							
							loginButton.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent i = new Intent(CreateAccountAct.this, LoginAct.class);
									i.putExtra("email", email.getText().toString());
									startActivity(i);
									d.dismiss();
								}
							});
							forgotPassword.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Intent i = new Intent(CreateAccountAct.this, ForgotPasswordAct.class);
									i.putExtra("email", email.getText().toString());
									startActivity(i);
									d.dismiss();
								}
							});
							d.show();
						}
					}

					pd.dismiss();
				} catch (JSONException e) {
					e.printStackTrace();
					pd.dismiss();
				}
			}
		}
	}

}
