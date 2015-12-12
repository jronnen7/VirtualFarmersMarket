package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditProfileFrag extends Fragment implements OnClickListener {

	private static EditProfileFrag singleton;
	
	private EditText email;
	private EditText firstName;
	private EditText lastName;
	private Button submitButton;
	private TextView editProfileImageText;
	private ImageView profileImage;
	
	private PopupMenu popupMenu;
	private ServiceHandler mServer;
	
	private EditProfileFrag() {

	}

	public static EditProfileFrag GetInstance() {
		if(singleton == null) {
			singleton = new EditProfileFrag();
		}return singleton;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = null;
		rootView = inflater.inflate(R.layout.edit_profile_frag, container, false);
		InitView(rootView);
		return rootView;
	}
	
	private void InitView(View v) {
		mServer = new ServiceHandler();
		
		email = (EditText) v.findViewById(R.id.edit_profile_email);
		firstName = (EditText) v.findViewById(R.id.edit_profile_first_name);
		lastName = (EditText) v.findViewById(R.id.edit_profile_last_name);
		submitButton = (Button) v.findViewById(R.id.edit_profile_submit_button);
		editProfileImageText = (TextView) v.findViewById(R.id.edit_profile_edit_image_text);
		profileImage = (ImageView) v.findViewById(R.id.edit_profile_edit_image);
		
		editProfileImageText.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		profileImage.setOnClickListener(this);
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		GetProfileInfoFromServer();
	}

	@Override
	public void onClick(View v) {
		if((v.getId() == editProfileImageText.getId())
				|| (v.getId() == profileImage.getId())) {
			popupMenu = new PopupMenu(getActivity(), v);
			popupMenu.getMenuInflater().inflate(R.menu.edit_profile_edit_picture_dropdown, popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if(item.getTitle().toString().equalsIgnoreCase("From Camera")) {
						/* TODO */
					}else if(item.getTitle().toString().equalsIgnoreCase("From Gallery")) {
						/* TODO */
					}
					popupMenu.dismiss();
					return true;
				}
			});
			popupMenu.show();
		} else if(v.getId() == submitButton.getId()) {
			if(IsValid()) {
				PostToServer();
			}
		}
		
	}
	
	private boolean IsValid() {
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


	private void GetProfileInfoFromServer() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.GET_PROFILE_INFO_REQ_ID));
		new GetProfileInfoTask().execute(parametersList);
	}
	
	
	private class GetProfileInfoTask extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(List<Pair<String,String>>... params) {
			parameters = params[0];
			return mServer.makeServiceCall(AppUtils.serverUrl, ServiceHandler.GET,
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
	
	
	
	
	private void PostToServer() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.SAVE_PROFILE_INFO_REQ_ID));
		new SaveProfileInfoTask().execute(parametersList);
	}
	
	
	private class SaveProfileInfoTask extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(List<Pair<String,String>>... params) {
			parameters = params[0];
			return mServer.makeServiceCall(AppUtils.serverUrl, ServiceHandler.POST,
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

	public static void SetNull() {
		singleton = null;
	}
	

}


