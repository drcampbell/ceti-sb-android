package com.ceti_sb.android.registration;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ceti_sb.android.R;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.application.SchoolBusiness;
import com.ceti_sb.android.controller.MainActivity;
import com.ceti_sb.android.volley.NetworkVolley;

import java.util.HashMap;

import android.content.Intent;
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
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {

    private EditText userEmailText;
    private EditText userPasswordText;
    private CheckBox saveLoginCheckBox;

	private static final String TAG = "Login Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	    Log.d(TAG, "Login Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		Intent intent = getIntent();
        userEmailText = (EditText) findViewById(R.id.email);
        userPasswordText = (EditText) findViewById(R.id.password);
	    saveLoginCheckBox = (CheckBox) findViewById(R.id.saveLoginCheckBox);

	    if (SchoolBusiness.loadLogin(getApplicationContext())) {
		    saveLoginCheckBox.setChecked(true);
		    startMain(intent);
	    }

        View btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnLogin.setOnClickListener(this);
        View btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(this);
    }

    private void checkLogin() {
	    JSONObject obj;
		String url = SchoolBusiness.getTarget() + "users/sign_in";
	    RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
			                                .getRequestQueue();
//		Log.d("isPASSWORD", "Password is present? " + isPassword);
//	    if (!isPassword){
//		    if (token.equals(Constants.NULL)){
//			    return;
//		    } else {
//			    obj = useToken(token);
//		    }
//	    } else {
		    obj = newLogin();
//	    }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,obj,
			new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response){
					SchoolBusiness.setProfile(response);
					if (saveLoginCheckBox.isChecked()){
						SchoolBusiness.setRemember(true);
					}
					SchoolBusiness.saveLogin(getApplicationContext());
					startMain(null);
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

	private JSONObject useToken(String token){
		final String email = SchoolBusiness.getEmail();
		HashMap<String, String> inner = new HashMap<>();
		inner.put(Constants.EMAIL, email);
		inner.put(Constants.TOKEN, token);
		HashMap<String, HashMap<String, String>> outer = new HashMap<>();
		outer.put("user", inner);
		return new JSONObject(outer);
	}

	private JSONObject newLogin(){
		final String email = this.userEmailText.getText().toString();
		final String password = this.userPasswordText.getText().toString();

		HashMap<String, String> inner = new HashMap<String, String>();
		inner.put(Constants.EMAIL, email);
		inner.put(Constants.PASSWORD, password);
		HashMap<String, HashMap<String, String>> outer = new HashMap<String, HashMap<String, String>>();
		outer.put("user", inner);
		return new JSONObject(outer);
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

	public void startMain(Intent intent){

		Intent freshIntent = new Intent(this, MainActivity.class);
		freshIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		if (intent != null){
			if (intent.getAction() != null) {
				Log.d(TAG, intent.getAction());
				if (intent.getAction().equals(Intent.ACTION_VIEW)) {
					Log.d(TAG, intent.getData().toString());
				}
				freshIntent.setAction(intent.getAction());
			}
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				freshIntent.putExtras(bundle);
			}
			freshIntent.setData(intent.getData());
		}
		startActivity(freshIntent);
		if (!isFinishing()){
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		/* Logs 'install' and 'app activate' App Events. */
		AppEventsLogger.activateApp(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		/* Logs 'app deactive' App Event */
		AppEventsLogger.deactivateApp(this);
	}
}
