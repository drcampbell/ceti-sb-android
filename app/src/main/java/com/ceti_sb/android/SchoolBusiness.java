package com.ceti_sb.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

	public static final Boolean DEBUG = true;
	public static final String URL = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/";
	public static final String TARGET = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/";
	public static final String AWS_S3 = "https://s3-us-west-1.amazonaws.com/ceti-sb/badges/";
	public static final String NONE = "None";
	public static final String TEACHER = "Teacher";
	public static final String SPEAKER = "Speaker";
	public static final String BOTH = "Both";
	public static final String STUDENT = "Student";
	public static final String[] roles = {"None", "Teacher", "Speaker", "Both", "Student"};

	public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
	public static final String REGISTRATION_COMPLETE = "registrationComplete";

	private static final String TAG = "school_business";
	private static String id;
	private static JSONObject profile;
	private static JSONObject notifications;
	private static SharedPreferences loginPreferences;
	private static SharedPreferences.Editor loginPrefsEditor;
	public static final String ACTION_NOTIFICATION = "com.school_business.android.action.NOTIFICATION";

	public static final String[] time_zones = {"Eastern Time (US & Canada)"};
	@Override
	public void onCreate(){
		super.onCreate();

		init();
	}

	public static JSONObject getNotifications(){
		return notifications;
	}

	public static void setProfile(JSONObject obj){
		profile = obj;
		try {
			profile.put("role", translateRole(profile.get("role").toString()));
		} catch (JSONException e){
			Log.d(TAG, e.toString());
		}
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
			profile.put("role", translateRole(profile.get("role").toString()));
		} catch (JSONException e){
			Log.d(TAG, e.toString());
		}
	}

	public static String getUserAttr(String attribute){
		try {
			if (profile != null) {
				return profile.getString(attribute);
			} else {
				Log.d(TAG, "Failed to get user attribute " + attribute);
				return "None";
			}
		}  catch (JSONException e) {
			Log.d(TAG, e.toString());
			return "None";
		}
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

	public static String getRole(){
		try {
			return profile.getString("role");
		} catch (JSONException e){
			Log.d(TAG, e.toString());
			return "";
		}
	}

	public static void setRole(String role){
		try {
			profile.put("role", role);
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

	public static String translateRole(String role){
		switch (role) {
			case "0":
				return NONE;
			case "1":
				return TEACHER;
			case "2":
				return SPEAKER;
			case "3":
				return BOTH;
			default:
				return role;
		}
	}
	public static Bundle json2Bundle(JSONObject obj){
		Bundle bundle = new Bundle();
		try {
			Iterator<String> content = obj.keys();
			while (content.hasNext()) {
				String key = content.next();
				bundle.putString(key, obj.getString(key));
			}
		} catch (JSONException e){
			Log.d("JSON EXCEPTION ERROR", e.toString());
		}
		return bundle;
	}
}