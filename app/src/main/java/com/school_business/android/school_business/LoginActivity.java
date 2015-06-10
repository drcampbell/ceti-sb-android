package com.school_business.android.school_business;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
    private EditText userEmailText;
    private EditText userPasswordText;
    private final static String OPT_NAME = "name";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailText = (EditText) findViewById(R.id.email);
        userPasswordText = (EditText) findViewById(R.id.password);
        View btnLogin = (Button) findViewById(R.id.sign_in_button);
        btnLogin.setOnClickListener(this);
        View btnRegister = (Button) findViewById(R.id.register_button);
        btnRegister.setOnClickListener(this);
    }

    private void checkLogin() {

        startActivity(new Intent(this, HomeActivity.class));

        /*
        String email = this.userEmailText.getText().toString();
        String password = this.userPasswordText.getText().toString();
        List<String> names = this.dh.selectAll(username, password);
        if (names.size() > 0) { // Login successful
            // Save username as the name of the player
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(OPT_NAME, username);
            editor.commit();

            // Bring up the GameOptions screen
            startActivity(new Intent(this, GameOptions.class));
            finish();
        } else {
            // Try again?
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Login failed")
                    .setNeutralButton("Try Again",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            }).show();
        }
        */
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                checkLogin();
                break;
            case R.id.register_button:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
        }
    }
}
