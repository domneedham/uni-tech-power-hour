package com.example.techpowerhour

import android.app.Application
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techpowerhour.data.model.LeaderboardUser
import com.example.techpowerhour.ui.leaderboard.LeaderboardFragment
import org.hamcrest.core.IsNot.not
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LeaderboardFragmentInstrumentedTest {
    private val userOne = LeaderboardUser("one", "User One", 5)
    private val userTwo = LeaderboardUser("two", "User Two", 5)
    private val emptyList = ArrayList<LeaderboardUser>()
    private val populatedListOne = arrayListOf(userOne)
    private val populatedListTwo = arrayListOf(userOne, userTwo)

    private fun getScenario(): FragmentScenario<LeaderboardFragment> {
        return launchFragmentInContainer<LeaderboardFragment>(
                themeResId = R.style.Theme_TechPowerHour
        )
    }

    private fun createOptionMenu(option: Int) : ActionMenuItem {
        // Create a dummy menu item with the desired item id.
        val context = ApplicationProvider.getApplicationContext<Application>()
        return ActionMenuItem(context, 0, option, 0, 0, null)
    }

    @Test
    fun verifyTitleDisplaysForDateSelectionOfDay() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_today)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
        }

        onView(withId(R.id.leaderboard_date_range_title))
            .check(matches(withText(R.string.leaderboard_title_today)))
    }

    @Test
    fun verifyTitleDisplaysForDateSelectionOfWeek() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_week)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
        }

        onView(withId(R.id.leaderboard_date_range_title))
            .check(matches(withText(R.string.leaderboard_title_week)))
    }

    @Test
    fun verifyTitleDisplaysForDateSelectionOfMonth() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_month)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
        }

        onView(withId(R.id.leaderboard_date_range_title))
            .check(matches(withText(R.string.leaderboard_title_month)))
    }

    @Test
    fun verifyNoItemsFoundTextIsDisplayedIfLeaderboardIsNotPopulatedForToday() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_today)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(emptyList)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyNoItemsFoundTextIsDisplayedIfLeaderboardIsNotPopulatedForWeek() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_week)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(emptyList)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyNoItemsFoundTextIsDisplayedIfLeaderboardIsNotPopulatedForMonth() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_month)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(emptyList)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyNoItemsFoundTextIsNotDisplayedIfLeaderboardIsPopulatedForToday() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_today)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyNoItemsFoundTextIsNotDisplayedIfLeaderboardIsPopulatedForWeek() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_week)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyNoItemsFoundTextIsNotDisplayedIfLeaderboardIsPopulatedForMonth() {
        val scenario = getScenario()

        val addMenuItem = createOptionMenu(R.id.action_date_range_month)

        //Call onOptionsItemSelected with the dummy menu item
        scenario.onFragment { fragment ->
            fragment.onOptionsItemSelected(addMenuItem)
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.leaderboard_list_empty_text))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun verifyFirstItemInLeaderboardListStartsAtNumberOne() {
        val scenario = getScenario()

        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.mtrl_list_trailing_text))
            .check(matches(withText("#1")))
    }

    @Test
    fun verifyItemInLeaderboardUsesNameAsPrimaryText() {
        val scenario = getScenario()

        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.mtrl_list_item_text))
            .check(matches(withText(userOne.name)))
    }

    @Test
    fun verifyItemInLeaderboardUsesPointsAsSecondaryText() {
        val scenario = getScenario()

        scenario.onFragment { fragment ->
            fragment.updateDisplay(populatedListOne)
        }

        onView(withId(R.id.mtrl_list_item_secondary_text))
            .check(matches(withText(userOne.points.toString())))
    }
}