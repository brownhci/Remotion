package org.hitlabnz.sensor_fusion_demo.androidTest;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.hitlabnz.sensor_fusion_demo.R;
import org.bhi.sfClient.SensorSelectionActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

///**
// * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
// */
//public class ApplicationTest extends ApplicationTestCase<Application> {
//    public ApplicationTest() {
//        super(Application.class);
//    }
//}

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationTest {

    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            SensorSelectionActivity.class);

    @Test
    public void checkAssertMenuExists(){
        DiscardGyroscopeWarning();

        // Act & Assert
        AssertThatAboutActionMenuExists();
    }

    @Test
    public void clickAboutMenu_expectAboutToBeDisplayed(){
        DiscardGyroscopeWarning();

        // Act
        onView(withId(R.id.action_about)).perform(click());

        onView(withId(R.id.webViewAbout)).check(matches(ViewMatchers.isDisplayed()));
    }


    public void AssertThatAboutActionMenuExists() {
        onView(withId(R.id.action_about)).check(matches(withText("About")));
    }

    public static void DiscardGyroscopeWarning() {
        try {
            onView(withText("OK")).perform(click());
        } catch (NoMatchingViewException e) {
            //view not displayed logic
        }
    }
}