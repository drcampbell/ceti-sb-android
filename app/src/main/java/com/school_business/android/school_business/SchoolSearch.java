package com.school_business.android.school_business;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SchoolSearch extends ListActivity implements OnClickListener
		//implements LoaderManager.LoaderCallbacks<Cursor>
{
	SimpleCursorAdapter mAdapter;
	//private static final String[] PROJECTION = new String[]{Provider.Programs.NAME};
	private EditText editTextSchoolSearch;
	MatrixCursor mCursor;
	String TAG = "SchoolSearch";
	private String[] listArr;
	private ArrayList<String> names = new ArrayList<String>();
	private ListView listView;
	private Activity mContext;
	private TextView textView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_search);

//		// Get the intent, verify the action and get the query
//		Intent intent = getIntent();
//		if (Intent.ACTION_SEARCH.equals(intent.getAction())){
//			String query = intent.getStringExtra(SearchManager.QUERY);
//
//		}
		mCursor = new MatrixCursor(new String[] {"ID", "NAME"});
		editTextSchoolSearch = (EditText) findViewById(R.id.editTextSchoolSearch);
//		mAdapter = new SimpleCursorAdapter(this, android.R.id.list, null,
//				new String[]{"ID", "NAME"}, new int[]{R.id.text1}, 0);
		//MyArrayAdapter adapter = new MyArrayAdapter(this, listArr);

		//listView = (ListView) findViewById(android.R.id.list);
		//listView.setAdapter(mAdapter);

		View btnLogin = (Button) findViewById(R.id.search_schools_button);
		btnLogin.setOnClickListener(this);
		mContext = this;
		textView = (TextView) findViewById(R.id.text1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_school_search, menu);
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
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.search_schools_button:
				String query = this.editTextSchoolSearch.getText().toString();
				Log.d(TAG, "Search query: "+query);
				searchSchools(query);
				break;
		}
	}

	public void searchSchools(final String query){
		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/schools";
		RequestQueue queue = NetworkVolley.getInstance(this.getApplicationContext())
				.getRequestQueue();

		url = String.format(url+"?search="+query.replaceAll(" ","%20")+"&school=");

		//JSONObject obj = new JSONObject(params);
//		inner.put("password", password);
//		HashMap<String, HashMap<String, String>> outer = new HashMap<String, HashMap<String, String>>();
//		outer.put("user", inner);
//		JSONObject obj = new JSONObject(outer);

		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d("JSON", "Response: " + response.toString());
						try {
							JSONObject school;
							JSONArray schools = response.getJSONArray("schools");
							Log.d(TAG, response.toString());
							Log.d(TAG, schools.toString());
							for (int i = 0; i < schools.length(); i++){

								school = (JSONObject) schools.get(i);
								Log.d(TAG, school.toString());
								String id = school.getString("id");
								String school_name = school.getString("school_name");
								textView.setText(school_name);
								names.add(school_name);
								//mCursor.addRow(new Object[] {id, school_name});
							}
							//getApplicationContext().getContentResolver().notifyChange(Schools.CONTENT_URI, null);
							//txtResponse.setText(schoollist);
							listArr = new String[names.size()];
							listArr = names.toArray(listArr);

							MyArrayAdapter adapter = new MyArrayAdapter(mContext, listArr);
							setListAdapter(adapter);
							//adapter.notifyDataSetChanged();
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
				params.put("search", query);
				Log.d(TAG,SchoolBusiness.getEmail()+" "+SchoolBusiness.getUserAuth());
				Log.d(TAG, params.toString());
				return params;
			}
			@Override
			public Map<String, String> getParams(){
				Map<String, String> params = new HashMap<String, String>();
				params.put("search", query);
				return params;
			}
		};
		queue.add(jsonRequest);
	}
	public void displayResults(){
		/*
		Adapter<String>(this, android.R.layout.simple_list_item_1, mCursor);
				CursorLoader(this, android.R.layout.two_line_list_item,
				mCursor, new String[] {"ID", "NAME"},
				new int[] {android.R.id.text1, android.R.id.text2});
		setListAdapter(adapter);
		*/
	}

//	public Loader<Cursor> onCreateLoader(int id, Bundle args){
//		return (new CursorLoader(this, Provider.Schools.CONTENT_URI,
//				PROJECTION, null, null, null));
//	}
//
//	public void onLoaderReset(Loader<Cursor> loader){
//		mAdapter.swapCursor(null);
//	}
//
//	public void onLoadFinished(Loader loader, Cursor cursor){
//		mAdapter.swapCursor(cursor);
//	}

	//@Override
	public void onListItemClick(ListView l, View v, int position) {
		// Do something
	}
	public class MyArrayAdapter extends ArrayAdapter<String> {

		Activity context;
		String[] listArr;

		private TextView btnchkout;

		// private final integer[] image;

		public MyArrayAdapter(Activity context, String[] objects) {
			super(context, R.layout.activity_school_search, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			listArr = objects;

		}

		@Override
		public View getView(final int position, View convertView,
		                    ViewGroup parent) {
			// TODO Auto-generated method stub


			LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.activity_school_search, null, true);

			TextView textView = (TextView) view.findViewById(R.id.text1);
			textView.setText(listArr[position]);

			return view;
		}
	}
}
