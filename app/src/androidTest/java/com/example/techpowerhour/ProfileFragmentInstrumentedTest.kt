package com.example.techpowerhour

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.techpowerhour.ui.home.HomeFragment
import com.example.techpowerhour.ui.profile.ProfileFragment
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentInstrumentedTest {
    private val username = "Test User"
    private val points = 3
    private val powerHours = 1

    private fun getScenario(): FragmentScenario<ProfileFragment> {
        return launchFragmentInContainer<ProfileFragment>(
            themeResId = R.style.Theme_AppCompat,
            fragmentArgs = bundleOf(TEST_MODE to true)
        )
    }

    @Test
    fun verifyUsernameIsDisplayed() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateName(username)
        }

        onView(withId(R.id.profile_header_name))
            .check(matches(withText(username)))
    }

    @Test
    fun verifyUserPointsStatisticsAreDisplayed() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateTotalPointsText(points)
        }

        onView(withId(R.id.points_text))
            .check(matches(withText("You have earned $points points")))
    }

    @Test
    fun verifyUserPowerHourStatisticsAreDisplayed() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateTotalPowerHoursCompletedText(powerHours)
        }

        onView(withId(R.id.number_workouts_text))
            .check(matches(withText("You have completed $powerHours Power Hour")))
    }

    @Test
    fun verifyViewAllPowerHoursNavigatesToUserPowerHourListFragment() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val scenario = getScenario()

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.mobile_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.controls_power_hour_list_layout))
            .perform(click())

        assert(navController.currentDestination?.id!! == R.id.navigation_user_power_hour_list)
    }

    @Test
    fun verifySignoutButtonBringsUpDialogForConfirmation() {
        getScenario()

        onView(withId(R.id.account_signout_layout))
            .perform(click())

        onView(withText("Are you sure you want to signout?"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyClickFABNavigatesToAddPowerHourFragment() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val scenario = getScenario()

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.mobile_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.fab))
            .perform(click())

        assert(navController.currentDestination?.id!! == R.id.navigation_add_power_hour)
    }
}