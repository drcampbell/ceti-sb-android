package com.ceti_sb.android.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;
import com.ceti_sb.android.volley.NetworkVolley;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegistrationIntentService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = {"global"};

	// TODO: Rename actions, choose action names that describe tasks that this
	// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
	private static final String ACTION_FOO = "com.ceti_sb.android.action.FOO";
	private static final String ACTION_BAZ = "com.ceti_sb.android.action.BAZ";

	// TODO: Rename parameters
	private static final String EXTRA_PARAM1 = "com.ceti_sb.android.extra.PARAM1";
	private static final String EXTRA_PARAM2 = "com.ceti_sb.android.extra.PARAM2";

	/**
	 * Starts this service to perform action Foo with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionFoo(Context context, String param1, String param2) {
		Intent intent = new Intent(context, RegistrationIntentService.class);
		intent.setAction(ACTION_FOO);
		intent.putExtra(EXTRA_PARAM1, param1);
		intent.putExtra(EXTRA_PARAM2, param2);
		context.startService(intent);
	}

	/**
	 * Starts this service to perform action Baz with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 *
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startActionBaz(Context context, String param1, String param2) {
		Intent intent = new Intent(context, RegistrationIntentService.class);
		intent.setAction(ACTION_BAZ);
		intent.putExtra(EXTRA_PARAM1, param1);
		intent.putExtra(EXTRA_PARAM2, param2);
		context.startService(intent);
	}

	public RegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			// [START register_for_gcm]
			// Initially this call goes out to the network to retrieve the token, subsequent calls
			// are local.
			// [START get_token]
			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
					GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			// [END get_token]
			Log.i(TAG, "GCM Registration Token: " + token);


			sendRegistrationToServer(token);

			// Subscribe to topic channels
			subscribeTopics(token);

			// You should store a boolean that indicates whether the generated token has been
			// sent to your server. If the boolean is false, send the token to your server,
			// otherwise your server should have already received the token.
			sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, true).apply();
			// [END register_for_gcm]
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating our registration data
			// on a third-party server, this ensures that we'll attempt the update at a later time.
			sharedPreferences.edit().putBoolean(Constants.SENT_TOKEN_TO_SERVER, false).apply();
		}
		// Notify UI that registration has completed, so the progress indicator can be hidden.
		//Intent registrationComplete = new Intent(SchoolBusiness.REGISTRATION_COMPLETE);
		//LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
	}

	/**
	 * Persist registration to third-party servers.
	 *
	 * Modify this method to associate the user's GCM registration token with any server-side account
	 * maintained by your application.
	 *
	 * @param token The new token.
	 */
	private void sendRegistrationToServer(String token) {
		String url = SchoolBusiness.getTarget() + "register_device";
		JSONObject registration = new JSONObject();
		try {
			registration.put("token", token);
			registration.put("device_name", Build.MODEL);
		} catch (JSONException e){
			Toast.makeText(getApplicationContext(), "Couldn't create json", Toast.LENGTH_LONG).show();
		}
		RequestQueue queue = NetworkVolley.getInstance(getApplicationContext())
				.getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,registration,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){

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

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token GCM token
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token) throws IOException {
		for (String topic : TOPICS) {
			GcmPubSub pubSub = GcmPubSub.getInstance(this);
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}
	// [END subscribe_topics]

	/**
	 * Handle action Foo in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionFoo(String param1, String param2) {
		// TODO: Handle action Foo
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Handle action Baz in the provided background thread with the provided
	 * parameters.
	 */
	private void handleActionBaz(String param1, String param2) {
		// TODO: Handle action Baz
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
