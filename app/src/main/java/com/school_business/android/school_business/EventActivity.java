package com.school_business.android.school_business;

//import android.app.ListFragment;
//import android.app.FragmentManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
		import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
		import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class EventActivity extends FragmentActivity
		implements ListItemFragment.OnListItemInteractionListener,
		EventViewFragment.onEventViewInteractionListener,
		EventCreateFragment.OnEventCreatorListener,
		SchoolViewFragment.OnSchoolViewInteractionListener,
		UserViewFragment.OnUserViewInteractionListener,
		ClaimViewFragment.OnClaimViewInteractionListener,
		SearchOptionsFragment.OnSearchInteractionListener,
		WelcomeFragment.OnWelcomeInteractionListener,
		SearchManager.OnDismissListener,
		DeleteEventDialogFragment.DeleteEventDialogListener,
		EventTabFragment.OnEventTabListener,
		FragmentTabHost.OnTabChangeListener//,
		//FragmentManager.OnBackStackChangedListener
{
	private enum E_Type {EVENT_CREATE, EVENT_CLAIM};

	private final String TAG = "Event";
	private String[] listArr;
	private String event_id;
	private int mTab = 0;
	private final String EVENTS = "events";
	private final String USERS = "users";
	private final String CLAIMS = "claims";
	private final String SCHOOLS = "schools";
	private String searchModel = EVENTS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);

		handleIntent(getIntent());

		if (findViewById(R.id.fragment_container) != null){
			if (savedInstanceState != null){
				return;
			}
			BlankFragment blankFragment = new BlankFragment();
			BlankFragment blankFragment2 = new BlankFragment();
			WelcomeFragment welcomeFragment = new WelcomeFragment();
			SearchOptionsFragment searchOptionsFragment = new SearchOptionsFragment();
			welcomeFragment.setArguments(getIntent().getExtras());

			// Add the Search Options Container Fragment
			getSupportFragmentManager().beginTransaction()
					.add(R.id.search_options_container, searchOptionsFragment, "Search")
					.commit();

			// Add fragment to the container Frame Layout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, welcomeFragment, "Home").addToBackStack("Home")
					.commit();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tab_container, blankFragment, "Home")
					.commit();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, blankFragment2, "Home")
					.commit();
		}
	}

	@Override
	protected  void onNewIntent(Intent intent){
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_event, menu);

		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
				(SearchView) menu.findItem(R.id.event_search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (item.getItemId()){
			case R.id.action_settings:

				return true;
			case R.id.profile:
				ProfileFragment profileFragment = new ProfileFragment();
				swapFragment(profileFragment, R.id.fragment_container, "Profile", true);
				return true;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSearchRequested(){
		Log.d(TAG, "onSearchRequested called");
		SearchOptionsFragment searchOptionsFragment = new SearchOptionsFragment();
		swapFragment(searchOptionsFragment, R.id.search_options_container, "Search", true);
		//setOnDismissListener(SearchManager.OnDismissListener listener);
		return super.onSearchRequested();
	}

	@Override
	public void onDismiss(){
		BlankFragment blankFragment = new BlankFragment();
		swapFragment(blankFragment, R.id.search_options_container, "Search", true);
	}
	private void handleIntent(Intent intent){
		if (intent.getAction() != null) {
			Log.d(TAG, intent.getAction().toString());
		} else {
			Log.d(TAG, "Intent but no action");
		}
		if (Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			searchModels(query, searchModel, false);
		}
	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		switch (view.getId()){
			case R.id.events_checkBox:
				if (checked){
					searchModel = EVENTS;
					((CheckBox) findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.schools_checkBox:
				if (checked) {
					searchModel = SCHOOLS;
					((CheckBox) findViewById(R.id.events_checkBox)).setChecked(false);
					((CheckBox) findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.user_checkBox:
				if (checked) {
					searchModel = USERS;
					((CheckBox) findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) findViewById(R.id.events_checkBox)).setChecked(false);
				}
				break;
		}
	}

	public void onListItemSelected(String id, String model) {
		Boolean backtrack = true;
		getModel(Request.Method.GET, id, model, null, backtrack);
	}

	public void onEventViewInteraction(String id, String model){
		Boolean backtrack = true;
		getModel(Request.Method.GET, id, model, null, backtrack);
	}

	public void onSchoolViewInteraction(String id, String model){

	}
	public void onUserViewInteraction(String id, String model){
		Boolean backtrack = true;
		getModel(Request.Method.GET, id, model, null, backtrack);
	}
	public void onSearchInteraction(String model){
		searchModel = model;
	}
	public void onWelcomeInteraction(Uri uri){

	}
	public void onCreateEvent(Boolean edit, String event){
		EventCreateFragment eventCreateFragment = EventCreateFragment.newInstance(edit, event);
		swapFragment(eventCreateFragment, R.id.fragment_container, "CreateEvent", true);
	}

	public void onPostEvent(JSONObject event, Boolean edit){
		try {
			if (edit) {
				Log.d(TAG, event.toString());
				String event_id = event.getString("id");
				getModel(Request.Method.PATCH, event_id, EVENTS, event, true);
			} else {
				getModel(Request.Method.POST, "create", EVENTS, event, true);
			}
			//postEvent(event, E_Type.EVENT_CREATE);
		} catch (JSONException e){
			//TODO
		}
	}

	public void onClaimEvent(String event){
		try{
			//JSONObject jsonObject = new JSONObject(event);
			//postEvent(jsonObject, E_Type.EVENT_CLAIM);
			getModel(Request.Method.POST, "claim_event", EVENTS, new JSONObject(event), true);
		} catch (JSONException e){

		}
	}

	public void getClaims(String id){
		String res = "pending_claims?event_id="+id;
		getModel(Request.Method.GET, res,CLAIMS, null,false);
	}

	public void onAcceptClaim(String event_id, String claim_id){
		String res = "teacher_confirm?event_id="+event_id+"&claim_id="+claim_id;
		getModel(Request.Method.POST, res, CLAIMS, null, true);
	}

	public void onRejectClaim(String event_id, String user_id){

	}

	public void onDeleteEvent(String id){
		DeleteEventDialogFragment fragment = new DeleteEventDialogFragment();
		fragment.show(getFragmentManager(), "delete_event");
		event_id = id;
	}

	public void onDeleteEventPositiveClick(DialogFragment dialog){
		dialog.dismiss();
		getModel(Request.Method.DELETE, event_id, EVENTS, null, false);
	}

	public void onDeleteEventNegativeClick(DialogFragment dialog){
		dialog.dismiss();
	}

	public void onCreateTab(int tab){
		String[] tabs = {"all", "approval", "claims", "confirmed"};
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		EventTabFragment eventTabFragment = EventTabFragment.newInstance(mTab);
		transaction.add(R.id.tab_container, eventTabFragment, "EventTab" );
		transaction.commit();
	}

	public void clearTabs(){
		swapFragment(new BlankFragment(),R.id.tab_container, "Blank", false);
		swapFragment(new BlankFragment(),R.id.tab_content, "Blank", false);
//		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////		FragmentTransaction transaction = getSupportFragmentManager().findFragmentByTag("Home")
////											.getChildFragmentManager().beginTransaction();
//		Fragment fragment = getSupportFragmentManager().findFragmentByTag("EventTab");
//		transaction.remove(fragment);
//		transaction.commit();
//
//		FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
////		FragmentTransaction transaction = getSupportFragmentManager().findFragmentByTag("Home")
////											.getChildFragmentManager().beginTransaction();
//		fragment = getSupportFragmentManager().findFragmentByTag("Tab_Content");
//		transaction2.remove(fragment);
//		transaction2.commit();
	}

	public void onEventTabSelected(String tab){

	}

	@Override
	public void onTabChanged(String tab) {
		switch (tab){
			case "all":
				mTab = 0;
				getModel(Request.Method.GET, "my_events", EVENTS, null, false);
				break;
			case "approval":
				mTab = 1;
				getModel(Request.Method.GET, "pending_events", EVENTS, null, false);
				break;
			case CLAIMS:
				mTab = 2;
				getModel(Request.Method.GET, "pending_claims", EVENTS, null, false);
				break;
			case "confirmed":
				mTab = 3;
				getModel(Request.Method.GET, "confirmed", EVENTS, null, false);
				break;
			default:
				break;
		}
	}

//	@Override
//	public void onBackStackChanged(){
//
//	}

	public void cleanFragment(Fragment fragment, int container){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.remove(fragment);//getSupportFragmentManager().findFragmentById(R.id.tab_content));
		transaction.commit();
	}
//	public void swapFragment(Fragment fragment, int container, String tag, Boolean backstack){
//		Fragment current = getSupportFragmentManager().findFragmentById(container);
//		if (current != null) {
//			Log.d(TAG, "Current Fragment: " + current.getTag());
//		}
//		Log.d(TAG, "Tag: " + tag);
//		FragmentTransaction transaction;
//		if (tag.equals("TabList")){
//			current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//			transaction = current.getChildFragmentManager().beginTransaction();
//		} else {
////		if (current != null){
////			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////			Fragment subFragment;
////			switch (current.getTag()){
////
////				case "EventView":
////					subFragment = getSupportFragmentManager().findFragmentById(R.id.claim_container);
////					if (subFragment != null) {
////						transaction.remove(subFragment).commit();
////					}
////					break;
////
////				case "Home":
////					subFragment = getSupportFragmentManager().findFragmentById(R.id.tab_content);
////					if (subFragment != null){
////						transaction.remove(subFragment).commit();
////					}
////					break;
////			}
////		}
//			transaction = getSupportFragmentManager().beginTransaction();
//		}
//		transaction.replace(container, fragment, tag);
//		if (backstack) {
//			transaction.addToBackStack(null);
//		}
//		transaction.commit();
//	}
	public void swapFragment(Fragment fragment, int container, String tag, Boolean backstack)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(container, fragment, tag);
		if (backstack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	public void searchModels(final String query, final String model, final boolean feed_mode)
	{
		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/" + model;
		String encodedUrl = null;
		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
				.getRequestQueue();
		try {
			if (feed_mode) {
				encodedUrl = url + "?user=" + java.net.URLEncoder.encode(query, "UTF-8");//+"&event=");
			} else {
				encodedUrl = url + "?search=" + java.net.URLEncoder.encode(query, "UTF-8");//+"&event=");
			}
		} catch (UnsupportedEncodingException ignored) {
			// Can be safely ignored
		}
		Log.d(TAG, query);
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,encodedUrl,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d("JSON", "Response: " + response.toString());
						ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model);
						if (feed_mode){
							FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
							//transaction.replace(R.id.event_feed_container, listItemFragment, "EventFeed");
							transaction.commit();
						} else {
							swapFragment(listItemFragment, R.id.fragment_container, "EventList", true);
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


	public void getModel(final int method, final String id, final String model, JSONObject obj, final Boolean backtrack){
		String url = "http://ceti-production-spnenzsmun.elasticbeanstalk.com/api/"+model+"/"+id;
		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
				.getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(method,url,obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						Log.d(TAG, "Response: " + response.toString());
						switch (model) {
							case EVENTS:
								handleEventResponse(method, model, id, response, backtrack);
								break;
							case SCHOOLS:
								SchoolViewFragment schoolViewFragment = SchoolViewFragment.newInstance(response);
								swapFragment(schoolViewFragment, R.id.fragment_container, "SchoolView", backtrack);
								break;
							case USERS:
								UserViewFragment userViewFragment = UserViewFragment.newInstance(response);
								swapFragment(userViewFragment, R.id.fragment_container, "UserView", backtrack);
								break;
							case CLAIMS:
								handleClaimResponse(method, model, id, response);
								break;
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

	public void handleEventResponse(int method, String model, String id, JSONObject response, Boolean backtrack){
		EventViewFragment eventViewFragment;
		if (id.equals("my_events") || id.equals("confirmed") || id.equals("pending_events") || id.equals("pending_claims")){
			ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model);
			swapFragment(listItemFragment, R.id.tab_content, "Tab_Content", backtrack);
			return;
		}
		try {
			switch (method) {
				case Request.Method.GET:
					eventViewFragment = EventViewFragment.newInstance(response);
					swapFragment(eventViewFragment, R.id.fragment_container, "EventView", backtrack);
					break;
				case Request.Method.DELETE:
					getSupportFragmentManager().popBackStackImmediate();
					onBackPressed();
					break;
				case Request.Method.PATCH:
					if (response.getString("state").equals("0")) {
						eventViewFragment = EventViewFragment.newInstance(response.getJSONObject("event"));
						swapFragment(eventViewFragment, R.id.fragment_container, "EventView", backtrack);
					} else {
						Log.d(TAG, response.getString("message"));
						Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
					}
					break;
				case Request.Method.POST:
					JSONObject data;
					if (id.equals("create")) {
						 //data = new JSONObject(response.getString("state"));

						if (response.getString("state").equals("0")) {
							Toast.makeText(getApplicationContext(),
									"Event posted", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString("event"));

							getSupportFragmentManager().popBackStackImmediate();
							getModel(Request.Method.GET, data.getString("id"), EVENTS, null, backtrack);
						}
					} else { /* EVENT_CLAIM */
						if (response.getString("state").equals("0")){
							Toast.makeText(getApplicationContext(),"Event Claimed", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString("event"));
							getModel(Request.Method.GET, data.getString("id"), EVENTS, null, backtrack);
						}
					}
					break;
			}
		}catch (JSONException e){
			Toast.makeText(getApplicationContext(),
					e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void handleClaimResponse(int method, String model, String id, JSONObject response){
		try {
			switch (method) {
				case Request.Method.GET:
					if (id.contains("pending_claims")) {
						ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model);
						swapFragment(listItemFragment, R.id.claim_container, "ClaimList", false);
					} else {
						ClaimViewFragment claimViewFragment = ClaimViewFragment.newInstance(response);
						swapFragment(claimViewFragment, R.id.claim_container, "Claim", true);
					}
					break;
				case Request.Method.POST:
					if (response.getString("status").equals("0")) {
						JSONObject event = response.getJSONObject("event");
						EventViewFragment eventViewFragment = EventViewFragment.newInstance(event);
						swapFragment(eventViewFragment, R.id.fragment_container, "EventView", true);
					}
					break;
			}
		}catch(JSONException e) {
			Toast.makeText(getApplicationContext(),
					e.getMessage(), Toast.LENGTH_SHORT).show();
		}


	}
}
