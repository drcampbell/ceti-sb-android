package com.school_business.android.school_business;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity
		implements ListItemFragment.OnListItemInteractionListener,
		EventViewFragment.onEventViewInteractionListener,
		EventCreateFragment.OnEventCreatorListener,
		SchoolViewFragment.OnSchoolViewInteractionListener,
		UserViewFragment.OnUserViewInteractionListener,
		ClaimViewFragment.OnClaimViewInteractionListener,
		SearchOptionsFragment.OnSearchInteractionListener,
		HomeFragment.OnWelcomeInteractionListener,
		SearchManager.OnDismissListener,
		DeleteEventDialogFragment.DeleteEventDialogListener,
		EventTabFragment.OnEventTabListener,
		FragmentTabHost.OnTabChangeListener,
		ProfileFragment.OnProfileInteractionListener,
		ProfileEditFragment.OnProfileEditListener,
		AccountEditFragment.OnEditAccountListener,
		SettingsFragment.OnSettingsListener,
		MessageFragment.OnMessageListener
{

	private final String TAG = "Event";
	private String event_id;
	private int mTab = 0;
	private final String EVENTS = "events";
	private final String USERS = "users";
	private final String CLAIMS = "claims";
	private final String SCHOOLS = "schools";
	private final String NOTIFICATIONS = "notifications";
	private String searchModel = "events";

	private final String TEACHER = "Teacher";
	private final String SPEAKER = "Speaker";
	private final String BOTH = "BOTH";

	private String FRAG_SEARCH = "Event";
	private String FRAG_MAIN = "Main";
	private String FRAG_CLAIM = "Claim";
	private String TAB_CONTENT = "TabContent";
	private String TAB_CONTAINER = "TabContainer";

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private Boolean CAN_GET_TABS = false;

	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private Fragment tabContent;
	private Fragment tabContainer;
	private Fragment blankContent;
	private Fragment blankContainer;
	private Fragment blankSearch;
	private SearchOptionsFragment searchOptionsFragment;
	private View mainView;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainView = getWindow().getDecorView().getRootView();
		handleIntent(getIntent());

		/* Get GCM Token */
		mRegistrationBroadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent){
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences.getBoolean(SchoolBusiness.SENT_TOKEN_TO_SERVER, false);
				if (sentToken){
					Log.d(TAG, "Received GCM Token");
				} else {
					Log.d(TAG, "An error occurred while either fetching the Instance ID ");
				}
			}
		};

		/* Start Registration Service */
		if (checkPlayServices()){
			Intent intent = new Intent(this, RegistrationIntentService.class);
			getApplicationContext().startService(intent);
		}

		if (findViewById(R.id.fragment_container) != null){
			if (savedInstanceState != null){
				return;
			}
			tabContent = new BlankFragment();
			tabContainer = new BlankFragment();
			blankContainer = new BlankFragment();
			blankContent = new BlankFragment();
			blankSearch = new BlankFragment();
			HomeFragment homeFragment = new HomeFragment();
			searchOptionsFragment = new SearchOptionsFragment();
			homeFragment.setArguments(getIntent().getExtras());

			/* Add the Search Options Container Fragment */
			getSupportFragmentManager().beginTransaction()
					.add(R.id.search_options_container, searchOptionsFragment, "Search")
					.commit();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.search_options_container, blankSearch, "Search").hide(searchOptionsFragment)
					.commit();
			/* Add Tab Container */
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tab_container, tabContainer, TAB_CONTAINER)
					.commit();
			/* Add Tab Content */
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tab_content, tabContent, TAB_CONTENT)
					.commit();
			/* Add Main Fragment */
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, homeFragment, FRAG_MAIN)
					.commit();
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onResume(){
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(SchoolBusiness.REGISTRATION_COMPLETE));
		if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() == HomeFragment.class) {
			onCreateTab(mTab);
		}
		super.onResume();
	}

	@Override
	protected void onPause(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() == HomeFragment.class) {
			clearTabs();
		}
		super.onPause();
	}

	@Override
	protected  void onNewIntent(Intent intent){
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView =
				(SearchView) menu.findItem(R.id.event_search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View view, boolean hasFocus){
				if(!hasFocus){
					if (searchView != null){
						if (!searchView.isIconified()){
							Log.d(TAG, "Sanity");
							getSupportFragmentManager().beginTransaction()
								.hide(searchOptionsFragment)
								.show(blankSearch)
								.commit();
							searchView.setIconified(true);
						}
					}
				}
			}
		});
		searchView.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "SearchView is iconified: " + searchView.isIconified());
				getSupportFragmentManager().beginTransaction()
						.show(searchOptionsFragment)
						.hide(blankSearch)
						.commit();
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (item.getItemId()){
			case R.id.notifications:
				sendVolley(Request.Method.GET, "", NOTIFICATIONS, null, true);
				return true;
			case R.id.menu_home:
				if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() != HomeFragment.class) {
					HomeFragment homeFragment = new HomeFragment();
					swapFragment(homeFragment, R.id.fragment_container, FRAG_MAIN, true);
				}
				return true;
			case R.id.menu_action_settings:
				Log.d(TAG, "User selected get settings");
				sendVolley(Request.Method.GET, "settings", USERS, null, true);
				return true;
			case R.id.menu_profile:
				Log.d(TAG, "User selected get profile");
				sendVolley(Request.Method.GET, "profile", USERS, null, true);
				return true;
			case R.id.menu_logout:
				Log.d(TAG, "User selected Logout");
				sendVolley(Request.Method.DELETE, "sign_out", USERS, null, false);
				return true;
		}


		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onSearchRequested() {
		Log.d(TAG, "onSearchRequested called");
		//SearchOptionsFragment searchOptionsFragment = new SearchOptionsFragment();
		swapFragment(searchOptionsFragment, R.id.search_options_container, FRAG_SEARCH, false);
		//setOnDismissListener(SearchManager.OnDismissListener listener);
		return super.onSearchRequested();
	}

	@Override
	public void onDismiss(){
		BlankFragment blankFragment = new BlankFragment();
		swapFragment(blankFragment, R.id.search_options_container, FRAG_SEARCH, false);
	}

	private void handleIntent(Intent intent){
		//Log.d(TAG, intent.getAction());
		//Log.d(TAG, intent.getDataString());
		if (intent.getAction() != null) {
			Log.d(TAG, intent.getAction().toString());
		} else {
			Log.d(TAG, "Intent but no action");
		}
		if (Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			sendSearchVolley(query, searchModel, false);
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

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
			case R.id.register_teacher:
				SchoolBusiness.setRole(TEACHER);
				break;
			case R.id.register_speaker:
				SchoolBusiness.setRole(SPEAKER);
				break;
			case R.id.register_both:
				SchoolBusiness.setRole(BOTH);
				break;
		}
		Log.d(TAG, "Role is now set to " + SchoolBusiness.getRole());

	}

	public void onListItemSelected(String id, String model) {
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	public void onEventViewInteraction(String id, String model){
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	public void onSchoolViewInteraction(String id, String model){

	}

	public void onUserViewInteraction(String id, String model){
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	public void onSearchInteraction(String model){
		searchModel = model;
	}

	public void onCreateEvent(Boolean edit, String event){
		EventCreateFragment eventCreateFragment = EventCreateFragment.newInstance(edit, event);
		swapFragment(eventCreateFragment, R.id.fragment_container, FRAG_MAIN, true);//FRAG_CREATE_EVENT, true);
	}

	public void onPostEvent(JSONObject event, Boolean edit){
		try {
			if (edit) {
				Log.d(TAG, event.toString());
				String event_id = event.getString("id");
				sendVolley(Request.Method.PATCH, event_id, EVENTS, event, true);
			} else {
				Log.d(TAG, event.toString());
				sendVolley(Request.Method.POST, "create", EVENTS, event, true);
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
			sendVolley(Request.Method.POST, "claim_event", EVENTS, new JSONObject(event), true);
		} catch (JSONException e){

		}
	}

	public void getClaims(String id){
		String res = "pending_claims?event_id="+id;
		sendVolley(Request.Method.GET, res, CLAIMS, null, false);
	}

	public void onAcceptClaim(String event_id, String claim_id){
		String res = "teacher_confirm?event_id="+event_id+"&claim_id="+claim_id;
		sendVolley(Request.Method.POST, res, CLAIMS, null, true);
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
		sendVolley(Request.Method.DELETE, event_id, EVENTS, null, false);
	}

	public void onDeleteEventNegativeClick(DialogFragment dialog){
		dialog.dismiss();
	}

	public void createSchoolFeed(View view, JSONObject response){
		try {
			imageLoader = NetworkVolley.getInstance(getApplicationContext()).getImageLoader();
			NetworkImageView badge = (NetworkImageView) view.findViewById(R.id.school_badge);
			badge.setImageUrl(SchoolBusiness.AWS_S3 + response.getString("badge_url"), imageLoader);
		} catch (JSONException e){
			;
		}
		ListItemFragment event_list = ListItemFragment.newInstance(response, "events");
		swapFragment(event_list, R.id.tab_content, TAB_CONTENT, false);
	}

	public void makeMySchool(String id){
		sendVolley(Request.Method.GET, "make_mine/" + id, SCHOOLS, null, true);
	}

	public void onCreateTab(int tab){
		CAN_GET_TABS = true;
		Log.d(TAG, "Creating Tabs");
		tabContainer = EventTabFragment.newInstance(mTab);
		swapFragment(tabContainer, R.id.tab_container, TAB_CONTAINER, false);
	}

	public void clearTabs(){
//		swapFragment(new BlankFragment(),R.id.tab_container, TAB_CONTAINER, false);
//		swapFragment(new BlankFragment(),R.id.tab_content, TAB_CONTENT, false);
	}

	public void onEditProfile(){
		ProfileEditFragment profileEditFragment = new ProfileEditFragment();
		swapFragment(profileEditFragment, R.id.fragment_container, FRAG_MAIN, true);
	}

	public void onEditAccount(){
		swapFragment(new AccountEditFragment(), R.id.fragment_container, FRAG_MAIN, true);
	}

	public void onSaveAccount(JSONObject account){
		sendVolley(Request.Method.PUT, "", "account", account, false);
	}

	public void onSaveProfile(JSONObject profile){
		sendVolley(Request.Method.PUT, "", USERS, profile, false);
	}

	public void onSaveSettings(JSONObject settings){
		sendVolley(Request.Method.PUT, "settings", USERS, settings, false);
	}

	public void onEventTabSelected(String tab){

	}

	public void createUserFeed(JSONObject response){
		ListItemFragment event_list = ListItemFragment.newInstance(response, "events");
		swapFragment(event_list, R.id.tab_content, TAB_CONTENT, false);
	}

	public void onContactUser(String id, String name){
		MessageFragment messageFragment = MessageFragment.newInstance(id, name);
		swapFragment(messageFragment, R.id.fragment_container, FRAG_MAIN, true);
	}

	public void onSendMessage(String id, JSONObject message){
		sendVolley(Request.Method.POST, id, "send_message", message, false);
	}

	@Override
	public void onTabChanged(String tab) {
		if (!CAN_GET_TABS){return;}
		switch (tab){
			case "all":
				mTab = 0;
				sendVolley(Request.Method.GET, "my_events", EVENTS, null, false);
				break;
			case "approval":
				mTab = 1;
				sendVolley(Request.Method.GET, "pending_events", EVENTS, null, false);
				break;
			case CLAIMS:
				mTab = 2;
				sendVolley(Request.Method.GET, "pending_claims", EVENTS, null, false);
				break;
			case "confirmed":
				mTab = 3;
				sendVolley(Request.Method.GET, "confirmed", EVENTS, null, false);
				break;
			default:
				break;
		}
	}

	public void swapFragment(final Fragment fragment, final int container, final String tag, final Boolean backstack) {
		MainActivity.this.mainView.post(new Runnable() {
			public void run() {
				getSupportFragmentManager().executePendingTransactions();

				if (tag.equals(FRAG_MAIN)) {
					CAN_GET_TABS = false;
				}
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				Fragment old = getSupportFragmentManager().findFragmentByTag(tag);
				if (old != null) {
					transaction.remove(old);
				}
				transaction.add(container, fragment, tag);
				if (backstack) {
					transaction.addToBackStack(tag);
				}
				transaction.commit();

				if (tag.equals(FRAG_MAIN)) {
					Log.d(TAG, "Removing tabs");
					Fragment f = getSupportFragmentManager().findFragmentById(R.id.tab_container);
					Log.d(TAG, f.getClass().toString());
					if (f != null && f.getClass() != blankContainer.getClass()) {
						tabContainer = blankContainer;
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.tab_container, blankContainer, TAB_CONTAINER)
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
								.commit();
					}
					f = getSupportFragmentManager().findFragmentById(R.id.tab_content);
					if (f.getClass() != blankContent.getClass()) {
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.tab_content, blankContent, TAB_CONTENT)
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
								.commit();
					}
				}
			}
		});
	}

	public void sendSearchVolley(final String query, final String model, final boolean feed_mode)
	{
		String url = SchoolBusiness.TARGET + model;
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
						if(SchoolBusiness.DEBUG){Log.d("JSON", "Response: " + response.toString());}
						ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model);
						if (feed_mode){
							FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
							transaction.commit();
						} else {
							swapFragment(listItemFragment, R.id.fragment_container, FRAG_MAIN, true);
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


	public void sendVolley(final int method, final String id, final String model, JSONObject obj, final Boolean backtrack){
		String url = SchoolBusiness.TARGET + model + "/" + id;
		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
				.getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(method,url,obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						if(SchoolBusiness.DEBUG){Log.d("JSON", "Response: " + response.toString());}
						switch (model) {
							case "account":
								try {
									JSONObject profile = response.getJSONObject("user");
									handleUserResponse(method, "profile", profile, backtrack);
								} catch (JSONException e) {
									Toast.makeText(getApplicationContext(),
											e.getMessage(), Toast.LENGTH_SHORT).show();
								}
								break;
							case "send_message":
								try {
									if (response.getString("state").equals("0")){
										HomeFragment homeFragment = new HomeFragment();
										swapFragment(homeFragment, R.id.fragment_container, FRAG_MAIN, false);
										Toast.makeText(getApplicationContext(), "Message Sent Successfully", Toast.LENGTH_LONG).show();
									} else {
										findViewById(R.id.send_message_button).setClickable(true);
										Toast.makeText(getApplicationContext(), "Message  Unsuccessful", Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
									findViewById(R.id.send_message_button).setClickable(true);
								}
								break;
							case NOTIFICATIONS:
								ListItemFragment notifications = ListItemFragment.newInstance(response, NOTIFICATIONS);
								swapFragment(notifications, R.id.fragment_container, FRAG_MAIN, backtrack);
								clearTabs();
								break;
							case EVENTS:
								handleEventResponse(method, model, id, response, backtrack);
								break;
							case SCHOOLS:
								handleSchoolResponse(method, id, response, backtrack);
								break;
							case USERS:
								handleUserResponse(method, id, response, backtrack);
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
						error.getMessage(), Toast.LENGTH_LONG).show();
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
		/* Handle Tabs */
		if (id.equals("my_events") || id.equals("confirmed") || id.equals("pending_events") || id.equals("pending_claims")){
			if (!CAN_GET_TABS){return;}
			tabContent = ListItemFragment.newInstance(response, model);
			swapFragment(tabContent, R.id.tab_content, TAB_CONTENT, false);
			return;
		}
		try {
			switch (method) {
				/* Get an event */
				case Request.Method.GET:
					eventViewFragment = EventViewFragment.newInstance(response);
					swapFragment(eventViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);//FRAG_EVENT, backtrack);
					break;
				/* Cancel an event */
				case Request.Method.DELETE:
					//getSupportFragmentManager().popBackStackImmediate();
					onBackPressed();
					break;
				/* Update an event */
				case Request.Method.PATCH:
					if (response.getString("state").equals("0")) {
						eventViewFragment = EventViewFragment.newInstance(response.getJSONObject("event"));
						swapFragment(eventViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);//FRAG_EVENT, backtrack);
					} else {
						Log.d(TAG, response.getString("message"));
						Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
					}
					break;
				/* Create or Claim an Event */
				case Request.Method.POST:
					JSONObject data;
					/* Create an Event */
					if (id.equals("create")) {

						if (response.getString("state").equals("0")) {
							Toast.makeText(getApplicationContext(),
									"Event posted", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString("event"));

							getSupportFragmentManager().popBackStackImmediate();
							sendVolley(Request.Method.GET, data.getString("id"), EVENTS, null, backtrack);
						} else {
							Log.d(TAG, response.getString("messages"));
							Toast.makeText(getApplicationContext(), "Failure to post event\n"+response.getString("messages"), Toast.LENGTH_LONG).show();
							findViewById(R.id.post_event_button).setClickable(true);
						}
					} else { /* Claim an Event */
						if (response.getString("state").equals("0")){
							Toast.makeText(getApplicationContext(),"Event Claimed", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString("event"));
							sendVolley(Request.Method.GET, data.getString("id"), EVENTS, null, backtrack);
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
						swapFragment(listItemFragment, R.id.claim_container, FRAG_CLAIM, false);
					} else {
						findViewById(R.id.layout_event_buttons).setVisibility(View.GONE);
						ClaimViewFragment claimViewFragment = ClaimViewFragment.newInstance(response);
						swapFragment(claimViewFragment, R.id.claim_container, FRAG_CLAIM, true);
					}
					break;
				case Request.Method.POST:
					if (response.getString("status").equals("0")) {
						JSONObject event = response.getJSONObject("event");
						EventViewFragment eventViewFragment = EventViewFragment.newInstance(event);
						swapFragment(eventViewFragment, R.id.fragment_container, FRAG_MAIN, true);//FRAG_EVENT, true);
					}
					break;
			}
		}catch(JSONException e) {
			Toast.makeText(getApplicationContext(),
					e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void handleSchoolResponse(int method, String id, JSONObject response, Boolean backtrack) {
		if (id.contains("make_mine")){
			Log.d(TAG, "Set School as Users");
			sendVolley(Request.Method.GET, "profile", USERS, null, true);
			return;
		}
		Log.d(TAG, "Getting School");
			SchoolViewFragment schoolViewFragment = SchoolViewFragment.newInstance(response);
			swapFragment(schoolViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
	}


	public void handleUserResponse(int method, String id, JSONObject response, Boolean backtrack) {
		switch (id) {
			case "profile":
				Log.d(TAG, "Updating User Profile");
				SchoolBusiness.updateProfile(response);
				ProfileFragment profileFragment = ProfileFragment.newInstance(response);
				swapFragment(profileFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				return;
			case "settings":
				switch (method) {
					case Request.Method.GET:
						Log.d(TAG, "Getting User Settings");
						SettingsFragment settingsFragment = SettingsFragment.newInstance(response);
						swapFragment(settingsFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
						return;
					case Request.Method.PUT:
						Log.d(TAG, "Posting User Settings");

						HomeFragment homeFragment = new HomeFragment();
						swapFragment(homeFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
						Toast.makeText(getApplicationContext(),
								"Settings Saved",
								Toast.LENGTH_LONG).show();
						return;
				}
			default:
				break;
		}

		UserViewFragment userViewFragment;
		switch (method) {
			case Request.Method.PUT:
				try {
					Log.d(TAG, "Posting User Profile");
					JSONObject user = response.getJSONObject("user");
					SchoolBusiness.updateProfile(user);
					userViewFragment = UserViewFragment.newInstance(response);
					swapFragment(userViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				break;
			case Request.Method.GET:
				Log.d(TAG, "Getting User Profile");
				userViewFragment = UserViewFragment.newInstance(response);
				swapFragment(userViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				break;
			case Request.Method.DELETE:
				Log.d(TAG, "Logging user out");
				try {
					Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
				SchoolBusiness.clearLogin(getApplicationContext());

				Intent intent = new Intent(this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
				break;
		}
	}
}
