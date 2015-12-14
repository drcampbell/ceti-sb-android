package com.ceti_sb.android.test;

import com.ceti_sb.android.LoginActivity;
import com.ceti_sb.android.R;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by nandu on 12/13/2015.
 */
public class TestRegistration extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public TestRegistration() {
        super(LoginActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testRun() {
        //Registration for the application
        //Wait for activity: 'com.ceti_sb.android.LoginActivity'
        solo.waitForActivity(com.ceti_sb.android.LoginActivity.class, 2000);
        //Set default small timeout to 1000 milliseconds
        Timeout.setSmallTimeout(1000);
        //Click on register
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.register_button));
        //Wait for activity: 'com.ceti_sb.android.SignUpActivity'
        solo.waitForActivity(com.ceti_sb.android.SignUpActivity.class, 2000);
        //Set default small timeout to 1000 milliseconds
        Timeout.setSmallTimeout(1000);

        //Enter the text: Test User
        solo.enterText((android.widget.EditText) solo.getView(R.id.name), "Test User");
        //Enter the text: 'test@test.com'
        solo.enterText((android.widget.EditText) solo.getView(R.id.email), "a@b.com");
        //Click on Empty Text View
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.password));
        //Enter the text: 'testtest'
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password), "testtest");
        //Click on Empty Text View
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.confirm_password));
        //Enter the text: 'testtest'
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.confirm_password), "testtest");
        //Click on Both
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.register_both));
        //Click on Register
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.register_button));
        //Wait for activity: 'com.ceti_sb.android.MainActivity'
        assertTrue("com.ceti_sb.android.MainActivity is not found!", solo.waitForActivity(com.ceti_sb.android.MainActivity.class));

        //Logout steps
        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_logout);
        //Wait for activity: 'com.ceti_sb.android.LoginActivity'
        assertTrue("com.ceti_sb.android.LoginActivity is not found!", solo.waitForActivity(com.ceti_sb.android.LoginActivity.class));
        //Press menu back key
        solo.goBack();
    }
}