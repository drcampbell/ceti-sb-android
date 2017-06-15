package com.ceti_sb.android.account;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ceti_sb.android.R;
import com.ceti_sb.android.BuildConfig;


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




        TextView btnontoborn = (TextView) view.findViewById(R.id.aboutontolink);
        btnontoborn.setOnClickListener(this);

        ImageView btnimageclick = (ImageView) view.findViewById(R.id.ontoborn);
        btnimageclick.setOnClickListener(this);

        TextView btnPrivacyclick = (TextView) view.findViewById(R.id.aboutPrivacylink);
        btnPrivacyclick.setOnClickListener(this);

        TextView versionNumber = (TextView) view.findViewById(R.id.versionNumber);


        String versionName = BuildConfig.VERSION_NAME;

        String versionNumberStr = "Version number : " + versionName;
        versionNumber.setText(versionNumberStr);

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
            case R.id.aboutPrivacylink:
                Intent privacyLink = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ceti-test-env.elasticbeanstalk.com/privacy_policy"));
                startActivity(privacyLink);
                break;
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
