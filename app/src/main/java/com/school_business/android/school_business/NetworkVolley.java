package com.school_business.android.school_business;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.LruCache;

import org.apache.http.client.methods.HttpPost;

import java.net.HttpURLConnection;
import java.net.URL;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
//import com.github.volley_examples.toolbox.BitmapLruCache;

/**
 * Created by david on 6/10/15.
 */
public class NetworkVolley {

	private static NetworkVolley mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

//    private NetworkVolley() {
//
//    }

//    static void init(Context context) {
//    	mRequestQueue = Volley.newRequestQueue(context);
//
//    	int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
//    				.getMemoryClass();
//    	int cacheSize = 1024 ** 2 * memClass / 8;
//    	mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
//    }

    
    private NetworkVolley(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
		Log.d("VOLLEY", "Volley started");
	    if (mRequestQueue != null){
		    Log.d("VOLLEY", "Request Queue isn't NULL");
	    }
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized NetworkVolley getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkVolley(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
/*
	public poster(){
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost();
		} catch (IllegalArgumentException e){
			;
		}
	}
	{
		URL url = new URL(hostName);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		readStream(con.getInputStream());
	} catch (Exception e) {
		e.printStackTrace();;
	}
	*/

//	public boolean isNetworkAvailable() {
//		ConnectivityManager cm = (ConnectivityManager)
//				getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//		if (networkInfo != null && networkInfo.isConnected()){
//			return true;
//		}
//		return false;
//	}

}
