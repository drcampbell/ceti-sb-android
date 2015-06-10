package com.school_business.android.school_business;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.client.methods.HttpPost;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by david on 6/10/15.
 */
public class NetworkService {


	public poster(){
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost();
		} catch (IllegalArgumentException e){
			;
		}
	}
	{
		URL url = new URL(hostName);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		readStream(con.getInputStream());
	} catch (Exception e) {
		e.printStackTrace();;
	}
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()){
			return true;
		}
		return false;
	}
}
