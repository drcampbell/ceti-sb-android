package com.ceti_sb.android;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;

import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bolts.AppLinks;

public class MainActivity extends FragmentActivity
		implements ListItemFragment.OnListItemInteractionListener,
		EventViewFragment.onEventViewInteractionListener,
		EventCreateFragment.OnEventCreatorListener,
		SchoolViewFragment.OnSchoolViewInteractionListener,
		UserViewFragment.OnUserViewInteractionListener,
		UserProfileFragment.OnUserProfileListener,
		UserBadgesFragment.UserBadgesListener,
		ClaimViewFragment.OnClaimViewInteractionListener,
		SearchOptionsFragment.OnSearchInteractionListener,
		HomeFragment.OnWelcomeInteractionListener,
		SearchManager.OnDismissListener,
		DeleteEventDialogFragment.DeleteEventDialogListener,
		TabFragment.OnEventTabListener,
		FragmentTabHost.OnTabChangeListener,
		ProfileFragment.OnProfileInteractionListener,
		ProfileEditFragment.OnProfileEditListener,
		AccountEditFragment.OnEditAccountListener,
		SettingsFragment.OnSettingsListener,
		MessageFragment.OnMessageListener,
		BadgeAwardFragment.AwardBadgeListener,
		BadgeViewFragment.OnBadgeReceiveListener,
		SchoolBusiness.OnNotificationListener

{

	private final String TAG = "MainActivity";
	private String event_id;
	private int mTab = 0;
	private String searchModel = Constants.EVENTS;

	private String FRAG_SEARCH = "Event";
	private String FRAG_MAIN = "Main";
	private String FRAG_CLAIM = "Claim";
	private String TAB_CONTENT = "TabContent";
	private String TAB_CONTAINER = "TabContainer";

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private Boolean CAN_GET_TABS = false;

	MenuItem notifications;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private Fragment tabContent;
	private Fragment tabContainer;
	private Fragment blankContent;
	private Fragment blankContainer;
	private Fragment blankSearch;
	private SearchOptionsFragment searchOptionsFragment;
	private View mainView;
	public ImageLoader imageLoader;
	private SearchView searchView;
	private String restore_model;
	private String restore_id;
	private UserProfileFragment userProfileFragment;
	private UserBadgesFragment userBadgesFragment;
	public TwitterClient twitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		mainView = getWindow().getDecorView().getRootView();
		Intent intent = getIntent();
		handleIntent(intent);
		SchoolBusiness.loadLogin(this);
		twitter = new TwitterClient();
		twitter.initialize(this);
		Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
		if (targetUrl != null) {
			Log.i("Activity", "App Link Target URL: "+targetUrl.toString());
		}
		/* Get GCM Token */
		mRegistrationBroadcastReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent){
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
				if (sentToken){
					Log.d(TAG, "Received GCM Token");
				} else {
					Log.d(TAG, "An error occurred while either fetching the Instance ID ");
				}
			}
		};

		/* Start Registration Service */
		if (checkPlayServices()){
			intent = new Intent(this, RegistrationIntentService.class);
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
		//SchoolBusiness.activityVisible = true;
		SchoolBusiness.setUpMain(getApplicationContext(), this);
		if (SchoolBusiness.loadLogin(getApplicationContext())){

			LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(Constants.REGISTRATION_COMPLETE));
			if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() == HomeFragment.class) {
				onCreateTab(mTab);
			}
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
		if (blankContainer == null || blankContent == null){
			blankContainer = new BlankFragment(); blankContent = new BlankFragment();
		}
		super.onResume();
		//getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("notification_filter"));
	}

	@Override
	protected void onPause(){
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		//SchoolBusiness.activityVisible = false;
		if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() == HomeFragment.class) {
			clearTabs();
		}
		if(SchoolBusiness.getProfile() != null) {
			SchoolBusiness.saveLogin(getApplicationContext());
		}
		super.onPause();
		//getApplicationContext().unregisterReceiver(mMessageReceiver);
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
		/* Set the notifications counter to the current value in class */
		notifications = menu.findItem(R.id.notifications);
		notifications.setTitle(SchoolBusiness.getNotificationCount());
		/* Initialize the Search View */
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);

		searchView = (SearchView) menu.findItem(R.id.event_search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {
					if (searchView != null) {
						if (!searchView.isIconified()) {
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
				sendVolley(Request.Method.GET, Constants.NULL, Constants.NOTIFICATIONS, null, true);
				return true;
			case R.id.menu_home:
				if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN).getClass() != HomeFragment.class) {
					HomeFragment homeFragment = new HomeFragment();
					swapFragment(homeFragment, R.id.fragment_container, FRAG_MAIN, true);
				}
				return true;
			case R.id.menu_action_settings:
				Log.d(TAG, "User selected get settings");
				sendVolley(Request.Method.GET, Constants.SETTINGS, Constants.USERS, null, true);
				return true;
			case R.id.menu_profile:
				Log.d(TAG, "User selected get profile");
				sendVolley(Request.Method.GET, Constants.PROFILE, Constants.USERS, null, true);
				return true;
			case R.id.menu_logout:
				Log.d(TAG, "User selected Logout");
				sendVolley(Request.Method.DELETE, Constants.SIGN_OUT, Constants.USERS, null, false);

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
		if (intent.getAction() == null) {
			Log.d(TAG, "Intent but no action");
			return;
		}
		Log.d(TAG, intent.getAction().toString());
		switch (intent.getAction()) {
			case Intent.ACTION_SEARCH:
				String query = intent.getStringExtra(SearchManager.QUERY);
				String encodedQuery = null;
				try {
					encodedQuery = java.net.URLEncoder.encode(query, Constants.UTF8);
				} catch (UnsupportedEncodingException ignored){/* Who Cares! */}
				//sendSearchVolley(query, searchModel, false);
				sendVolley(Request.Method.GET, "search="+encodedQuery, searchModel, null, true);
				break;
			case SchoolBusiness.ACTION_NOTIFICATION:
				Bundle extras = intent.getExtras();
				if (extras != null) {
					String notification_type = extras.getString(Constants.N_TYPE);
					Log.d("Notification Type", Constants.NULL + notification_type);
					switch (notification_type) {
						case Constants.AWARD_BADGE:
							BadgeAwardFragment badgeAwardFragment = BadgeAwardFragment.newInstance(extras);
							swapFragment(badgeAwardFragment, R.id.fragment_container, FRAG_MAIN, true);
							break;
						case "new_badge":
							BadgeViewFragment badgeViewFragment = BadgeViewFragment.newInstance(extras, true);
							swapFragment(badgeViewFragment, R.id.fragment_container, FRAG_MAIN, true);
							break;
						case "message":
							break;
						default:
							sendVolley(Request.Method.GET, extras.getString(Constants.EVENT_ID),
									Constants.EVENTS, null, true);
							break;
					}
				}
				break;
			case Intent.ACTION_VIEW:
				android.net.Uri uri = intent.getData();
				if (uri != null) {
					String path = uri.toString();
					path.replace(SchoolBusiness.TARGET, Constants.NULL);
					if (path.split("/").length >= 2) {
						String amodel = path.split("/", 1)[0];
						String aid = path.split("/", 1)[1];
						sendVolley(Request.Method.GET, aid, amodel, null, true);
					}
				}
				break;
		}
	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		switch (view.getId()){
			case R.id.events_checkBox:
				if (checked){
					searchModel = Constants.EVENTS;
					((CheckBox) findViewById(R.id.schools_checkBox)).setChecked(false);
					((CheckBox) findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.schools_checkBox:
				if (checked) {
					searchModel = Constants.SCHOOLS;
					((CheckBox) findViewById(R.id.events_checkBox)).setChecked(false);
					((CheckBox) findViewById(R.id.user_checkBox)).setChecked(false);
				}
				break;
			case R.id.user_checkBox:
				if (checked) {
					searchModel = Constants.USERS;
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
				SchoolBusiness.setRole(Constants.TEACHER);
				break;
			case R.id.register_speaker:
				SchoolBusiness.setRole(Constants.SPEAKER);
				break;
			case R.id.register_both:
				SchoolBusiness.setRole(Constants.BOTH);
				break;
		}
		Log.d(TAG, "Role is now set to " + SchoolBusiness.getRole());

	}

	/* Listener for ListItemFragment.java */
	/* Listener for ProfileFragment.java */
	public void onListItemSelected(String id, String model) {
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	/* Listener for ListItemFragment.java */
	public void onNotificationViewed(String id){
		sendVolley(Request.Method.POST, id, Constants.NOTIFICATIONS, null, true);
	}

	/* Listener for ListItemFragment.java */
	public void markAllNotificationsRead(){
		sendVolley(Request.Method.DELETE, Constants.NULL, Constants.NOTIFICATIONS, null, true);
	}

	/* Listener for EventViewFragment.java */
	public void onEventViewInteraction(String id, String model){
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	public void onSchoolViewInteraction(String id, String model){

	}

	/* Listener for UserProfileFragment.java */
	/* Listener for UserViewFragment.java */
	public void onUserViewInteraction(String id, String model){
		Boolean backtrack = true;
		sendVolley(Request.Method.GET, id, model, null, backtrack);
	}

	/* Listener for SearchOptionsFragment.java */
	public void onSearchInteraction(String model){
		searchModel = model;
	}

	/* Listener for HomeFragment.java */
	/* Listener for EventViewFragment.java */
	public void onCreateEvent(Boolean edit, String event){
		EventCreateFragment eventCreateFragment = EventCreateFragment.newInstance(edit, event);
		swapFragment(eventCreateFragment, R.id.fragment_container, FRAG_MAIN, true);//FRAG_CREATE_EVENT, true);
	}
	/* Listener for EventCreateFragment.java */
	public void onPostEvent(JSONObject event, Boolean edit){
		try {
			if (edit) {
				Log.d(TAG, event.toString());
				String event_id = event.getString(Constants.ID);
				sendVolley(Request.Method.PATCH, event_id, Constants.EVENTS, event, true);
			} else {
				Log.d(TAG, event.toString());
				sendVolley(Request.Method.POST, "create", Constants.EVENTS, event, true);
			}
			//postEvent(event, E_Type.EVENT_CREATE);
		} catch (JSONException e){
			//TODO
		}
	}

	/* Listener for EventViewFragment.java */
	public void onClaimEvent(String event){
		try{
			//JSONObject jsonObject = new JSONObject(event);
			//postEvent(jsonObject, E_Type.EVENT_CLAIM);
			sendVolley(Request.Method.POST, "claim_event", Constants.EVENTS, new JSONObject(event), true);
		} catch (JSONException e){

		}
	}

	/* Listener for EventViewFragment.java */
	/* Gets claims for a particular event */
	public void getClaims(String id){
		String res = "pending_claims?event_id="+id;
		sendVolley(Request.Method.GET, res, Constants.CLAIMS, null, false);
	}

	/* Listener for ClaimViewFragment.java */
	public void onAcceptClaim(String event_id, String claim_id){
		String res = "teacher_confirm?event_id="+event_id+"&claim_id="+claim_id;
		sendVolley(Request.Method.POST, res, Constants.CLAIMS, null, true);
	}

	/* Listener for ClaimViewFragment.java */
	public void onRejectClaim(String event_id, String claim_id){
		String res = claim_id+"/reject";
		sendVolley(Request.Method.DELETE, res, Constants.CLAIMS, null, true);
	}

	/* Listener for EventViewFragment.java */
	public void onCancelClaim(String claim_id){
		String res = claim_id+"/cancel";
		sendVolley(Request.Method.DELETE, res, Constants.CLAIMS, null, true);
	}

	/* Listener for EventViewFragment.java */
	public void onDeleteEvent(String id){
		DeleteEventDialogFragment fragment = new DeleteEventDialogFragment();
		fragment.show(getFragmentManager(), "delete_event");
		event_id = id;

	}

	/* Listener for DeleteEventDialogFragment.java */
	public void onDeleteEventPositiveClick(DialogFragment dialog){
		dialog.dismiss();
		sendVolley(Request.Method.DELETE, event_id + "/cancel", Constants.EVENTS, null, false);
	}

	/* Listener for DeleteEventDialogFragment.java */
	public void onDeleteEventNegativeClick(DialogFragment dialog){
		dialog.dismiss();
	}

	/* Listener for SchoolViewFragment.java */
	public void createSchoolFeed(View view, JSONObject response, String id){
		try {
			imageLoader = NetworkVolley.getInstance(getApplicationContext()).getImageLoader();
			NetworkImageView badge = (NetworkImageView) view.findViewById(R.id.school_badge);
			badge.setImageUrl(SchoolBusiness.AWS_S3 + response.getString("badge_url"), imageLoader);
		} catch (JSONException e){
			;
		}
		ListItemFragment event_list = ListItemFragment.newInstance(response, Constants.EVENTS, "school_id="+id);
		swapFragment(event_list, R.id.tab_content, TAB_CONTENT, false);
	}

	/* Listener for SchoolViewFragment.java */
	public void makeMySchool(String id){
		sendVolley(Request.Method.GET, "make_mine/" + id, Constants.SCHOOLS, null, true);
	}

	/* Listener for UserBadgesFragment.java */
	public void onBadgesLoad(View view, Map<Integer, String> badges){
		Iterator it = badges.entrySet().iterator();
		NetworkImageView badge;
		Map.Entry pair;
		while (it.hasNext()){
			pair = (Map.Entry)it.next();
			badge = (NetworkImageView) view.findViewById((int) pair.getKey());
			badge.setImageUrl(SchoolBusiness.AWS_S3 + pair.getValue(), imageLoader);
		}
	}

	/* Listener for BadgeAwardFragment.java */
	public void awardBadge(Boolean award, int event_id){
		JSONObject obj = new JSONObject();
		try {
			obj.put(Constants.AWARD, award);
			obj.put(Constants.EVENT_ID, event_id);
		} catch (JSONException e) {

		}
		sendVolley(Request.Method.POST, "award_badge", Constants.USERS, obj, true);
	}

	/* Listener for UserBadgesFragment.java */
	public void selectBadge(String user_id, String badge_id){
		sendVolley(Request.Method.GET, Constants.NULL + user_id + "/badges/" + badge_id, Constants.USERS, null, true);
	}

	/* Social Media Integration Functions */
	/* Listener for BadgeViewFragment.java */
	public void onShareBadgeFacebook(Uri badgeUrl){
		ShareLinkContent content = new ShareLinkContent.Builder()
				.setContentUrl(Uri.parse(SchoolBusiness.URL))
				.setContentTitle(SchoolBusiness.getUserAttr(Constants.NAME) +
						getString(R.string.awarded_badge))
				.setContentDescription("Badge awarded for speaking at")
				.setImageUrl(badgeUrl)
				.build();
	}

	/* Listener for BadgeViewFragment.java */
	public void onTweetBadge(Uri badgeUrl, String url) {
		try {
			twitter.composeTweet(this, SchoolBusiness.getUserAttr(Constants.NAME) + getString(R.string.awarded_badge),
					new URL(url),
					badgeUrl
					);
		} catch (MalformedURLException e){
			;
		}
	}

	/* Listener for HomeFragment.java */
	public void onCreateTab(int tab){
		CAN_GET_TABS = true;
		Log.d(TAG, "Creating Tabs");
		String[] event_tab_names = {Constants.ALL, Constants.APPROVAL, Constants.CLAIMS, Constants.CONFIRMED};
		String[] event_tab_display = {"All","Approval", "Claims", "Confirmed"};
		tabContainer = TabFragment.newInstance(mTab, event_tab_names, event_tab_display);
		swapFragment(tabContainer, R.id.tab_container, TAB_CONTAINER, false);
	}

	/* Listener for HomeFragment.java */
	public void clearTabs(){
//		swapFragment(new BlankFragment(),R.id.tab_container, TAB_CONTAINER, false);
//		swapFragment(new BlankFragment(),R.id.tab_content, TAB_CONTENT, false);
	}

	/* Listener for ProfileFragment.java */
	public void onEditProfile(){
		ProfileEditFragment profileEditFragment = new ProfileEditFragment();
		swapFragment(profileEditFragment, R.id.fragment_container, FRAG_MAIN, true);
	}

	/* Listener for ProfileEditFragment.java */
	public void onSaveProfile(JSONObject profile) {
		sendVolley(Request.Method.PUT, Constants.NULL, Constants.USERS, profile, true);
	}

	/* Listener for ProfileEditFragment.java */
	public void onFindMySchool(){
		searchView.setIconified(false);
		searchModel = Constants.SCHOOLS;
		((CheckBox) findViewById(R.id.schools_checkBox)).setChecked(true);
		((CheckBox) findViewById(R.id.events_checkBox)).setChecked(false);
		((CheckBox) findViewById(R.id.user_checkBox)).setChecked(false);
	}

	/* Listener for ProfileFragment.java */
	public void onEditAccount(){
		swapFragment(new AccountEditFragment(), R.id.fragment_container, FRAG_MAIN, true);
	}

	/* Listener for AccountEditFragment.java */
	public void onSaveAccount(JSONObject account){
		sendVolley(Request.Method.PUT, Constants.NULL, Constants.ACCOUNT, account, true);
	}

	/* Listener for SettingsFragment.java */
	public void onSaveSettings(JSONObject settings){
		findViewById(R.id.save_settings_button).setClickable(false);
		sendVolley(Request.Method.PUT, Constants.SETTINGS, Constants.USERS, settings, true);
	}

	public void onEventTabSelected(String tab){

	}

	/* Listener for UserViewFragment.java */
	public void createUserFeed(JSONObject response, String id){
		String[] tab_names = {Constants.PROFILE, Constants.BADGES};
		String[] tab_display = {"Profile", "Badges"};
		userProfileFragment = UserProfileFragment.newInstance(response.toString());
		userBadgesFragment = UserBadgesFragment.newInstance(response.toString());
		TabFragment tabFragment = TabFragment.newInstance(0, tab_names, tab_display);
		getSupportFragmentManager().beginTransaction().add(R.id.user_tab, tabFragment).commit();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.user_tab_content, userProfileFragment).commit();
		ListItemFragment event_list = ListItemFragment.newInstance(response, Constants.EVENTS, "user_id="+id);
		swapFragment(event_list, R.id.tab_content, TAB_CONTENT, false);
		CAN_GET_TABS = true;
	}

	/* Listener for ClaimViewFragment.java */
	/* Listener for UserViewFragment.java */
	public void onContactUser(String id, String name){
		MessageFragment messageFragment = MessageFragment.newInstance(id, name);
		swapFragment(messageFragment, R.id.fragment_container, FRAG_MAIN, true);
	}

	/* Listener for MessageFragment.java */
	public void onSendMessage(String id, JSONObject message){
		sendVolley(Request.Method.POST, id, Constants.SEND_MESSAGE, message, false);
	}

	@Override
	public void onTabChanged(String tab) {
		if (!CAN_GET_TABS){return;}
		switch (tab){
			case Constants.ALL:
				mTab = 0;
				sendVolley(Request.Method.GET, Constants.MY_EVENTS, Constants.EVENTS, null, false);
				break;
			case Constants.APPROVAL:
				mTab = 1;
				sendVolley(Request.Method.GET, Constants.PENDING_EVENTS, Constants.EVENTS, null, false);
				break;
			case Constants.CLAIMS:
				mTab = 2;
				sendVolley(Request.Method.GET, Constants.PENDING_CLAIMS, Constants.EVENTS, null, false);
				break;
			case Constants.CONFIRMED:
				mTab = 3;
				sendVolley(Request.Method.GET, Constants.CONFIRMED, Constants.EVENTS, null, false);
				break;
			case Constants.PROFILE:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.user_tab_content, userProfileFragment, "USER_TAB").commit();
				break;
			case Constants.BADGES:
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.user_tab_content, userBadgesFragment, "USER_TAB").commit();
				break;
			default:
				break;
		}
	}

	public void swapFragment(final Fragment fragment, final int container, final String tag, final Boolean backstack) {
		MainActivity.this.mainView.post(new Runnable() {
			public void run() {
				getSupportFragmentManager().executePendingTransactions();
				/* */
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
					//Log.d(TAG, f.getClass().toString());
					if ((f != null) && (!f.getClass().equals(BlankFragment.class))) {
						Log.d(TAG, f.getClass().toString());
						tabContainer = blankContainer;
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.tab_container, blankContainer, TAB_CONTAINER)
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
								.commit();
					}
					f = getSupportFragmentManager().findFragmentById(R.id.tab_content);
					if (!f.getClass().equals(BlankFragment.class)) {
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.tab_content, blankContent, TAB_CONTENT)
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
								.commit();
					}
				}
			}
		});
	}

//	public void sendSearchVolley(final String query, final String model, final boolean feed_mode)
//	{
//		String url = SchoolBusiness.TARGET + model;
//		String encodedUrl = null;
//		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
//				.getRequestQueue();
//		try {
//			if (feed_mode) {
//				encodedUrl = url + "?user=" + java.net.URLEncoder.encode(query, Constants.UTF8);//+"&event=");
//			} else {
//				encodedUrl = url + "?search=" + java.net.URLEncoder.encode(query, Constants.UTF8);//+"&event=");
//			}
//		} catch (UnsupportedEncodingException ignored) {
//			// Can be safely ignored
//		}
//		Log.d(TAG, query);
//		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,encodedUrl,null,
//				new Response.Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject response){
//						if(SchoolBusiness.DEBUG){Log.d("JSON", "Response: " + response.toString());}
//						ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model, id);
//						if (feed_mode){
//							FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//							transaction.commit();
//						} else {
//							swapFragment(listItemFragment, R.id.fragment_container, FRAG_MAIN, true);
//						}
//					}
//				}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error){
//				VolleyLog.d(TAG, "Error: " + error.getMessage());
//				Toast.makeText(getApplicationContext(),
//						error.getMessage(), Toast.LENGTH_SHORT).show();
//			}
//		}){
//			@Override
//			public String getBodyContentType(){
//				return "application/json";
//			}
//
//			@Override public Map<String, String> getHeaders() throws AuthFailureError {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("Content-Type", "application/json");
//				params.put("Accept", "application/json");
//				params.put("X-User-Email", SchoolBusiness.getEmail());
//				params.put("X-User-Token", SchoolBusiness.getUserAuth());
//				params.put(Constants.SEARCH, query);
////				Log.d(TAG,SchoolBusiness.getEmail()+" "+SchoolBusiness.getUserAuth());
////				Log.d(TAG, params.toString());
//				return params;
//			}
//			@Override
//			public Map<String, String> getParams(){
//				Map<String, String> params = new HashMap<String, String>();
//				params.put(Constants.SEARCH, query);
//				return params;
//			}
//		};
//		queue.add(jsonRequest);
//	}


	public void sendVolley(final int method,
	                       final String id,
	                       final String model,
	                       JSONObject obj,
	                       final Boolean backtrack){
		String delim;
		final boolean search;
		if (id.contains("search")){
			delim = "?";
			search = true;
		} else {
			if (id.isEmpty()){//   equals(Constants.NULL)){
				delim = Constants.NULL;
			} else {
				delim = "/";
			}
			search = false;
		}
		String url = SchoolBusiness.TARGET + model + delim + id;
		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
				.getRequestQueue();
		/* Create the Request */
		JsonObjectRequest jsonRequest = new JsonObjectRequest(method,url,obj,new Response.Listener<JSONObject>()
		{
			@Override
			public void onResponse(JSONObject response){
				if(SchoolBusiness.DEBUG){Log.d("JSON", "Response: " + response.toString());}
				if (search){
					ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model, id);
					swapFragment(listItemFragment, R.id.fragment_container, FRAG_MAIN, true);
					return;
				}
				switch (model) {
					case Constants.ACCOUNT:
						try {
							if (response.getString(Constants.STATE).equals(Constants.SUCCESS)){
								getSupportFragmentManager().popBackStack();
								getSupportFragmentManager().popBackStack();
								JSONObject profile = response.getJSONObject(Constants.USER);
								handleUserResponse(method, Constants.PROFILE, profile, backtrack);
							}
							else {
								findViewById(R.id.save_account_button).setClickable(true);
								Toast.makeText(getApplicationContext(),
												getString(R.string.error_incorrect_password),
												Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							handleJSONException(e);
						}
						break;
					case Constants.SEND_MESSAGE:
						try {
							if (response.getString(Constants.STATE).equals(Constants.SUCCESS)){
								onBackPressed();
								Toast.makeText(getApplicationContext(),
										getString(R.string.success_message),
										Toast.LENGTH_LONG).show();
							} else {
								findViewById(R.id.send_message_button).setClickable(true);
								Toast.makeText(getApplicationContext(),
										getString(R.string.failure_message),
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							handleJSONException(e);
							findViewById(R.id.send_message_button).setClickable(true);
						}
						break;
					case Constants.NOTIFICATIONS:
						handleNotificationResponse(response, id, backtrack);
						break;
					case Constants.EVENTS:
						handleEventResponse(method, model, id, response, backtrack);
						break;
					case Constants.SCHOOLS:
						handleSchoolResponse(method, id, response, backtrack);
						break;
					case Constants.USERS:
						handleUserResponse(method, id, response, backtrack);
						break;
					case Constants.CLAIMS:
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

	public void handleEventResponse(int method,
	                                String model,
	                                String id,
	                                JSONObject response,
	                                Boolean backtrack){
		EventViewFragment eventViewFragment;
		/* Handle Tabs */
		if (id.equals(Constants.MY_EVENTS) || id.equals(Constants.CONFIRMED) || id.equals(Constants.PENDING_EVENTS) || id.equals(Constants.PENDING_CLAIMS)){
			if (!CAN_GET_TABS){return;}
			tabContent = ListItemFragment.newInstance(response, model, id);
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
					refreshApp();
					break;
				/* Update an event */
				case Request.Method.PATCH:
					if (response.getString(Constants.STATE).equals(Constants.SUCCESS)) {
						getSupportFragmentManager().popBackStack();
						getSupportFragmentManager().popBackStack();
						eventViewFragment = EventViewFragment.newInstance(response.getJSONObject(Constants.EVENT));
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

						if (response.getString(Constants.STATE).equals(Constants.SUCCESS)) {
							Toast.makeText(getApplicationContext(),
									"Event posted", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString(Constants.EVENT));

							getSupportFragmentManager().popBackStackImmediate();
							sendVolley(Request.Method.GET, data.getString(Constants.ID), Constants.EVENTS, null, backtrack);
						} else {
							Log.d(TAG, response.getString("messages"));
							Toast.makeText(getApplicationContext(), "Failure to post event\n"+response.getString("messages"), Toast.LENGTH_LONG).show();
							findViewById(R.id.post_event_button).setClickable(true);
						}
					} else { /* Claim an Event */
						if (response.getString(Constants.STATE).equals(Constants.SUCCESS)){
							Toast.makeText(getApplicationContext(),"Event Claimed", Toast.LENGTH_SHORT).show();
							data = new JSONObject(response.getString(Constants.EVENT));
							getSupportFragmentManager().popBackStack();
							//getSupportFragmentManager().popBackStack();
							sendVolley(Request.Method.GET, data.getString(Constants.ID), Constants.EVENTS, null, true);
						}
					}
					break;
			}
		}catch (JSONException e){
			handleJSONException(e);
		}
	}

	public void handleBadgeResponse(JSONObject response){
			Bundle badge = SchoolBusiness.json2Bundle(response);
			BadgeViewFragment badgeViewFragment = BadgeViewFragment.newInstance(badge, false);
			swapFragment(badgeViewFragment, R.id.fragment_container, FRAG_MAIN, true);
	}

	public void handleClaimResponse(int method, String model, String id, JSONObject response){
		try {
			switch (method) {
				/* Handle a GET Claims Call */
				case Request.Method.GET:
					if (getSupportFragmentManager().findFragmentByTag(FRAG_MAIN)
									.getClass().equals(EventViewFragment.class)) {
						if (id.contains(Constants.PENDING_CLAIMS)) {
							ListItemFragment listItemFragment = ListItemFragment.newInstance(response, model, id);
							swapFragment(listItemFragment, R.id.claim_container, FRAG_CLAIM, false);
						} else {
							findViewById(R.id.layout_event_buttons).setVisibility(View.GONE);
							ClaimViewFragment claimViewFragment = ClaimViewFragment.newInstance(response);
							swapFragment(claimViewFragment, R.id.claim_container, FRAG_CLAIM, true);
						}
					}
					break;
				/* Handle a POST for a new claim */
				case Request.Method.POST:
					if (response.getString("status").equals(Constants.SUCCESS)) {
						JSONObject event = response.getJSONObject(Constants.EVENT);
						getSupportFragmentManager().popBackStack();
						getSupportFragmentManager().popBackStack();
						EventViewFragment eventViewFragment = EventViewFragment.newInstance(event);
						swapFragment(eventViewFragment, R.id.fragment_container, FRAG_MAIN, true);//FRAG_EVENT, true);
					}
					break;
				case Request.Method.DELETE:
					if (id.contains("reject")){
						Toast.makeText(this, "You have rejected " + response.getString(Constants.USER_NAME)
										+ "'s claim to event " + response.getString(Constants.EVENT_TITLE),
										Toast.LENGTH_LONG).show();
						refreshApp();
					} else if (id.contains("cancel")){
						refreshApp();
					}
					break;

			}
		}catch(JSONException e) {
			handleJSONException(e);
		}
	}

	public void handleSchoolResponse(int method, String id, JSONObject response, Boolean backtrack) {
		if (id.contains("make_mine")){
			Log.d(TAG, "Set School as Users");
			sendVolley(Request.Method.GET, Constants.PROFILE, Constants.USERS, null, true);
			return;
		}
		Log.d(TAG, "Getting School");
			SchoolViewFragment schoolViewFragment = SchoolViewFragment.newInstance(response);
			swapFragment(schoolViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
	}


	public void handleUserResponse(int method, String id, JSONObject response, Boolean backtrack) {
		if (id.contains(Constants.BADGES)){
			handleBadgeResponse(response);
			return;
		}
		switch (id) {
			case Constants.PROFILE:
				Log.d(TAG, "Updating User Profile");
				SchoolBusiness.updateProfile(response);
				ProfileFragment profileFragment = ProfileFragment.newInstance(response);
				swapFragment(profileFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				return;
			case Constants.SETTINGS:
				switch (method) {
					case Request.Method.GET:
						Log.d(TAG, "Getting User Settings");
						SettingsFragment settingsFragment = SettingsFragment.newInstance(response);
						swapFragment(settingsFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
						return;
					case Request.Method.PUT:
						Log.d(TAG, "Posting User Settings");

						//HomeFragment homeFragment = new HomeFragment();
						//swapFragment(homeFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
						Toast.makeText(getApplicationContext(),
								"Settings Saved",
								Toast.LENGTH_LONG).show();
						findViewById(R.id.save_settings_button).setClickable(true);
						return;
				}
			case Constants.AWARD_BADGE:
				onBackPressed();
				return;
			default:
				break;
		}

		UserViewFragment userViewFragment;
		switch (method) {
			case Request.Method.PUT:
				try {
					Log.d(TAG, "Posting User Profile");
					JSONObject user = response.getJSONObject(Constants.USER);
					SchoolBusiness.updateProfile(user);
					getSupportFragmentManager().popBackStack();
					getSupportFragmentManager().popBackStack();
					ProfileFragment profileFragment = ProfileFragment.newInstance(response);
					swapFragment(profileFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				} catch (JSONException e) {
					handleJSONException(e);
				}
				break;
			case Request.Method.GET:
				Log.d(TAG, "Getting User Profile");
				userViewFragment = UserViewFragment.newInstance(response);
				swapFragment(userViewFragment, R.id.fragment_container, FRAG_MAIN, backtrack);
				break;
			case Request.Method.DELETE:
				/* Logging User Out */
				SchoolBusiness.clearLogin(getApplicationContext());
				refreshApp();
				break;
		}
	}

	public void handleNotificationResponse(JSONObject response, String id, Boolean backtrack){
		try {
			updateNotifications(response.getString(Constants.COUNT));
			if (id.equals(Constants.NULL)){
				ListItemFragment notifications = ListItemFragment.newInstance(response, Constants.NOTIFICATIONS, id);
				swapFragment(notifications, R.id.fragment_container, FRAG_MAIN, backtrack);
				clearTabs();
			}
		} catch (JSONException e){
			handleJSONException(e);
		}
	}

	public void SharingToSocialMedia(String application) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, "Test text");
		startActivity(Intent.createChooser(intent, "Share using"));
	}

	public void refreshApp(){
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	public void updateNotifications(String count){
		SchoolBusiness.setNotificationCount(count);
		notifications.setTitle(SchoolBusiness.getNotificationCount());
	}

	public void handleJSONException(JSONException e){
		e.printStackTrace();
		Toast.makeText(getApplicationContext(),
				"Error: " + e.getMessage(),
				Toast.LENGTH_LONG).show();
	}

	public void updateCount(){
		if (notifications != null) {
			MainActivity.this.mainView.post(new Runnable() {
				public void run() {
					notifications.setTitle(SchoolBusiness.getNotificationCount());
				}
			});
		}
	}
}
