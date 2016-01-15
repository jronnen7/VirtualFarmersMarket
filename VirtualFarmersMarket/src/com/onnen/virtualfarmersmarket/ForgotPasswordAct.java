package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.HttpPost;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordAct extends Activity implements OnClickListener {

	private Button submit;
	private EditText email;
	
	private ServiceHandler mServer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		String emailStr = i.getStringExtra("email");
		
		setContentView(R.layout.forgot_password_act);
		InitView();
		
		email.setText(emailStr);
	
	}

	private void InitView() {
		mServer = new ServiceHandler();
		
		submit = (Button) findViewById(R.id.forgot_password_submit);
		email = (EditText) findViewById(R.id.forgot_password_email);
		
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == submit.getId()) {
			/* TODO */
			boolean isValidEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches();
			if(!isValidEmail) {
				email.setError(null);
				email.setError("Please enter valid email");
				email.requestFocus();
			} else {
				List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
				parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.RESET_PASSWORD_REQ_ID));
				parametersList.add(new Pair<String,String>("vfmUserEmail", email.getText().toString()));

				new HttpPost(new ResetPasswordHandler()).execute(parametersList);
			}
		}
		
	}
	
	private class ResetPasswordHandler implements IResultHandler{
		@Override
		public int onResult(String result) {
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
					if(rootObject.getString("vfmData").equalsIgnoreCase("true")) {
						// let the user know to check email
						Toast.makeText(ForgotPasswordAct.this, "Please Check your email for your new password!", Toast.LENGTH_LONG).show();
						Intent i = new Intent(ForgotPasswordAct.this, LoginAct.class);
						i.putExtra("email", email.getText().toString());
						startActivity(i);
					} else {
						Toast.makeText(ForgotPasswordAct.this, "We could not find an email on file, please create an account.", Toast.LENGTH_LONG).show();
						
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
						   public void run() {
							   Intent i = new Intent(ForgotPasswordAct.this, CreateAccountAct.class);
							   i.putExtra("email", email.getText().toString());
							   startActivity(i);
						   }
						}, 1100);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return 0;
		}

		@Override
		public void onError(int resultError) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
