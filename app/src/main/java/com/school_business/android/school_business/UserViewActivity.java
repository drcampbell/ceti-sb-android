package com.school_business.android.school_business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.school_business.android.school_business.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewActivity extends Activity implements View.OnClickListener {

	TextView mTextView;
	final String TAG = "UserView";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_view);
		getUser("4");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_user_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v){
		switch (v.getId()){
			case R.id.TV_School:

				startActivity(new Intent(this,SchoolViewActivity.class));
				break;
			case R.id.TV_Grades:
			case R.id.TV_Business:
			case R.id.TV_Job:
			case R.id.TV_Bio:
			case R.id.TV_Name:
			case R.id.TV_Role:
		}
	}

	class TVAttributes {
		public int res; public String label;public String getStr;
		TVAttributes(int r, String l, String str){
			res=r;label=l;getStr=str;
		}
	}

	public void getUser(final String id){

		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/users/"+id;
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();

		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d("JSON", "Response: " + response.toString());
						try {
							ArrayList<TVAttributes> attrs = new ArrayList<TVAttributes>();
							String role = response.getString("role");
							Log.d(TAG, role);
							attrs.add(new TVAttributes(R.id.TV_Name, "","name"));
							if (role.equals("Teacher") || role.equals("Both")){
								attrs.add(new TVAttributes(R.id.TV_School, "School:", "school"));
								attrs.add(new TVAttributes(R.id.TV_Grades, "Grades:","grades"));
							}
							if (role.equals("Speaker") || role.equals("Both")){
								attrs.add(new TVAttributes(R.id.TV_Job, "Job Title:", "job_title"));
								attrs.add(new TVAttributes(R.id.TV_Business, "Business:","business"));
							}
							attrs.add(new TVAttributes(R.id.TV_Role, "Role:", "role"));
							attrs.add(new TVAttributes(R.id.TV_Bio, "Biography:","biography"));

							createText(R.id.layout_user, attrs.get(0).res, response.getString(attrs.get(0).getStr),
										true, android.R.style.TextAppearance_Large, true);
							for (int i = 1; i < attrs.size(); i++) {
								createText(R.id.layout_user, attrs.get(i).res, attrs.get(i).label, true,
										android.R.style.TextAppearance_Medium, false);
								createText(R.id.layout_user, attrs.get(i).res, response.getString(attrs.get(i).getStr),
										false, android.R.style.TextAppearance_Medium, false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"Error: " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				Toast.makeText(getApplicationContext(),
						error.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}){
			@Override
			public String getBodyContentType(){
				return "application/json";
			}

			@Override public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application/json");
				params.put("Accept", "application/json");
				params.put("X-User-Email", SchoolBusiness.getEmail());
				params.put("X-User-Token", SchoolBusiness.getUserAuth());
				return params;
			}
		};
		queue.add(jsonRequest);
	}


	private void createText(int layout, int tv, String message, Boolean bold, int size, Boolean center){
		LinearLayout ll = (LinearLayout) findViewById(layout);
		TextView title = new TextView(UserViewActivity.this);
		title.setId(tv);
		title.setText(message);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
											LinearLayout.LayoutParams.WRAP_CONTENT);
		title.setLayoutParams(params);
		title.setTextAppearance(this, size);
		if (bold) {
			title.setTypeface(null, Typeface.BOLD);
		}
		if (center) {
			title.setGravity(Gravity.CENTER);
		}
		ll.addView(title);
	}
}
