package com.ceti_sb.android;

import android.content.Context;

import com.android.volley.toolbox.NetworkImageView;
import com.ceti_sb.android.R;

/**
 * Created by david on 11/3/15.
 */
public class BadgeImageView extends NetworkImageView {
	public BadgeImageView(Context context){
		super(context, null, R.style.Badge);
	}
}