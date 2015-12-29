package com.ceti_sb.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
	public static final String TAG = "GCMListenerService";
	private int rcode = 0;
	public MyGcmListenerService() {
	}

	@Override
	public void onMessageReceived(String from, Bundle data){
		Log.d(TAG, "Data"+data.toString());
		String message = data.getString("message");
		String event_id = data.getString("event_id");
		String n_type = data.getString("n_type");
		String notif_count = data.getString("count");
		Log.d(TAG, "From: " + from);
		Log.d(TAG, "Message: " + message);
		Log.d(TAG, "Notification: " + n_type);
		Log.d(TAG, "Event: " + event_id);

		if (from.startsWith("/topics/")) {
			// message received from some topic.
		} else {
			// normal downstream message.
		}

		/**
		 * Production applications would usually process the message here.
		 * Eg: - Syncing with server.
		 *     - Store message in local database.
		 *     - Update UI.
		 */

		/**
		 * In some cases it may be useful to show a notification indicating to the user
		 * that a message was received.
		 */
		SchoolBusiness.setNotificationCount(notif_count);
		sendNotification(message, data);
	}

	/**
	 * Create and show a simple notification containing the received GCM message.
	 *
	 * @param message GCM message received.
	 */
	private void sendNotification(String message, Bundle data) {
		PendingIntent pendingIntent;
		if (SchoolBusiness.getProfile() != null) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(SchoolBusiness.ACTION_NOTIFICATION);
			intent.putExtras(data);
			pendingIntent = PendingIntent.getActivity(this, rcode++ /* Request code */, intent,
					PendingIntent.FLAG_ONE_SHOT);
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			pendingIntent = PendingIntent.getActivity(this, rcode++, intent, PendingIntent.FLAG_ONE_SHOT);
		}

		Log.d("message", message);
		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("School Business")
				.setContentText(message)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}
}
