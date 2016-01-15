package com.onnen.virtualfarmersmarket.utils;

import java.util.List;

import com.onnen.virtualfarmersmarket.IResultHandler;
import com.onnen.virtualfarmersmarket.LoginAct;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

public class HttpPost extends AsyncTask<List<Pair<String,String>>, Void, String> {

	private List<Pair<String,String>> parameters;
	private ProgressDialog pd;
	private IResultHandler rh;
	private Context mCtx;
	private ServiceHandler mServiceHandler;
	
	public HttpPost(Context context, IResultHandler rh) {
		mCtx = context;
		mServiceHandler = new ServiceHandler();
		this.rh = rh;
	}
	public HttpPost(IResultHandler rh) {
		mCtx = null;
		mServiceHandler = new ServiceHandler();
		this.rh = rh;
	}
	public HttpPost(Context context) {
		mCtx = context;
		mServiceHandler = new ServiceHandler();
		this.rh = null;
	}
	public HttpPost() {
		mCtx = null;
		mServiceHandler = new ServiceHandler();
		this.rh = null;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(mCtx != null) {
			pd = new ProgressDialog(mCtx);
			pd.setTitle("Please wait...");
			pd.show();
		}

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
		if(rh != null) {
			int handlerResult = rh.onResult(result);
			if(handlerResult != MyResult.RESULT_OK) {
				rh.onError(handlerResult);
			}
		}
		if(mCtx != null && pd != null) {
			pd.dismiss();
		}
	}
	
}
