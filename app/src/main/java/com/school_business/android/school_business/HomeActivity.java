package com.school_business.android.school_business;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity implements View.OnClickListener{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View myEvent1 = (TextView) findViewById(R.id.event1);
        myEvent1.setOnClickListener(this);
        View myEvent2 = (TextView) findViewById(R.id.event2);
        myEvent2.setOnClickListener(this);
        View btnCreateRequest = (Button) findViewById(R.id.create_request);
        btnCreateRequest.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){

            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

        int id = item.getItemId();
        if (id == R.id.search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_request:
                startActivity(new Intent(this, CreateRequestActivity.class));
                break;
            case R.id.event1:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.event2:
                startActivity(new Intent(this, Event2Activity.class));
                break;
        }
    }
}
