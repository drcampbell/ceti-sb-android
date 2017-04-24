package com.ceti_sb.android.test;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.ceti_sb.android.R;
import com.ceti_sb.android.registration.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Registration_UITest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void registration_UITest() {
        ViewInteraction button = onView(
                allOf(withId(R.id.register_button), withText("Register"),
                        withParent(allOf(withId(R.id.email_login_form),
                                withParent(withId(R.id.login_form))))));
        button.perform(scrollTo(), click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.name),
                        withParent(allOf(withId(R.id.email_register_form),
                                withParent(withId(R.id.register_form))))));
        editText.perform(scrollTo(), replaceText("rama"), closeSoftKeyboard());

        ViewInteraction autoCompleteTextView = onView(
                allOf(withId(R.id.email),
                        withParent(allOf(withId(R.id.email_register_form),
                                withParent(withId(R.id.register_form))))));
        autoCompleteTextView.perform(scrollTo(), replaceText("testram91@gmail.com"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.password),
                        withParent(allOf(withId(R.id.email_register_form),
                                withParent(withId(R.id.register_form))))));
        editText2.perform(scrollTo(), replaceText("ontojith"), closeSoftKeyboard());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.confirm_password),
                        withParent(allOf(withId(R.id.email_register_form),
                                withParent(withId(R.id.register_form))))));
        editText3.perform(scrollTo(), replaceText("ontojith"), closeSoftKeyboard());

        ViewInteraction radioButton = onView(
                allOf(withId(R.id.register_speaker), withText("Speaker"),
                        withParent(allOf(withId(R.id.radio_group_role),
                                withParent(withId(R.id.email_register_form))))));
        radioButton.perform(scrollTo(), click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.register_button), withText("Register"),
                        withParent(allOf(withId(R.id.email_register_form),
                                withParent(withId(R.id.register_form))))));
        button2.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.title), withText("All"), isDisplayed()));
        textView.perform(click());

        //ViewInteraction textView2 = onView(
//                allOf(withId(android.R.id.title), withText("All"),
//                        childAtPosition(
//                                childAtPosition(
//                                        withId(android.R.id.tabs),
//                                        0),
//                                0),
//                        isDisplayed()));
       // textView2.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
