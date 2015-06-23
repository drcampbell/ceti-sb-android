package com.school_business.android.school_business;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ProfileActivity extends Activity {//ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

	    TextView tv = (TextView) findViewById(R.id.textViewName);
	    tv.setText(SchoolBusiness.getUserAttr("name"));
	    tv = (TextView) findViewById(R.id.textViewEmail);
	    tv.setText(SchoolBusiness.getUserAttr("email"));
	    tv = (TextView) findViewById(R.id.textViewSchool);
	    tv.setText("School: " + SchoolBusiness.getUserAttr("school_id"));
	    tv = (TextView) findViewById(R.id.textViewBio);
	    tv.setText("Bio: " + SchoolBusiness.getUserAttr("biography"));
	    tv = (TextView) findViewById(R.id.textViewJob);
	    tv.setText("Job Title: " + SchoolBusiness.getUserAttr("job_title"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
