package little.goose.navigation.app.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import little.goose.navigation.ViewContainer
import little.goose.navigation.app.MAIN_NAVIGATOR
import little.goose.navigation.app.MainActivity
import little.goose.navigation.app.MainTag
import little.goose.navigation.app.MainValue
import little.goose.navigation.app.R
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var mainActivityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun common_operate_test_of_MainActivity() {

        val scenario = mainActivityScenarioRule.scenario

        // ensure I have a navigator in content.
        onView(withTagValue(`is`(MAIN_NAVIGATOR))).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            assertThat(view, `is`(instanceOf(ViewContainer::class.java)))
        }

        // click button to navigate to screen two.
        onView(withId(R.id.bt_nav_to_screen_two)).perform(click())
        onView(withTagValue(`is`(MainTag.ScreenTwo.CONTAINER))).check { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            assertThat(
                "Navigate to screen two finish",
                view.isAttachedToWindow,
                `is`(true)
            )
        }


        // check out screen two.
        val screenTwoTitle = onView(withTagValue(`is`(MainTag.ScreenTwo.TITLE)))
            .check(matches(withText(MainValue.SCREEN_TWO_TITLE_FROM_ONE)))

        scenario.recreate()

        onView(withTagValue(`is`(MainTag.ScreenTwo.TITLE)))
            .check(matches(not(screenTwoTitle))) // ensure view recreate
            .check(matches(withText(MainValue.SCREEN_TWO_TITLE_FROM_ONE))) // ensure title restored

        // navigate to screen three.
        onView(withTagValue(`is`(MainTag.ScreenTwo.BUTTON_NAVIGATE_TO_COROUTINE)))
            .perform(click())

        // back to screen one
        onView(withTagValue(`is`(MainTag.ScreenThree.BUTTON_BACK_TO_SCREEN_ONE)))
            .perform(click())


        // check args from screen three
        val screenOneBeforeRecreate = onView(withId(R.id.tv_title))
            .check(matches(withText(MainValue.SCREEN_ONE_TITLE_FROM_THREE)))

        scenario.recreate()

        onView(withId(R.id.tv_title))
            .check(matches(not(screenOneBeforeRecreate))) // ensure view recreate
            .check(matches(withText(MainValue.SCREEN_ONE_TITLE_FROM_THREE))) // ensure title restored
    }

}