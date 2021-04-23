package com.example.techpowerhour

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techpowerhour.util.TestHelpers.clickOnViewChild
import com.example.techpowerhour.util.TestHelpers.hasItemAtPosition
import com.example.techpowerhour.data.model.PowerHour
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.ui.user_power_hour_list.UserPowerHourListFragment
import com.example.techpowerhour.util.DateHelper
import org.hamcrest.core.IsNot.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserPowerHourListFragmentInstrumentedTest {
    private val phOne = PowerHour("Today", 30.0, PowerHourType.Walk, DateHelper.todayEpoch, "One")
    private val phTwo = PowerHour("Yesterday", 30.0, PowerHourType.Run, DateHelper.todayEpoch - 1, "Two")
    private val emptyList = ArrayList<PowerHour>()
    private val populatedListOne = arrayListOf(phOne)
    private val populatedListTwo = arrayListOf(phOne, phTwo)
    private val populatedListToChangeOrder = arrayListOf(phTwo, phOne)

    private fun getScenario() : FragmentScenario<UserPowerHourListFragment> {
        return launchFragmentInContainer(
                themeResId = R.style.Theme_TechPowerHour,
                fragmentArgs = bundleOf(TEST_MODE to true)
        )
    }

    @Test
    fun verifyPowerHourListInformationTextIsShownIfUserHasNoPowerHours() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(emptyList)
        }

        onView(withId(R.id.user_power_hour_list_empty_text))
                .check(matches(isDisplayed()))
    }

    @Test
    fun verifyPowerHourListInformationTextIsNotShownIfUserHasPowerHours() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.user_power_hour_list_empty_text))
                .check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyUserPowerHourListIsDisplayedIfUserHasPowerHours() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.user_power_hour_list_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyPowerHourNameIsShownInTopLineText() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.mtrl_list_item_text))
                .check(matches(withText(phOne.name)))
    }

    @Test
    fun verifyPowerHourDateIsShownInBottomLineText() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.mtrl_list_item_secondary_text))
                .check(matches(withText(DateHelper.displayDate(phOne.epochDate!!))))
    }

    @Test
    fun verifyUserPowerHourListIsShownInDateDescendingOrder() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            // this list needs reversing so that should be handled
            fragment.updateDisplay(populatedListToChangeOrder)
        }

        onView(hasItemAtPosition(0, hasDescendant(withText(phOne.name))))
                .check(matches(isDisplayed()))

        onView(hasItemAtPosition(1, hasDescendant(withText(phTwo.name))))
                .check(matches(isDisplayed()))
    }

    @Test
    fun verifyEditButtonNavigatesUserToEditFragment() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
                ApplicationProvider.getApplicationContext())

        val scenario = getScenario()

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.mobile_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)

            fragment.updateDisplay(populatedListOne)
        }

        // Verify that performing a click changes the NavController’s state
        onView(withId(R.id.user_power_hour_list_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        clickOnViewChild(R.id.mtrl_list_item_icon_edit)
                ))

        assert(navController.currentDestination?.id!! == R.id.navigation_add_power_hour)
    }

    @Test
    fun verifyDeleteButtonBringsUpDialogForConfirmation() {
        val scenario = getScenario()
        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.user_power_hour_list_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                        0,
                        clickOnViewChild(R.id.mtrl_list_item_icon_delete)
                ))

        onView(withText("Are you sure you want to delete ${phOne.name}?"))
                .check(matches(isDisplayed()))
    }
}