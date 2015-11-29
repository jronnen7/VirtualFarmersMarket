package com.onnen.virtualfarmersmarket;

import com.onnen.virtualfarmersmarket.utils.CacheingEngine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
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
		
		return ret;
	}

	private void GetProfileInfoFromServer() {
		// TODO Auto-generated method stub	
	}
	
	private void PostToServer() {
		// TODO Auto-generated method stub		
	}

	public static void SetNull() {
		singleton = null;
	}
	

}


