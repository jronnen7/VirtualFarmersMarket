package com.onnen.virtualfarmersmarket.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import java.nio.charset.StandardCharsets;
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
			if(i==0) { //ret += "?"; 
			} else { ret += "&"; }
			ret+= params.get(i).first + "=" + params.get(i).second;
		} return ret;
    }
    public String makeServiceCall(String targetUrl, int method,
            List<Pair<String,String>> params) {
    		String ret = new String();
    		if(params != null) {
	        	URL url;
	        	HttpURLConnection httpCon;
				if(method == GET) {
					try {
						url = new URL(targetUrl + "?" + BuildUrlOptions(params));
						httpCon = (HttpURLConnection) url.openConnection();
						httpCon.setUseCaches(false);
			        	httpCon.setAllowUserInteraction(false);

			            httpCon.setRequestMethod("GET");

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
				} else if(method == POST) {
					String urlParameters  = BuildUrlOptions(params);
					byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
					int    postDataLength = postData.length;
					try {
						url            = new URL( targetUrl );
					
						httpCon= (HttpURLConnection) url.openConnection();           
						httpCon.setDoOutput( true );
						httpCon.setInstanceFollowRedirects( false );
						httpCon.setRequestMethod( "POST" );
						httpCon.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
						httpCon.setRequestProperty( "charset", "utf-8");
						httpCon.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
						httpCon.setUseCaches( false );
						httpCon.getOutputStream().write(postData);
						
						BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			            StringBuilder sb = new StringBuilder();
			            String line = null;
			            
			            while((line = reader.readLine()) != null) {
			                       sb.append(line + "\n");
			            }
			            ret = sb.toString();

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
		return ret;
    }
}

    
   

