package com.school_business.android.school_business;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by david on 6/11/15.
 */
public class SchoolBusiness extends Application{
	private static String email;
	private static String token;
	private static JSONObject profile;
	private static final String TAG = "school_business";

	@Override
	public void onCreate(){
		super.onCreate();

		init();
	}

	public static void setProfile(JSONObject obj){
		profile = obj;
	}

	public static String getUserAttr(String attribute){
		try {
			return profile.getString(attribute);
		}  catch (JSONException e) {
			Log.d(TAG, e.toString());
			return "None";
		}
	}

	//public static String getBio(){}
	//public static String getBio(){}

	public static String getEmail(){
		if (email != null){
			return email;
		} else {
			return "";
		}
	}
	public static void setEmail(String em) {email = em;}

	public static String getUserAuth(){
		if (token != null) {
			return token;
		} else {
			return "";
		}
	}

	public static void setUserAuth(String ua){
		token = ua;
	}

	private void init() {
		NetworkVolley.getInstance(this.getApplicationContext());
	}
}
