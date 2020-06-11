package com.flamevision.findiro;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UpdateTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void updateTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnSignUpValue), withText("sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                5),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.SignUp_EmailValue),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("asd1@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.SignUp_NameValue),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                4),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Theo"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.SignUp_PassValue),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                5),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("asd123"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnSignUp), withText("Sign up"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                6),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.toolbar),
                                childAtPosition(
                                        withClassName(is("com.google.android.material.appbar.AppBarLayout")),
                                        0)),
                        2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        7),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.etName), withText("Theo"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                3)),
                                1),
                        isDisplayed()));
        editText.check(matches(withText("Theo111")));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.etName), withText("Theo"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("Theo11"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.etName), withText("Theo11"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.etName), withText("Theo11"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.etName), withText("Theo11"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("Theo1111"));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.etName), withText("Theo1111"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.etName), withText("Theo1111"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.etName), withText("Theo1111"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText("Theo111"));

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.etName), withText("Theo111"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText11.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.EditName), withText("Change Name"),
                        childAtPosition(
                                allOf(withId(R.id.LinearFragment),
                                        childAtPosition(
                                                withId(R.id.FragmentRelative),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatButton3.perform(click());
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
