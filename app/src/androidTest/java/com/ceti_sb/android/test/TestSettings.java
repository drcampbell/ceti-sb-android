package com.ceti_sb.android.test;

import com.ceti_sb.android.LoginActivity;
import com.ceti_sb.android.R;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by nandu on 12/13/2015.
 */
public class TestSettings extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public TestSettings() {
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
        //Login to the application
        //Wait for activity: 'com.ceti_sb.android.LoginActivity'
        solo.waitForActivity(com.ceti_sb.android.LoginActivity.class, 2000);
        //Set default small timeout to 1000 milliseconds
        Timeout.setSmallTimeout(1000);
        //Enter the text: 'test@test.com'
        solo.clearEditText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.email));
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.email), "test@test.com");
        //Click on Empty Text View
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.password));
        //Enter the text: 'testtest'
        solo.clearEditText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password));
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password), "testtest");
        //Click on Sign in
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.sign_in_button));
        //Wait for activity: 'com.ceti_sb.android.MainActivity'
        assertTrue("com.ceti_sb.android.MainActivity is not found!", solo.waitForActivity(com.ceti_sb.android.MainActivity.class));

        //Tests for settings section
        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_action_settings);
        //Click on Event Updates
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.set_event_updates));
        //Click on Save Settings
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.save_settings_button));
        //Press menu back key
        //solo.goBack();

        //Wait for activity: 'com.ceti_sb.android.MainActivity'
        assertTrue("com.ceti_sb.android.MainActivity is not found!", solo.waitForActivity(com.ceti_sb.android.MainActivity.class));

        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_action_settings);
        //Click on Event Updates
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.set_event_updates));
        //Click on Save Settings
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.save_settings_button));
        //Press menu back key
        //solo.goBack();

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