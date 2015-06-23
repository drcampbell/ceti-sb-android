package com.school_business.android.school_business;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
//import com.example.volley_examples.R;
//import com.github.volley_examples.app.MyVolley;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * Created by david on 6/11/15.
 */
public class JsonRequest extends Activity {
	private TextView mTvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_json_request);

		mTvResult = (TextView) findViewById(R.id.tv_result);

		Button btnJsonRequest = (Button) findViewById(R.id.btn_json_request);
		btnJsonRequest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
													.getRequestQueue();

				JsonObjectRequest myReq = new JsonObjectRequest(Method.GET,
						"http://echo.jsontest.com/key/value/one/two",
						null,
						createMyReqSuccessListener(),
						createMyReqErrorListener());

				queue.add(myReq);
			}
		});
	}


	private Response.Listener<JSONObject> createMyReqSuccessListener() {
		return new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					mTvResult.setText(response.getString("one"));
				} catch (JSONException e) {
					mTvResult.setText("Parse error");
				}
			}
		};
	}


	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mTvResult.setText(error.getMessage());
			}
		};
	}
}