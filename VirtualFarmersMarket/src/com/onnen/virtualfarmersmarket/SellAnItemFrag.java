package com.onnen.virtualfarmersmarket;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onnen.virtualfarmersmarket.constants.MyResult;
import com.onnen.virtualfarmersmarket.constants.RequestCode;
import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;
import com.onnen.virtualfarmersmarket.utils.HttpPost;
import com.onnen.virtualfarmersmarket.utils.LatLongToAddress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SellAnItemFrag extends Fragment {

	private static SellAnItemFrag singleton;
	private Dialog addDialog;
	private TextView addDialogLocationTextView;
	private EditText addDialogPriceEditText;
	private EditText addDialogPerUnitEditText;
	private EditText addDialogNameEditText;
	private EditText addDialogDescriptionEditText;
	private Button addDialogOkButton;
	private Button addDialogCancelButton;
	private ImageView image;
	
	private Context mContext;
	private Bitmap imageBitmap;

	protected LatLongToAddress locationConverter;
	private Double latitude;
	private Double longitude;
	
	private SharedPreferences sharedPrefs;
	
	
	private SellAnItemFrag() {
	}
	private void SetContext(Context context) {
		this.mContext = context;
		locationConverter = null;
		locationConverter = new LatLongToAddress(mContext);
		sharedPrefs = context.getSharedPreferences(AppUtils.APP_PREFERENCES, Context.MODE_PRIVATE);	
	}

	public static SellAnItemFrag GetInstance(Context context) {
		if(singleton == null) {
			singleton = new SellAnItemFrag();
		} 
		singleton.SetContext(context);
		return singleton;
	}

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = null;
		rootView = inflater.inflate(R.layout.sell_an_item_frag, container, false);
		return rootView;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		TakePicture();
	}

	public static void SetNull() {
		if(singleton != null) {
			singleton = null;
		}
	}
	
	
    private void TakePicture() {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    Toast.makeText(mContext, "Take Picture to Add Item to List", Toast.LENGTH_LONG).show();
	    // use this routine to have it call onActivityResult when it is finished with the pic
	    startActivityForResult(intent, RequestCode.REQUEST_PHOTO_ACTIVITY);
	}
	
    // when activity is returened to from camera...
    // this routine can get called several other ways so we need to ensure
    // that the camera routine returned successfully before trying to capture the image

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(requestCode == RequestCode.REQUEST_PHOTO_ACTIVITY) {
	       if(addDialog!= null) {
		       if(addDialog.isShowing()) {
		    	   addDialog.dismiss();
		    	   addDialog = null;
		       }
	       }
	       // activity return successfully (user didn't hit back button to get back from camera)
	       if(data != null) { 
	    	    // make a listener for the location so when the user clicks it the location updates
		       	// make a listener for the add dialog class
		       	DismissAddDialogListener dialogListener = new DismissAddDialogListener();
		       	addDialog = new Dialog(mContext); // create dialog
				addDialog.setContentView(R.layout.add_dialog); // set background
				addDialog.setCancelable(true); // click outside the box dismisses it
				
				// get GUI objects into scope
				addDialogLocationTextView = (TextView) addDialog.findViewById(R.id.addDialogLocationTextView);
				image = (ImageView) addDialog.findViewById(R.id.imageView1);
				addDialogOkButton = (Button) addDialog.findViewById(R.id.addDialogOkButton);
				addDialogCancelButton = (Button) addDialog.findViewById(R.id.addDialogCancelButton);
				addDialogNameEditText = (EditText) addDialog.findViewById(R.id.addDialogNameEditText);
				addDialogPriceEditText = (EditText) addDialog.findViewById(R.id.addDialogPriceEditText);
				addDialogPerUnitEditText = (EditText) addDialog.findViewById(R.id.addDialogPerUnitEditText);
				addDialogDescriptionEditText = (EditText) addDialog.findViewById(R.id.addDialogDescriptionEditText);
				
				// set listener
				addDialogOkButton.setOnClickListener(dialogListener);
				addDialogCancelButton.setOnClickListener(dialogListener);
				
				
				latitude = Double.valueOf(sharedPrefs.getString("latitude", ""));
				longitude = Double.valueOf(sharedPrefs.getString("longitude", ""));
				locationConverter.SetLocation(latitude, longitude);
				addDialogLocationTextView.setText(locationConverter.ToAddress());
				
				
				
				// show dialog
				addDialog.show();
				
				// get image from camera activity
				imageBitmap = (Bitmap) data.getExtras().get("data");
				// set dialog image
				image.setImageBitmap(imageBitmap);
		        
	       }
      }
    }
    
 // responsible for actions performed when ok and cancel are pressed on the
 	// Add dialog window
 	private class DismissAddDialogListener implements OnClickListener {

 		@Override
 		public void onClick(View v) {
 			switch(v.getId()) {
 			case R.id.addDialogOkButton:
 				AddItem();
 			case R.id.addDialogCancelButton:
 				addDialog.dismiss();
 				addDialog = null;
 				break;
 			default:
 				addDialog.dismiss();
 				break;
 			}
 			
 		}

 		private void AddItem() {
 			String name = addDialogNameEditText.getText().toString();
 			String perUnit = addDialogPerUnitEditText.getText().toString();
 			String price = addDialogPriceEditText.getText().toString();

 			String l;
 			if(latitude != null && longitude != null) {
 				locationConverter.SetLocation(latitude, longitude);
 				l = locationConverter.ToAddress();
 			}
 			else {
 				l ="";
 			}
 			String desc = addDialogDescriptionEditText.getText().toString();
 			perUnit = '/' + perUnit; 

 			FoodItem f = new FoodItem("100",name, perUnit, price, l, imageBitmap, desc);
 			// send to server			
			SendNewItemToServer(f);
 		}

		private void SendNewItemToServer(FoodItem f) {
			ArrayList<Pair<String,String>> parametersList = new ArrayList<Pair<String,String>>();
			parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.ADD_ITEM_FOR_SALE));
			parametersList.add(new Pair<String,String>("vfmApiKey", AppUtils.APP_API_KEY));
			parametersList.add(new Pair<String,String>("vfmProductName", f.name));
			parametersList.add(new Pair<String,String>("vfmProductPrice", f.price + f.perUnit));
			parametersList.add(new Pair<String,String>("vfmProductDesc", f.description));
			parametersList.add(new Pair<String,String>("vfmProductLatitude", latitude.toString()));
			parametersList.add(new Pair<String,String>("vfmProductLongitude", longitude.toString()));
			parametersList.add(new Pair<String,String>("vfmProductLocation", f.location));
			
			
			new HttpPost(mContext,new SeverResponseHandler()).execute(parametersList);
		}
 	}
 	
 	private class SeverResponseHandler implements IResultHandler {
		@Override
		public int onResult(String result) {
			// TODO Auto-generated method stub
			return MyResult.RESULT_OK;
		}

		@Override
		public void onError(int resultError) {
			// TODO Auto-generated method stub
			
		}
 	}

}
