package com.school_business.android.school_business;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends Activity implements View.OnClickListener
{
	String role;
	final String TAG = "SignUp";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
			case R.id.register_teacher:
				role = "Teacher";
				break;
			case R.id.register_speaker:
				role = "Speaker";
				break;
			case R.id.register_both:
				role = "Both";
				break;
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.register_button:
				JSONObject params = getParams();
				JSONObject user = new JSONObject();
				Log.d(TAG, "Button clicked?");
				if (validateParams(params)) {
					Log.d(TAG, "Parameters are present");
					try {
						user.put("user", params);
					} catch (JSONException e){
						Log.d(TAG, "User Params Failed");
					}
					register(user);
					findViewById(R.id.register_button).setClickable(false);
				} else {
					Log.d(TAG, "Validation failed");
					Toast.makeText(getApplicationContext(), "Fill out all forms", Toast.LENGTH_SHORT);
				}
				break;
		}
	}
	private Boolean validateParams(JSONObject user) {
		try {
			return (user.getString("password").equals(user.getString("confirm_password"))
					&& user.getString("name") != null && user.getString("email") != null
					&& user.getString("role") != null);
		} catch (JSONException e) {
			Log.d(TAG, "Validate Params Failed");
			return false;
		}
	}

	public JSONObject getParams() {
		JSONObject user = new JSONObject();
		EditText field;
		try {
			field = (EditText) findViewById(R.id.name);
			user.put("name", field.getText());
			field = (EditText) findViewById(R.id.email);
			user.put("email", field.getText());
			field = (EditText) findViewById(R.id.password);
			user.put("password", field.getText());
			field = (EditText) findViewById(R.id.confirm_password);
			user.put("confirm_password", field.getText());
//			if (((EditText)findViewById(R.id.password)).getText() == ((EditText)findViewById(R.id.confirm_password)).getText()) {
//				field = (EditText) findViewById(R.id.password);
//				user.put("password", field.getText());
//			}
			user.put("role", role);
			user.put("school_id", "1");
		} catch (JSONException e) {
			Log.d(TAG, "Get Params Failed");
		}
		return user;
	}

	public void register(JSONObject obj){
		String user_auth;
		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/users/sign_up";
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();
		Log.d(TAG, "Register?");
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d(TAG + " JSON", "Response: " + response.toString());

						try {
							JSONObject user = response.getJSONObject("data");
							SchoolBusiness.setCID(user.getString("id"));
							//user_auth = response.getString("authentication_token");
							SchoolBusiness.setEmail(user.getString("email"));
							SchoolBusiness.setUserAuth(user.getString("authentication_token"));
							SchoolBusiness.setProfile(user);
							Log.d(TAG, "Registration successful");

							finish();
//						if(user_auth != null){
//							Log.d(TAG+" LOGIN", user_auth);
//						}
						} catch (JSONException e) {
							findViewById(R.id.register_button).setClickable(true);
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

//		if (SchoolBusiness.getUserAuth() == user_auth) {
//			saveLogin(email, user_auth);
//			startActivity(new Intent(this, MainActivity.class));
//		} else {
//			// TODO bad login
//		}
	}
	public void finish() {
		startActivity(new Intent(this, MainActivity.class));
	}
}
