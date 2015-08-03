package com.school_business.android.school_business;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {
    private EditText userEmailText;
    private EditText userPasswordText;
    private CheckBox saveLoginCheckBox;
	private SharedPreferences loginPreferences;
	private SharedPreferences.Editor loginPrefsEditor;
	private Boolean saveLogin;

	private final static String OPT_NAME = "name";
	private String user_auth = "";
	private static final String TAG = "school_business";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailText = (EditText) findViewById(R.id.email);
        userPasswordText = (EditText) findViewById(R.id.password);
	    saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);
	    loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
	    loginPrefsEditor = loginPreferences.edit();

	    saveLogin = loginPreferences.getBoolean("saveLogin", false);
	    if (saveLogin) {
		    saveLoginCheckBox.setChecked(true);

		    userEmailText.setText(loginPreferences.getString("username", ""));
		    SchoolBusiness.setEmail(loginPreferences.getString("username",""));
		    SchoolBusiness.setCID(loginPreferences.getString("id", ""));
		    user_auth = loginPreferences.getString("token", "");

		    SchoolBusiness.setUserAuth(user_auth);
		    Log.d(TAG, SchoolBusiness.getEmail()+" "+SchoolBusiness.getUserAuth());
	    }
	    if (saveLogin && !user_auth.equals("") && isValid(user_auth)){
		    startActivity(new Intent(this, MainActivity.class));
	    }
        View btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnLogin.setOnClickListener(this);
        View btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(this);
    }

	private Boolean isValid(String user_auth){
		return true;
	}

    private void checkLogin() {

		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/users/sign_in";
	    RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
			                                .getRequestQueue();

        final String email = this.userEmailText.getText().toString();
        final String password = this.userPasswordText.getText().toString();

	    HashMap<String, String> inner = new HashMap<String, String>();
	    inner.put("email", email);
	    inner.put("password", password);
	    HashMap<String, HashMap<String, String>> outer = new HashMap<String, HashMap<String, String>>();
		outer.put("user", inner);
	JSONObject obj = new JSONObject(outer);


    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response){
					Log.d(TAG+" JSON", "Response: " + response.toString());
					try {
						SchoolBusiness.setCID(response.getString("id"));
						user_auth = response.getString("authentication_token");
						SchoolBusiness.setEmail(email);
						SchoolBusiness.setUserAuth(user_auth);
						SchoolBusiness.setProfile(response);
						saveLogin(email, user_auth);
						startActivity(new Intent(getApplicationContext(), MainActivity.class));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error){
					Log.d(TAG + " Volley", error.toString());
				}
			}){
	            @Override
                public String getBodyContentType(){
		            return "application/json";
	            }
            };
	    //Log.d("JSON",obj.getParams.toString());

	    Log.d(TAG, jsonRequest.toString());
	    Log.d(TAG, jsonRequest.getBodyContentType().toString());
	    queue.add(jsonRequest);
	}

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
	            if (saveLogin && !user_auth.equals("") && isValid(user_auth)){
		            startActivity(new Intent(this, MainActivity.class));
	            } else {
		            checkLogin();
	            }
                break;
            case R.id.register_button:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

	public void saveLogin(String username, String token){
		if (saveLoginCheckBox.isChecked()) {
			loginPreferences = this.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
			loginPrefsEditor = loginPreferences.edit();
			loginPrefsEditor.putBoolean("saveLogin", true);
			loginPrefsEditor.putString("username", username);
			loginPrefsEditor.putString("token", token);
			loginPrefsEditor.putString("id", SchoolBusiness.getCID());
			loginPrefsEditor.commit();
		} else {
			loginPrefsEditor.clear();
			loginPrefsEditor.commit();
		}
	}

}
