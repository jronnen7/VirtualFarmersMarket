package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.app.Activity;
import android.app.Dialog;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_act);
		InitViews();
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
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.CREATE_ACCOUNT_REQ_ID));
		new CreateAccountUploader().execute(parametersList);
	}
	
	
	private class CreateAccountUploader extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(List<Pair<String,String>>... params) {
			parameters = params[0];
			return mServiceHandler.makeServiceCall(AppUtils.serverUrl, ServiceHandler.POST,
					parameters);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
//					ArrayList<HashMap<String,String>> data = AppUtils.GetData(rootObject);
					
				} catch (JSONException e) {
					e.printStackTrace();

				}
			}
		}
	}

}
