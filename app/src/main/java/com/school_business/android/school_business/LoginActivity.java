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
import android.widget.Toast;

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
	private Boolean saveLogin = false;

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

	    if (SchoolBusiness.loadLogin(getApplicationContext())) {
		    saveLoginCheckBox.setChecked(true);
		    saveLogin = true;
		    Log.d(TAG, SchoolBusiness.getEmail()+" "+SchoolBusiness.getUserAuth());
		    if (isValid(SchoolBusiness.getUserAuth())){
				startMain();
		    }
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
					SchoolBusiness.setProfile(response);
					saveLogin();
					startMain();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error){
					findViewById(R.id.sign_in_button).setClickable(true);
					Log.d(TAG + " Volley", error.toString());
					Toast.makeText(getApplicationContext(), "Unable to Authorize, please re-enter email and password", Toast.LENGTH_LONG).show();
				}
			}){
	            @Override
                public String getBodyContentType(){
		            return "application/json";
	            }
            };
	    queue.add(jsonRequest);
	}

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
	            findViewById(R.id.sign_in_button).setClickable(false);
	            checkLogin();
                break;
            case R.id.register_button:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }

	public void saveLogin(){
		if (saveLoginCheckBox.isChecked()) {
			SchoolBusiness.saveLogin(getApplicationContext());
		}
	}

	public void startMain(){
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		if (isFinishing()){
			;
		} else {
			finish();
		}
	}
}
