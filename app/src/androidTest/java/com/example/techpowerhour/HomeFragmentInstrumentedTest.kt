package com.example.techpowerhour

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techpowerhour.ui.home.HomeFragment

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentInstrumentedTest {

    private fun getScenario(): FragmentScenario<HomeFragment> {
        return launchFragmentInContainer<HomeFragment>(
                themeResId = R.style.Theme_TechPowerHour
        )
    }

    @Test
    fun verifyCompanyPointsStatisticsAreDisplayedForTheDay() {
        getScenario()

        onView(withId(R.id.company_points_layout_today_title))
            .check(matches(withText(R.string.home_company_statistics_title_today)))

        // check this is displayed. points are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_points_layout_today_text))
            .check(matches(isDisplayed()))
    }
    @Test
    fun verifyCompanyPointsStatisticsAreDisplayedForTheWeek() {
        getScenario()

        onView(withId(R.id.company_points_layout_week_title))
            .check(matches(withText(R.string.home_company_statistics_title_week)))

        // check this is displayed. points are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_points_layout_week_text))
            .check(matches(isDisplayed()))
    }
    @Test
    fun verifyCompanyPointsStatisticsAreDisplayedForTheMonth() {
        getScenario()

        onView(withId(R.id.company_points_layout_month_title))
            .check(matches(withText(R.string.home_company_statistics_title_month)))

        // check this is displayed. points are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_points_layout_month_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun verifyCompanyPowerHourTotalStatisticsAreDisplayedForTheDay() {
        getScenario()

        onView(withId(R.id.company_power_hours_layout_today_title))
            .check(matches(withText(R.string.home_company_statistics_title_today)))

        // check this is displayed. power hours are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_power_hours_layout_today_text))
            .check(matches(isDisplayed()))
    }
    @Test
    fun verifyCompanyPowerHourTotalStatisticsAreDisplayedForTheWeek() {
        getScenario()

        onView(withId(R.id.company_power_hours_layout_week_title))
            .check(matches(withText(R.string.home_company_statistics_title_week)))

        // check this is displayed. power hours are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_power_hours_layout_week_text))
            .check(matches(isDisplayed()))
    }
    @Test
    fun verifyCompanyPowerHourTotalStatisticsAreDisplayedForTheMonth() {
        getScenario()

        onView(withId(R.id.company_power_hours_layout_month_title))
            .check(matches(withText(R.string.home_company_statistics_title_month)))

        // check this is displayed. power hours are fetched so may fail in test if trying to use
        // specific points until testing is improved with local db.
        onView(withId(R.id.company_power_hours_layout_month_text))
            .check(matches(isDisplayed()))
    }
}