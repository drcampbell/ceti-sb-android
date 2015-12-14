package com.ceti_sb.android.test;

import com.ceti_sb.android.LoginActivity;
import com.ceti_sb.android.R;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by nandu on 12/13/2015.
 */
public class TestEditAccount extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public TestEditAccount() {
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

        //Test edit account section of the application
        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_profile);
        //Click on Edit Account
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.edit_account_button));
        //Click on Speaker
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.register_speaker));
        //Click on Empty Text View
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.password));
        //Enter the text: 'testtest'
        solo.clearEditText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password));
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password), "testtest");
        //Click on Save Account
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.save_account_button));
        //Set default small timeout to 1000 milliseconds
        Timeout.setSmallTimeout(2000);

        assertTrue("com.ceti_sb.android.MainActivity is not found!", solo.waitForActivity(com.ceti_sb.android.MainActivity.class));

        //Click on Edit Account
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.edit_account_button));
        //Click on Both
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.register_both));
        //Click on Empty Text View
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.password));
        //Enter the text: 'testtest'
        solo.clearEditText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password));
        solo.enterText((android.widget.EditText) solo.getView(com.ceti_sb.android.R.id.password), "testtest");
        //Click on Save Account
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.save_account_button));
        //Set default small timeout to 1000 milliseconds
        Timeout.setSmallTimeout(1000);

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