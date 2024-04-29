package little.goose.navigation.app.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import little.goose.navigation.app.PopRedirectActivity
import little.goose.navigation.app.PopRedirectTag
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestPopRedirectActivity {
    @get:Rule
    var popRedirectActivityScenarioRule = activityScenarioRule<PopRedirectActivity>()

    @Test
    fun test_pop_redirect() {
        val scenario = popRedirectActivityScenarioRule.scenario

        onView(
            withTagValue(`is`(PopRedirectTag.NAVIGATE_TO_TWO))
        ).perform(click())

        onView(
            withTagValue(`is`(PopRedirectTag.NAVIGATE_TO_THREE))
        ).perform(click())

        onView(
            withTagValue(`is`(PopRedirectTag.POP))
        ).perform(click())

        scenario.recreate()

        onView(
            withTagValue(`is`(PopRedirectTag.NAVIGATE_TO_TWO))
        ).check { _, noViewFoundException ->
            assertThat(noViewFoundException == null, `is`(true))
        }
    }
}