package com.school_business.android.school_business;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventViewActivity extends Activity {

	final String TAG = "EventView";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_view);
		getEvent("4");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_event_view, menu);
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
	public void getEvent(final String id){
		class UserAttr {
			public int res; public String label;public String getStr;
			UserAttr(int r, String l, String str){
				res=r;label=l;getStr=str;
			}
		};

		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/events/"+id;
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();

		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d(TAG, "Response: " + response.toString());
						try {
							String str;
							ArrayList<UserAttr> attrs = new ArrayList<UserAttr>();
							Log.d(TAG, response.getString("id"));
							attrs.add(new UserAttr(R.id.textViewName, "", "title"));
							attrs.add(new UserAttr(0, "Start:","event_start"));
							attrs.add(new UserAttr(0, "End:","event_end"));
							attrs.add(new UserAttr(0, "Location:","school_name"));
								attrs.add(new UserAttr(0, "Created by:", "user_name"));
								attrs.add(new UserAttr(0, "Content:","content"));

							createText(R.id.layout_event, response.getString(attrs.get(0).getStr),
									true, android.R.style.TextAppearance_Large, true);
							for (int i = 1; i < attrs.size(); i++) {
								str = attrs.get(i).getStr;
								if (str.contains("event")){
									str = parseTime(response.getString(str));
								} else {
									str = response.getString(str);
								}
								createText(R.id.layout_event, attrs.get(i).label, true,
										android.R.style.TextAppearance_Medium, false);
								createText(R.id.layout_event, str,
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


	private void createText(int layout, String message, Boolean bold, int size, Boolean center){
		LinearLayout ll = (LinearLayout) findViewById(layout);
		TextView title = new TextView(EventViewActivity.this);
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

	private String parseTime(String datetime){
		//String ndt = datetime.split(".")[0]+'Z';
		Date date = null;
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			date = format.parse(datetime);
		} catch (ParseException e){
			e.printStackTrace();
		}
		if (date != null) {
			return date.toString();
		} else {
			return "";
		}
	}

//	private String getDateTime(String datetime){
//		DateFormat[] formats = new DateFormat[]{
//				DateFormat.getDateInstance(),
//				DateFormat.getDateTimeInstance(),
//				DateFormat.getTimeInstance(),
//		};
//		for (DateFormat df: formats) System.out.println(df.format(new Date(0)));
//	}
}
