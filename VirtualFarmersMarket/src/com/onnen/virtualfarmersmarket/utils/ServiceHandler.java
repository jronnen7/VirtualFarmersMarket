package com.onnen.virtualfarmersmarket.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import android.util.Pair;

public class ServiceHandler {
 
    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;
 
    public ServiceHandler() {
 
    }
 
    private String BuildUrlOptions(List<Pair<String,String>> params) {
    	String ret = new String("");
    	for(int i=0;i<params.size();i++) {
			if(i==0) { ret += "?"; }
			else { ret += "&"; }
			ret+= params.get(i).first + "=" + params.get(i).second;
		} return ret;
    }
    public String makeServiceCall(String targetUrl, int method,
            List<Pair<String,String>> params) {
    		String ret = new String();
    		
            // http client
        	if(method == POST) {
        		if(params != null) {
		        	/*TODO*/
        		}
 
            } else if (method == GET) {
        		if(params != null) {
		        	URL url;
		        	HttpURLConnection httpCon;
					try {
						url = new URL(targetUrl + BuildUrlOptions(params));
						httpCon = (HttpURLConnection) url.openConnection();
						httpCon.setUseCaches(false);
			        	httpCon.setAllowUserInteraction(false);

			        	//OutputStreamWriter os = new OutputStreamWriter(httpCon.getOutputStream());
			        	//if(os != null) {
			        	//	os.flush();
			        	//	os.write(str);
			        	//}
			        
			        	BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			            StringBuilder sb = new StringBuilder();
			            String line = null;
			            
			            while((line = reader.readLine()) != null) {
			                       sb.append(line + "\n");
			            }
			            ret = sb.toString();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	

        		}
            }
            
        
    
			return ret;
    }
}

    
   

