package com.school_business.android.school_business;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by david on 6/11/15.
 */
public class SchoolBusiness extends Application{
	public static final String NONE = "0";
	public static final String TEACHER = "1";
	public static final String SPEAKER = "2";
	public static final String BOTH = "3";
	public static final String STUDENT = "4";
	private static String email;
	private static String token;
	private static String profile = "";
	private static final String TAG = "school_business";
	private static String id;

	@Override
	public void onCreate(){
		super.onCreate();

		init();
	}

	public static void setProfile(JSONObject obj){
		profile = obj.toString();
	}
	public static String getProfile(){
		return profile;
	}
	public static String getUserAttr(String attribute){
		try {
			if (profile != null) {
				JSONObject jprofile = new JSONObject(profile);
				return jprofile.getString(attribute);
			} else {
				return "None";
			}
		}  catch (JSONException e) {
			Log.d(TAG, e.toString());
			return "None";
		}
	}

	//public static String getBio(){}
	//public static String getBio(){}

	public static String getCID(){
		return ""+id;
	}

	public static void setCID(String cid){
		id = cid;
	}
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

	public static String parseTime(String datetime) {
		//String ndt = datetime.split(".")[0]+'Z';
		Date date = null;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			date = format.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date != null) {
			return date.toString();
		} else {
			return "";
		}
	}
	public static String toDisplayCase(String s) {

		final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
		// to be capitalized

		StringBuilder sb = new StringBuilder();
		boolean capNext = true;

		for (char c : s.toCharArray()) {
			c = (capNext)
					? Character.toUpperCase(c)
					: Character.toLowerCase(c);
			sb.append(c);
			capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
		}
		return sb.toString();
	}
	public static String phoneNumber(String s) {
		StringBuilder sb = new StringBuilder();
		char[] c = s.toCharArray();
		if (s.length() == 10) {
			sb.append('(');
			for (int i = 0; i < s.length(); i++){
				sb.append(c);
				if (i == 2){
					sb.append(')');
				}
				if (i == 5){
					sb.append('-');
				}
			}
		}
		return sb.toString();
	}
}
