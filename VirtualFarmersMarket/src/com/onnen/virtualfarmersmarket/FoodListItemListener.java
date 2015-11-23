package com.onnen.virtualfarmersmarket;

import com.onnen.virtualfarmersmarket.MainAct.FoodListAdapter;
import com.onnen.virtualfarmersmarket.utils.AppUtils;
import com.onnen.virtualfarmersmarket.utils.CacheingEngine;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// class is used to perform an action when a item in the list is clicked on,
// opening a dialog box showing the additional information regarding that item
// the class implements onClickListener for the dialog box.
// It also starts an activity when the location is clicked on giving the user directions if the 
// google navigation app is installed on the device
public class FoodListItemListener implements OnItemClickListener, OnClickListener {
	
	private Context parent;
	private Dialog d;
	private FoodItem f;
	private CacheingEngine cache;
	FoodListAdapter listAdapter;
	
	public FoodListItemListener(Context parent, FoodListAdapter listAdapter) {
		this.parent = parent; // needed to create intent and dialog 
		this.listAdapter = listAdapter; // needed to find item from the list 
										// use list adapter instead of food list 
										// due to a view being linked to the listAdapter
										// that can be compared to in onItemClick routine
		this.cache = CacheingEngine.getInstance();
	}
	
	private void ShowDialog() {
		// create dialog
		d = new Dialog(parent, R.style.MyCoolDialog);
		d.setContentView(R.layout.dialog);
		d.setCancelable(true);
		d.setTitle(f.userName);

		// bring dialog views into scope
		Button okButton = (Button) d.findViewById(R.id.dialogOkButton);;
		TextView price = (TextView) d.findViewById(R.id.dialogPriceTextView);
		TextView description = (TextView) d.findViewById(R.id.dialogDescriptionTextView);
		TextView location = (TextView) d.findViewById(R.id.dialogLocationTextView);
		TextView name = (TextView) d.findViewById(R.id.dialogNameTextView);
		ImageView image = (ImageView) d.findViewById(R.id.dialogImageView);
		
		// set listeners
		okButton.setOnClickListener(this);
		location.setOnClickListener(this);
		
		// set dialog view values
		price.setText(f.price.toString() +" / " + f.perUnit);
		description.setText(f.description);
		
		location.setText(f.location );
		// underline location to make it look clickable
		location.setPaintFlags(location.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // bitwise or
		name.setText(f.name);
		if(f.imageFile != null) {
			image.setImageBitmap(f.imageFile);
		}else {
			byte imgData[] = cache.Get(AppUtils.BuildImageUrlFromEntryId(f.entryId));
			if(imgData != null) {
				image.setImageBitmap(Bitmap.createScaledBitmap(
						BitmapFactory.decodeByteArray(imgData, 0, imgData.length), 
						240, 240, true));
			}
		}
		// show dialog
		d.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int p, long id) {
		f = listAdapter.getItem(p);
		ShowDialog();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.dialogLocationTextView:
			Intent i;
			i = new Intent(android.content.Intent.ACTION_VIEW,
				    Uri.parse("google.navigation:q=" + f.location.replaceAll(" ", "_") ));
			
			
			// dismiss dialog
			d.dismiss();
			
			// try start maps Activity
			if(i != null) {
				try {
					parent.startActivity(i);
				}
				catch(ActivityNotFoundException ex) {
					Toast.makeText(parent, "Please install google navigation application", Toast.LENGTH_LONG).show();
				}
			}
			break;
		case R.id.dialogOkButton: // we don't care about saving any user input
			d.dismiss();
			break;
		default:
			d.dismiss();
			break;
		}
		
	}
}
