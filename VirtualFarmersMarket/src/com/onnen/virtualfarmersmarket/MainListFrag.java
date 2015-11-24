package com.onnen.virtualfarmersmarket;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;
import com.onnen.virtualfarmersmarket.utils.LatLongToAddress;
import com.onnen.virtualfarmersmarket.utils.ServiceHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainListFrag extends Fragment implements LocationListener {

	public ListView listView;
	public FoodListAdapter listAdapter;
	public FoodListItemListener listItemListener;
	private ImageView image;
	private Double latitude;
	private Double longitude;
	private String address;
	private String city;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected LatLongToAddress locationConverter;
	private Dialog addDialog;
	private TextView addDialogLocationTextView;
	private EditText addDialogPriceEditText;
	private EditText addDialogPerUnitEditText;
	private EditText addDialogNameEditText;
	private EditText addDialogDescriptionEditText;
	private Button addDialogOkButton;
	private Button addDialogCancelButton;
	private List<Address> addresses;
	private ArrayList<FoodItem> foodList;
	private Bitmap imageBitmap;
	private ServiceHandler mServiceHandler;
	private Context mContext;
	private CacheingEngine cache;
	
	private static MainListFrag singleton;
	private MainListFrag() {
		this.mContext = getActivity();
	}

	public static MainListFrag GetInstance() {
		if(singleton == null) {
			singleton = new MainListFrag();
		}return singleton;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main_list_frag, container, false);
		ImageButton camera = (ImageButton) rootView.findViewById(R.id.cameraButton);
        camera.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		TakePicture();
			}
        	
        });
        
        cache = CacheingEngine.getInstance();
        mServiceHandler = new ServiceHandler();
        locationConverter = new LatLongToAddress(mContext);
        foodList = new ArrayList<FoodItem>(10);
        listView = (ListView) rootView.findViewById(R.id.foodList);	
        listAdapter = new FoodListAdapter(mContext, R.layout.row_food_item_list, foodList);
        listItemListener = new FoodListItemListener(mContext, listAdapter);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(listItemListener);
        
        List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
        parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.DOWNLOAD_LIST_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmCurrentLoc", "23,-123.123122"));
		 
		new DownloadListTask().execute(parametersList);
		
		

		
		
		
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	public Fragment ToFragment() {
		return this;
	}
	
	public class FoodListAdapter extends ArrayAdapter<FoodItem> {
		private ArrayList<FoodItem> items;
		private View v;
		public FoodListAdapter(Context context, int textViewResourceId,
				ArrayList<FoodItem> objects) {
			super(context, textViewResourceId, objects);
			items = objects;
		}
		
		// get the view of the current position
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_food_item_list, null);
            }
            FoodItem f = items.get(position);
            if(f != null) {
            	setView(f);
            } 
            return v;
		}
		
		private void setView(FoodItem f) {
			TextView name = (TextView) v.findViewById(R.id.listview_name);
			TextView location = (TextView) v.findViewById(R.id.listview_location);
			TextView price= (TextView) v.findViewById(R.id.listview_price);
			ImageView image = (ImageView) v.findViewById(R.id.listview_picture);
			if(name != null) {
				name.setText(f.name);
			}
			if(location != null) {
				location.setText(f.location);
			}
			if(price != null) {
				price.setText(f.price.toString() +" / " + f.perUnit);
			}
			if(image != null && f.imageFile != null) {
				image.setImageBitmap(Bitmap.createScaledBitmap(
						f.imageFile, 
						120, 120, true));
			} else{
				byte imgData[] = cache.Get(AppUtils.BuildImageUrlFromEntryId(f.entryId));
				if(imgData != null) {
					image.setImageBitmap(Bitmap.createScaledBitmap(
							BitmapFactory.decodeByteArray(imgData, 0, imgData.length), 
							120, 120, true));
				}
			}
		}

	} /* FoodListAdapter */
	
    private void TakePicture()
	{
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    Toast.makeText(mContext, "Take Picture to Add Item to List", Toast.LENGTH_LONG).show();
	    // use this routine to have it call onActivityResult when it is finished with the pic
	    startActivityForResult(intent, 0);
	}
    
    // when activity is returened to from camera...
    // this routine can get called several other ways so we need to ensure
    // that the camera routine returned successfully before trying to capture the image

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
			
			// show dialog
			addDialog.show();
			
			// get image from camera activity
			imageBitmap = (Bitmap) data.getExtras().get("data");
			// set dialog image
			image.setImageBitmap(imageBitmap);
	       
	        //Request Location Updates
			locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	        RequestLocationUpdates(); 
       }
    }

    // location of the device is found.
	@Override
	public void onLocationChanged(android.location.Location arg0) {
		// save location of the device
		latitude = arg0.getLatitude();
		longitude = arg0.getLongitude();
		locationManager.removeUpdates(this);
		locationConverter.SetLocation(latitude, longitude);
		
		if(addDialog != null && addDialogLocationTextView != null) {
			addDialogLocationTextView.setText(locationConverter.ToAddress());
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// Don't really care if it was disabled after first update
	}

	@Override
	public void onProviderEnabled(String arg0) {
		//  Wait for the provider to find location no initilization/cleanup needed
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// doesn't matter because updates are shut off after first found location
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
			// create food item 
			/* TODO GENERATE ENTRY ID MAYBE FROM THE SERVER ? */
			FoodItem f = new FoodItem("100",name, perUnit, price, l, imageBitmap, desc);
			// add it to the list
			foodList.add(f);
			// notify the view the list changed
			Notify();
		}
	}
	
	
	// if the locationManager is defined request updates on GPS and WIFI
	public void RequestLocationUpdates() {
		if(locationManager != null) {
			// request GPS Updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			// request Wi-Fi updates
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
	}
	
	// notify the listAdapter that additional view is added
	// if anything addition needs to get notified moving forward i.e. the routine
	// updating the main server it will get added here
	private void Notify() {
		if(listAdapter != null) {
			listAdapter.notifyDataSetChanged();
		}
	}
	
	

	private class DownloadListTask extends AsyncTask<List<Pair<String,String>>, Void, String> {
		private List<Pair<String,String>> parameters;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			/*pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();*/
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
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
					ArrayList<HashMap<String,String>> data = AppUtils.GetData(rootObject);
					DownloadImagesAsyc(data);
					ArrayList<FoodItem> serverList = AppUtils.GetFoodList(data, locationConverter);
					foodList.addAll(serverList);
					Notify();
				} catch (JSONException e) {
					e.printStackTrace();

				}
			}

		}
	}
	
	
	private void DownloadImagesAsyc(ArrayList<HashMap<String, String>> data) {
		for(int i=0;i<data.size() ;i++) {
			if(data.get(i) != null) {
				String url = AppUtils.BuildImageUrlFromEntryId(data.get(i).get("entryId"));
				new DownloadImageTask().execute(url);
			}
		}


		
	}
	private class DownloadImageTask extends AsyncTask<String, Void, Void> {
		
		 private CacheingEngine imgCache; 
		    private void init() {
		    	this.imgCache = CacheingEngine.getInstance();
		    }

		    public DownloadImageTask() {
		    	init();
			}

			
			private Bitmap GetFromCache(String url) {
				Bitmap ret = null;
				if(imgCache.Get(url) != null) {
					ret = BitmapFactory.decodeByteArray(imgCache.Get(url), 0, imgCache.Get(url).length);
				}
				return ret;
			}
			

			
			protected Void doInBackground(String... urls) {
				    for(int i=0;i<urls.length;i++) {
				        Bitmap mIcon11 = GetFromCache(urls[i]);
				        if(mIcon11 == null) {
					        try {
					            InputStream in = new java.net.URL(urls[i]).openStream();
					            
					            
					            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

					            int nRead;
					            byte[] data = new byte[16384];

					            while ((nRead = in.read(data, 0, data.length)) != -1) {
					              buffer.write(data, 0, nRead);
					            }

					            buffer.flush();
					            
					            mIcon11 = BitmapFactory.decodeByteArray(buffer.toByteArray(), 0, buffer.size());
					            if(mIcon11 != null) {
					            	imgCache.Add(urls[i], buffer.toByteArray());
					            }
					        } catch (Exception e) {
					            Log.e("Error", e.getMessage());
					            e.printStackTrace();
					        }
				        }
			    	}
		        return null;
		    }
			protected void onPostExecute(Void result) {
				Notify();
			}


		

	}
}
