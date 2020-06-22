package com.flamevision.findiro;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.StringRes;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void emailIsEmpty() {
        onView(withId(R.id.EmailValue)).perform(clearText());
        onView(withId(R.id.btnSignValue)).perform(click());
        onView(withId(R.id.EmailValue)).check(matches(withError("You must enter your email.")));
    }

    @Test
    public void passwordIsEmpty() {
        onView(withId(R.id.EmailValue)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(R.id.PassValue)).perform(clearText());
        onView(withId(R.id.btnSignValue)).perform(click());
        onView(withId(R.id.PassValue)).check(matches(withError("You must enter your password.")));
    }

    @Test
    public void loginFailedWrongEmail() {
        onView(withId(R.id.EmailValue)).perform(typeText("incorrect@email.com"), closeSoftKeyboard());
        onView(withId(R.id.PassValue)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btnSignValue)).perform(click());
        onView(withId(R.id.EmailValue)).check(matches(withError("No account exists with this email.")));
    }

    @Test
    public void loginFailedWrongPassword() {
        onView(withId(R.id.EmailValue)).perform(typeText("test@findiro.com"), closeSoftKeyboard());
        onView(withId(R.id.PassValue)).perform(typeText("findiro1234"), closeSoftKeyboard());
        onView(withId(R.id.btnSignValue)).perform(click());
        onView(withId(R.id.PassValue)).check(matches(withError("Wrong password.")));
    }

    @Test
    public void loginSuccessfully_shouldShowWelcomeMessage() {
        onView(withId(R.id.EmailValue)).perform(typeText("test@findiro.com"), closeSoftKeyboard());
        onView(withId(R.id.PassValue)).perform(typeText("findiro123"), closeSoftKeyboard());
        onView(withId(R.id.btnSignValue)).perform(click());
        onView(withId(R.id.fragment_title)).check(matches(withText("Home")));
    }

    private String getString(@StringRes int resourceId) {
        return activityTestRule.getActivity().getString(resourceId);
    }

    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof EditText) {
                    return ((EditText)item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message" + expected + ", find it!");
            }
        };
    }
}
