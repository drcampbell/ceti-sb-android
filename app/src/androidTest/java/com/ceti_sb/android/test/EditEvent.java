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
public class EditEvent {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void editEvent() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.email),
                        withParent(allOf(withId(R.id.email_login_form),
                                withParent(withId(R.id.login_form))))));
        editText.perform(scrollTo(), replaceText("jith87@gmail.com"), closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.password),
                        withParent(allOf(withId(R.id.email_login_form),
                                withParent(withId(R.id.login_form))))));
        editText2.perform(scrollTo(), replaceText("ontojith"), closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(R.id.sign_in_button), withText("Sign in"),
                        withParent(allOf(withId(R.id.email_login_form),
                                withParent(withId(R.id.login_form))))));
        button.perform(scrollTo(), click());

        ViewInteraction twoLineListItem = onView(
                allOf(childAtPosition(
                        withId(android.R.id.list),
                        0),
                        isDisplayed()));
        twoLineListItem.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(R.id.edit_button), withText("Edit Event"), isDisplayed()));
        button2.perform(click());

        ViewInteraction editText3 = onView(
                withId(R.id.ET_content));
        editText3.perform(scrollTo(), replaceText("no content"), closeSoftKeyboard());

//        pressBack();
//
//        ViewInteraction checkBox = onView(
//                allOf(withId(R.id.start_pm), withText("PM")));
//        checkBox.perform(scrollTo(), click());

//        ViewInteraction checkBox2 = onView(
//                allOf(withId(R.id.end_pm), withText("PM")));
//        checkBox2.perform(scrollTo(), click());
//
//        ViewInteraction checkBox3 = onView(
//                allOf(withId(R.id.end_pm), withText("PM")));
//        checkBox3.perform(scrollTo(), click());

        ViewInteraction button3 = onView(
                allOf(withId(R.id.post_event_button), withText("Update Event")));
        button3.perform(scrollTo(), click());

        ViewInteraction button4 = onView(
                allOf(withId(R.id.edit_button),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.layout_event_buttons),
                                        0),
                                0),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));

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
