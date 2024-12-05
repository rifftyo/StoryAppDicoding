package com.rifftyo.storyappdicoding.ui.upload

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UploadViewModelTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(UploadActivity::class.java)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun testUploadImage() {
        onView(withId(R.id.uploadButton)).check(matches(isNotEnabled()))

        onView(withId(R.id.galleryButton)).perform(click())

        onView(withId(R.id.description)).perform(typeText("Testing Upload"), closeSoftKeyboard())

        onView(withId(R.id.uploadButton)).check(matches(isEnabled()))

        onView(withId(R.id.uploadButton)).perform(click())

        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

}