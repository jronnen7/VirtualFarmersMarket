package com.example.virtalfamersmarket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.provider.MediaStore;

// This framework is the beginning of a much more complex framework that will integrate
// client server, to update the listView, we will also need to notify the server of our current/desired
// location to find food and it will return the closest results to me. (Summer project)
// as of right now this is the front-end of the application, it integrates the camera, GPS,
// and WI-FI to capture the location and of the device then trys to convert the Lat, and Long
// to a readable address.  It also allows you to take a picture then add description about the
// picture, while adding the description it resolves the devices current location and 
// updates the location description
public class MainActivity extends ActionBarActivity implements LocationListener {
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
	protected Location location;
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
    private Socket socket;
    private static final int SERVERPORT = 8888;
    private static final String SERVER_IP = "130.211.165.39";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton camera = (ImageButton) findViewById(R.id.cameraButton);
        camera.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		TakePicture();
			}
        	
        });
        
        location = new Location(this);
        foodList = new ArrayList<FoodItem>(10);
        listView = (ListView) findViewById(R.id.foodList);	
        listAdapter = new FoodListAdapter(MainActivity.this, R.layout.row_food_item_list, foodList);
        listItemListener = new FoodListItemListener(this, listAdapter);
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(listItemListener);
        
        new Thread(new ClientThread(this)).start();
       
    }

	// array adapter with overloaded getView to get views for each item.
    // Allows interfacing between the listView, a row in the list,
    // and the actual list of data
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
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				price.setText('$' + f.price.toString() + f.perUnit);
			}
			if(image != null) {
				image.setImageBitmap(f.imageFile);
			}
		}

	} /* FoodListAdapter */
	
    private void TakePicture()
	{
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    Toast.makeText(this, "Take Picture to Add Item to List", Toast.LENGTH_LONG).show();
	    // use this routine to have it call onActivityResult when it is finished with the pic
	    startActivityForResult(intent, 0);
	}
    
    // when activity is returened to from camera...
    // this routine can get called several other ways so we need to ensure
    // that the camera routine returned successfully before trying to capture the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if(addDialog!= null) {
	       if(addDialog.isShowing()) {
	    	   addDialog.dismiss();
	    	   addDialog = null;
	       }
       }
       // activity return successfully (user didn't hit back button to get back from camera)
       if(data != null) {
    	    // make a listener for the location so when the user clicks it the location updates
	       	UpdateAddressListener locationUpdate = new UpdateAddressListener();
	       	// make a listener for the add dialog class
	       	DismissAddDialogListener dialogListener = new DismissAddDialogListener();
	       	addDialog = new Dialog(this); // create dialog
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
			addDialogLocationTextView.setOnClickListener(locationUpdate);
			
			addDialogOkButton.setOnClickListener(dialogListener);
			addDialogCancelButton.setOnClickListener(dialogListener);
			
			// show dialog
			addDialog.show();
			
			// get image from camera activity
			imageBitmap = (Bitmap) data.getExtras().get("data");
			// set dialog image
			image.setImageBitmap(imageBitmap);
	       
	        //Request Location Updates
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
		location.SetLocation(latitude, longitude);
		
		if(addDialog != null && addDialogLocationTextView != null) {
			addDialogLocationTextView.setText(location.address + '\n' + location.city);
			
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
			double price;
			String stringPrice = addDialogPriceEditText.getText().toString();
			if(!stringPrice.equals("")) {
				price = Double.parseDouble(addDialogPriceEditText.getText().toString());
			}
			else {
				price = 0;
			}
			String l;
			if(latitude != null && longitude != null) {
				location.SetLocation(latitude, longitude);
				l = location.address + location.city;
			}
			else {
				l ="";
			}
			String desc = addDialogDescriptionEditText.getText().toString();
			perUnit = '/' + perUnit; 
			// create food item
			FoodItem f = new FoodItem(name, perUnit, price, l, imageBitmap, desc);
			// add it to the list
			foodList.add(f);
			// notify the view the list changed
			Notify();
		}
	}
	
	// class used to display all the addresses returned and
	// refresh update address when scrolled through entire list
	// beings there is no manual input of addresses by being able to update
	// addresses on click is important
	private class UpdateAddressListener implements OnClickListener {

		private int count;
		public UpdateAddressListener() {
			count = 0;
		}
		@Override
		public void onClick(View v) {
			String address = null;
			String city = null;
			if(addresses == null) {
				return;
			}
			if(addresses.size() > count + 1) {
				count++;
			}
			else if(count + 1 == addresses.size()) { // reached last item show lat and long
				// still increment count
				count++;
				address = "Lat : " + latitude.toString();
				city = "Long : " + longitude.toString();
			}
			else {
				count = 0;
				RequestLocationUpdates();
			}
			if(address == null && city == null) {
				address = addresses.get(count).getAddressLine(0);
				city = addresses.get(count).getAddressLine(1);
			}
			if(addDialog != null && addDialogLocationTextView != null) {
				addDialogLocationTextView.setText(address + '\n' + city);
			}
		}
		
	}
	
	// if the locationManager is defined request updates on GPS and WIFI
	public void RequestLocationUpdates() {
		if(locationManager != null) {
			// request GPS Updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
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
	

    class ClientThread implements Runnable {
        Context mainActivity;
    	public ClientThread(Context mainActivity) {
			this.mainActivity = mainActivity;
		}

		@Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                
                Toast.makeText(mainActivity, "text", Toast.LENGTH_LONG).show();
                
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream())); 
                out.println("getFarmers");
                String temp;
                String buf = new String();
                while ((temp = stdIn.readLine()) != null) {
                	buf.concat(temp);
                }
                Toast.makeText(mainActivity, buf, Toast.LENGTH_LONG).show();
            } catch (UnknownHostException e1) {

            	Toast.makeText(mainActivity, "error Unknown Host", Toast.LENGTH_LONG).show();

            } catch (IOException e1) {

            	Toast.makeText(mainActivity, "IO Exception", Toast.LENGTH_LONG).show();

            }
        }
    }
}
