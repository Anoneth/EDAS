package com.example.edas;

import android.content.Context;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;


import com.example.edas.activity.EventActivity;
import com.example.edas.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public IntentsTestRule<MainActivity> mMainActivityActivityTestRule =
            new IntentsTestRule<>(MainActivity.class);
    @Test
    public void checkCreateActivity() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.edas", appContext.getPackageName());

        onView(withId(R.id.fabCreate)).perform(click());
        intended(hasComponent(EventActivity.class.getName()));
    }

    @Test
    public void checkEventActivity() {
        onView(withId(R.id.fabCreate)).perform(click());
        onView(withId(R.id.editTextTitle)).perform(typeText("Some Title"));
        onView(withId(R.id.editTextTitle)).check(matches(withText("Some Title")));
    }
}
