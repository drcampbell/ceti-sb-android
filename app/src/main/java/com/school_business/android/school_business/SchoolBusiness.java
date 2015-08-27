package com.school_business.android.school_business;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by david on 6/11/15.
 */
public class SchoolBusiness extends Application{
	public static final String NONE = "0";
	public static final String TEACHER = "1";
	public static final String SPEAKER = "2";
	public static final String BOTH = "3";
	public static final String STUDENT = "4";
	public static final String[] roles = {"None", "Teacher", "Speaker", "Both", "Student"};

	private static final String TAG = "school_business";
	private static String id;
	private static JSONObject profile;
	private static SharedPreferences loginPreferences;
	private static SharedPreferences.Editor loginPrefsEditor;
	@Override
	public void onCreate(){
		super.onCreate();

		init();
	}

	public static void setProfile(JSONObject obj){
		profile = obj;
	}
	public static JSONObject getProfile(){
		return profile;
	}
	public static void updateProfile(JSONObject obj) {
		Iterator<?> keys = obj.keys();
		try {
			while (keys.hasNext()) {
				String key = (String) keys.next();
				profile.put(key, obj.getString(key));
			}
		} catch (JSONException e){
			Log.d(TAG, e.toString());
		}
	}
	public static String getUserAttr(String attribute){
		try {
			if (profile != null) {
				return profile.getString(attribute);
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
		try {
			return profile.getString("email");
		} catch (JSONException e){
			Log.d(TAG, e.toString());
			return "";
		}
	}
	public static void setEmail(String email) {
		try {
			profile.put("email", email);
		} catch (JSONException e){
			Log.d(TAG, e.toString());
		}
	}

	public static String getUserAuth(){
		try {
			return profile.getString("authentication_token");
		} catch (JSONException e){
			Log.d(TAG, e.toString());
			return "";
		}
	}

	public static void setUserAuth(String token){
		try {
			profile.put("authentication_token", token);
		} catch (JSONException e){
			Log.d(TAG, e.toString());
		}
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

	public static void saveLogin(Context context){
		loginPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();
		loginPrefsEditor.putBoolean("saveLogin", true);
		loginPrefsEditor.putString("profile", profile.toString());
		loginPrefsEditor.commit();
		Log.d(TAG, "Profile Saved");
	}

	public static boolean loadLogin(Context context){
		String str_profile;
		loginPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
		str_profile = loginPreferences.getString("profile","");
		if (str_profile.equals("")){
			return false;
		} else {
			try {
				profile = new JSONObject(str_profile);
				Log.d(TAG, "Profile Loaded");
				return true;
			} catch (JSONException e) {
				Log.d(TAG, e.getMessage());
				return false;
			}
		}
	}

	public static void clearLogin(Context context){
		loginPreferences = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
		loginPrefsEditor = loginPreferences.edit();
		profile = null;
		loginPrefsEditor.putString("profile", "");
		loginPrefsEditor.clear();
		loginPrefsEditor.commit();
		Log.d(TAG, "Profile cleared");
	}

	public static boolean isNumeric(String str){
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	public static String getRole(String role){
		switch (role) {
			case SchoolBusiness.NONE:
				return "None";
			case SchoolBusiness.TEACHER:
				return "Teacher";
			case SchoolBusiness.SPEAKER:
				return "Speaker";
			case SchoolBusiness.BOTH:
				return "Both";
			default:
				return "";
		}
	}
}
