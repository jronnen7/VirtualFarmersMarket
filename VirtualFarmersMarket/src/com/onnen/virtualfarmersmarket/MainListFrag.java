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
import com.onnen.virtualfarmersmarket.utils.HttpGet;
import com.onnen.virtualfarmersmarket.utils.LatLongToAddress;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainListFrag extends Fragment {

	public ListView listView;
	public FoodListAdapter listAdapter;
	public FoodListItemListener listItemListener;
	
	private Double latitude;
	private Double longitude;
	protected LatLongToAddress locationConverter;

	private ArrayList<FoodItem> foodList;
	
	private Context mContext;
	private CacheingEngine cache;
	private boolean isFirst = true;
	private static MainListFrag singleton;
	private MainListFrag() {
		foodList = new ArrayList<FoodItem>(10);
		cache = CacheingEngine.getInstance();
	}
	private void SetContext(Context context) {
		this.mContext = context;
		locationConverter = null;
		locationConverter = new LatLongToAddress(mContext);
	}
	
	public static MainListFrag GetInstance(Context context) {
		if(singleton == null) {
			singleton = new MainListFrag();
		} 
		singleton.SetContext(context);
		return singleton;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.main_list_frag, container, false);
		InitViews(rootView);
		 
		if(isFirst) {
			DownloadFarmerMarketList();
			isFirst = false;
		} 
		

		
		return rootView;
	}

	private void InitViews(View rootView) {
		listView = (ListView) rootView.findViewById(R.id.foodList);	
		listAdapter = new FoodListAdapter(mContext, R.layout.row_food_item_list, foodList);
		listItemListener = new FoodListItemListener(mContext, listAdapter);
		listView.setAdapter(this.listAdapter);
		listView.setOnItemClickListener(listItemListener);
	}

	private void DownloadFarmerMarketList() {
		List<Pair<String,String>> parametersList=new ArrayList<Pair<String,String>>();
		parametersList.add(new Pair<String,String>("vfmReqId", AppUtils.DOWNLOAD_LIST_REQ_ID));
		parametersList.add(new Pair<String,String>("vfmCurrentLoc", "23,-123.123122"));
		
		new HttpGet(new DownloadListHandler()).execute(parametersList);
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
            if(position % 2 == 1) {
            	parent.setBackgroundColor(0xcccccc);
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
	
	// notify the listAdapter that additional view is added
	// if anything addition needs to get notified moving forward i.e. the routine
	// updating the main server it will get added here
	public void NotifyView() {
		if(listAdapter != null) {
			listAdapter.notifyDataSetChanged();
		}
	}

	private class DownloadListHandler implements IResultHandler {
		@Override
		public int onResult(String result) {
			if (result != null) {
				Log.e("result", result);
				JSONObject rootObject;
				try {
					rootObject = new JSONObject(result);
					ArrayList<HashMap<String,String>> data = AppUtils.GetData(rootObject);
					DownloadImagesAsyc(data);
					ArrayList<FoodItem> serverList = AppUtils.GetFoodList(data, locationConverter);
					foodList.addAll(serverList);
					NotifyView();
				} catch (JSONException e) {
					e.printStackTrace();
	
				}
			}
			return 0;
		}

		@Override
		public void onError(int resultError) {
			
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
				NotifyView();
			}

	}

	public static void SetNull() {
		if(singleton != null) {
			singleton = null;
		}
	}
}
