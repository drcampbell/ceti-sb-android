package com.school_business.android.school_business;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class EventViewActivity extends Activity implements View.OnClickListener{

	final String TAG = "EventView";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_view);
		getEvent(SchoolBusiness.getCID());
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

	public void onClick(View v){
		switch (v.getId()){
			case R.id.TV_School:
				SchoolBusiness.setCID((String) findViewById(R.id.TV_School).getTag());
				startActivity(new Intent(this, SchoolViewActivity.class));
				break;
			case R.id.TV_Name:
				SchoolBusiness.setCID((String) findViewById(R.id.TV_Name).getTag());
				startActivity(new Intent(this, UserViewActivity.class));
				break;
			case R.id.TV_Business:
			case R.id.TV_Job:
			case R.id.TV_Bio:
			case R.id.TV_Role:
		}
	}

	class TVAttributes {
		public int res; public String label;public String getStr; int id;
		TVAttributes(int r, String l, String str){
			res=r;label=l;getStr=str;id=0;
		}
	}
	
	public String get_id(int res){
		switch (res){
			case R.id.TV_School:
				return "school_id";
			case R.id.TV_Name:
				return "user_id";
			default:
				return "";
		}
	}

	public void getEvent(final String id){
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
							String link = "";
							ArrayList<TVAttributes> attrs = new ArrayList<TVAttributes>();
							Log.d(TAG, response.getString("id"));
							attrs.add(new TVAttributes(R.id.TV_Title, "", "title"));
							attrs.add(new TVAttributes(R.id.tv_speaker, "Speaker:", "speaker"));
							attrs.add(new TVAttributes(R.id.TV_EventStart, "Start:","event_start"));
							attrs.add(new TVAttributes(R.id.TV_EventEnd, "End:","event_end"));
							attrs.add(new TVAttributes(R.id.TV_School, "Location:","school_name"));
							attrs.add(new TVAttributes(R.id.TV_Name, "Created by:", "user_name"));
							attrs.add(new TVAttributes(R.id.TV_Content, "Content:","content"));

							createText(R.id.layout_event, attrs.get(0).res, "0", response.getString(attrs.get(0).getStr),
									true, android.R.style.TextAppearance_Large, true);
							for (int i = 1; i < attrs.size(); i++) {
								link = get_id(attrs.get(i).res);
								if (!link.equals("")){
									link = response.getString(link);
								} else {
									link = "0";
								}
								str = attrs.get(i).getStr;
								if (str.contains("event")){
									str = parseTime(response.getString(str));
								} else {
									str = response.getString(str);
								}
								createText(R.id.layout_event, attrs.get(i).res, link, attrs.get(i).label, true,
										android.R.style.TextAppearance_Medium, false);
								createText(R.id.layout_event, attrs.get(i).res, link, str,
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


	private void createText(int layout, int tv, String id, String message, Boolean bold, int size, Boolean center){
		LinearLayout ll = (LinearLayout) findViewById(layout);
		TextView title;
			if (!id.equals("0")) {
				title = new TextView(new ContextThemeWrapper(EventViewActivity.this, R.style.TV_Style_Link),null,0);
			} else if (bold) {
				title = new TextView(new ContextThemeWrapper(EventViewActivity.this, R.style.TV_Style_Title),null,0);
			} else {
				title = new TextView(new ContextThemeWrapper(EventViewActivity.this, R.style.TV_Style_Content),null,0);
			}
		title.setId(tv);
		title.setText(message);
		title.setTag(id);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT);
//		title.setLayoutParams(params);
		title.setTextAppearance(this, size);
//		if (bold) {
//			title.setTypeface(null, Typeface.BOLD);
//		}
//		if (center) {
//			title.setGravity(Gravity.CENTER);
//		}
		if (!id.equals("0")){
			title.setOnClickListener(EventViewActivity.this);
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
}
