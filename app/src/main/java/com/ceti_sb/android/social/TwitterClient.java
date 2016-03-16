package com.ceti_sb.android.social;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ceti_sb.android.volley.NetworkVolley;
import com.ceti_sb.android.application.SchoolBusiness;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import io.fabric.sdk.android.Fabric;

/**
 * Created by David Campbell on 11/19/15.
 * https://docs.fabric.io/android/twitter/compose-tweets.html
 */
public class TwitterClient {
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
	private static String TWITTER_KEY = "ECHCzw5TH8FFn2ngIGYWdGgd2";
	private static String TWITTER_SECRET = "yVFTFodV5xSSNP55h7fG019F02spX6bGnh8jgqKbqqjFAz0dJQ";

	public TwitterClient(){
		;
	}

	public void initialize(Context context){
		TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(context, new Twitter(authConfig));
		Fabric.with(context, new TwitterCore(authConfig), new TweetComposer());
	}

	public void getCredentials(Context context){
		RequestQueue queue = NetworkVolley.getInstance(context)
				.getRequestQueue();
		String url = SchoolBusiness.getUrl() + "tk";
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response){
						try {
							TWITTER_KEY = response.getString("TWITTER_KEY");
							TWITTER_SECRET = response.getString("TWITTER_SECRET");
						}catch (JSONException e){
							;
							}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error){
				;
			}
		}){
			@Override
			public String getBodyContentType(){
				return "application/json";
			}
		};
		queue.add(jsonRequest);
	}

	/* https://docs.fabric.io/android/twitter/twitter.html#set-up-kit */
	public void something(){
//		TwitterCore core = Twitter.core;
//		TweetUi tweetUi = Twitter.tweetUi;
//		TweetComposer composer = Twitter.tweetComposer;
//		Digits digits = Twitter.digits;
	}

	/* More information: https://docs.fabric.io/android/twitter/compose-tweets.html */
	public void composeTweet(Context context, String text, URL url, Uri imgUri){
		TweetComposer.Builder builder = new TweetComposer.Builder(context)
				.text(text)
				.url(url)
				.image(imgUri);
		builder.show();
	}
}
