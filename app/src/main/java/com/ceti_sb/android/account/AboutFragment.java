package com.ceti_sb.android.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.regex.Pattern;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.widget.TextView;

import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;

import org.json.JSONObject;
import com.ceti_sb.android.application.Constants;
import com.ceti_sb.android.R;
import com.ceti_sb.android.application.SchoolBusiness;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.text.method.LinkMovementMethod;
import android.text.Html;


/**
 * Created by babu on 1/25/17.
 */

public class AboutFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.about_page, container, false);
        // setContentView(R.layout.about_page);
//        LinearLayout display = (LinearLayout) view.findViewById(R.id.layout_fragment_about);
//
//        TextView myCustomLink = new TextView(getContext());
//
//        Pattern pattern = Pattern.compile("[a-zA-Z]+&");
//
//        myCustomLink.setText("press one of these words to search it on google: Android Linkify dzone");
//        Linkify.addLinks(myCustomLink,pattern, "http://ontoborn.com",myMatchFilter, null);
//        display.addView(myCustomLink);
//        TextView textView =(TextView)view.findViewById(R.id.aboutlink);
//        textView.setClickable(true);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        String text = "<a href='http://www.google.com'> Google </a>";
//        textView.setText(Html.fromHtml(text));



        TextView btnontoborn = (TextView) view.findViewById(R.id.aboutontolink);
        btnontoborn.setOnClickListener(this);

        ImageView btnimageclick = (ImageView) view.findViewById(R.id.ontoborn);
        btnimageclick.setOnClickListener(this);

//        TextView btnceti = (TextView) view.findViewById(R.id.aboutcetilink);
//        btnceti.setOnClickListener(this);
//
//        TextView btngrant = (TextView) view.findViewById(R.id.aboutgrantlink);
//        btngrant.setOnClickListener(this);
//
//        TextView btnPrivacy = (TextView) view.findViewById(R.id.aboutPrivacylink);
//        btnPrivacy.setOnClickListener(this);
        return view;


    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view){
        switch ( view.getId() ){

            case R.id.aboutontolink:
                Intent browserontoborn = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ontoborn.com"));
                startActivity(browserontoborn);
                break;

            case R.id.ontoborn:
                Intent imgOntoBorn = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ontoborn.com"));
                startActivity(imgOntoBorn);
                break;
//            case R.id.aboutcetilink:
//                Intent cetiLink = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ceti.cse.ohio-state.edu/"));
//                startActivity(cetiLink);
//                break;
//            case R.id.aboutgrantlink:
//                Intent grantLink = new Intent(Intent.ACTION_VIEW, Uri.parse("http://education.ohio.gov/Topics/Straight-A-Fund/"));
//                startActivity(grantLink);
//                break;
//            case R.id.aboutPrivacylink:
//                Intent grantprivacy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.school2biz.com/privacy_policy"));
//                startActivity(grantprivacy);
//                break;


        }

    }
    public void clickMe(){

    }


}