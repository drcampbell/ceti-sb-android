package com.school_business.android.school_business;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
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
				role = getString(R.string.teacher);
				break;
			case R.id.register_speaker:
				role = getString(R.string.speaker);
				break;
			case R.id.register_both:
				role = getString(R.string.both);
				break;
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.register_button:
				view.findViewById(R.id.register_button).setClickable(false);
				JSONObject params = getParams();
				JSONObject user = new JSONObject();
				Log.d(TAG, "Button clicked?");
				try {
					if (!comparePassword(params)){
						Toast.makeText(getApplicationContext(), "Passwords Do Not Match!", Toast.LENGTH_LONG).show();
					}
					else if (!validateEmail(params.getString("email"))){
						Toast.makeText(getApplicationContext(), "Email is Invalid or Missing", Toast.LENGTH_LONG).show();
					}
					else if (!hasRole()) {
						Toast.makeText(getApplicationContext(), "Please Select a Role", Toast.LENGTH_LONG).show();
					} else if (!hasName(params.getString("name"))){
						Toast.makeText(getApplicationContext(), "Please Enter Your Name", Toast.LENGTH_LONG).show();
					}
					else {
						Log.d(TAG, "Parameters are present");

						user.put("user", params);

						findViewById(R.id.register_button).setClickable(false);
						register(user);
						}
				} catch (JSONException e){
					Log.d(TAG, "User Params Failed");
				}
				break;
		}
	}

	private Boolean hasName(String name){
		return !name.equals("");
	}

	private Boolean hasRole(){
		return  ((RadioButton) findViewById(R.id.register_teacher)).isChecked() ||
				((RadioButton) findViewById(R.id.register_speaker)).isChecked() ||
				((RadioButton) findViewById(R.id.register_both)).isChecked();
	}

	private Boolean validateEmail(String target){
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	private Boolean comparePassword(JSONObject user) {
		try {
			return user.getString("password").equals(user.getString("confirm_password"));
		} catch (JSONException e) {
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
			user.put("role", role);
			user.put("school_id", "1");
		} catch (JSONException e) {
			Log.d(TAG, "Get Params Failed");
		}
		return user;
	}

	public void register(JSONObject obj){
		String user_auth;
		String url = SchoolBusiness.TARGET + "users/sign_up";
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();
		Log.d(TAG, "Register?");
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						try {
							if (response.getString("state").equals("1")){
								JSONArray messages = response.getJSONArray("messages");
								for (int i = 0; i < messages.length(); i++ ){
									String message = messages.getString(i);
									Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
								}
								findViewById(R.id.register_button).setClickable(true);
							} else if (response.getString("state").equals("0")) {
								JSONObject user = response.getJSONObject("data");
								SchoolBusiness.setProfile(user);
								Log.d(TAG, "Registration successful");

								finishSignUp();
							}
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
		queue.add(jsonRequest);
	}

	public void finishSignUp() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		if (isFinishing()){
			;
		}else {
			finish();
		}
	}
}
