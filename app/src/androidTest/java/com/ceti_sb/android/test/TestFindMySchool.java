package com.ceti_sb.android.test;

import com.ceti_sb.android.registration.LoginActivity;
import com.ceti_sb.android.R;
import com.ceti_sb.android.controller.MainActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by nandu on 12/13/2015.
 */
public class TestFindMySchool extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    public TestFindMySchool() {
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
        //Wait for activity: 'com.ceti_sb.android.registration.LoginActivity'
        solo.waitForActivity(LoginActivity.class, 2000);
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
        //Wait for activity: 'com.ceti_sb.android.controller.MainActivity'
        assertTrue("com.ceti_sb.android.controller.MainActivity is not found!", solo.waitForActivity(MainActivity.class));

        //Test find my school functionality
        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_profile);
        //Click on Edit Profile
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.edit_profile_button));
        //Click on Find My School
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.find_school_button));
        //Enter the text: 'high'
        //solo.clickOnView(solo.getView(R.id.search_src_text));
        solo.clearEditText((android.widget.EditText) solo.getView(R.id.search_src_text));
        solo.enterText((android.widget.EditText) solo.getView(R.id.search_src_text), "high");
        //solo.typeText(1,"high");
        //Press next button
        solo.pressSoftKeyboardNextButton();
        //Scroll to 2nd item in the list
        android.widget.ListView listView0 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
        solo.scrollListToLine(listView0, 2);
        //Click on 2nd item in the list
        solo.clickOnView(solo.getView(android.R.id.text1, 4));
        //Click on Make My School
        solo.clickOnView(solo.getView(com.ceti_sb.android.R.id.make_my_school_button));
        //Set default small timeout to 2000 milliseconds
        Timeout.setSmallTimeout(2000);

        assertTrue("com.ceti_sb.android.controller.MainActivity is not found!", solo.waitForActivity(MainActivity.class));

        //Logout steps
        //Click on action bar item
        solo.clickOnActionBarItem(com.ceti_sb.android.R.id.menu_logout);
        //Wait for activity: 'com.ceti_sb.android.registration.LoginActivity'
        assertTrue("com.ceti_sb.android.registration.LoginActivity is not found!", solo.waitForActivity(LoginActivity.class));
        //Press menu back key
        solo.goBack();
    }
}