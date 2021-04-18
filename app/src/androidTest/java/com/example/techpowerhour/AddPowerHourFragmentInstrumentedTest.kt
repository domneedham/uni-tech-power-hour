package com.example.techpowerhour

import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.techpowerhour.TestHelpers.hasErrorText
import com.example.techpowerhour.TestHelpers.hasNoErrorText
import com.example.techpowerhour.data.model.enums.PowerHourType
import com.example.techpowerhour.ui.add_power_hour.AddPowerHourFragment
import com.example.techpowerhour.util.DateHelper
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddPowerHourFragmentInstrumentedTest {
    private fun getScenario(): FragmentScenario<AddPowerHourFragment> {
        return launchFragmentInContainer<AddPowerHourFragment>(
            themeResId = R.style.Theme_MaterialComponents,
            fragmentArgs = bundleOf(TEST_MODE to true)
        )
    }

    private fun getScenarioEdit(): FragmentScenario<AddPowerHourFragment> {
        return launchFragmentInContainer<AddPowerHourFragment>(
            themeResId = R.style.Theme_MaterialComponents,
            fragmentArgs = bundleOf(TEST_MODE to true, "EDIT" to true)
        )
    }

    private fun checkErrorMessageIsDisplayed(
        fragmentScenario: FragmentScenario<AddPowerHourFragment>,
        id: Int,
        errorMessageId: Int
    ) {
        var messageText = ""
        fragmentScenario.onFragment { fragment ->
            messageText = fragment.getString(errorMessageId)
        }

        onView(withId(id))
            .check(matches(hasErrorText(messageText)))
    }

    private fun checkErrorMessageIsCleared(id: Int, ) {
        onView(withId(id))
            .check(matches(hasNoErrorText()))
    }

    private fun clickSaveButton() {
        onView(withId(R.id.floatingActionButton))
            .perform(click())
    }

    private fun enterWorkoutName(text: String) {
        onView(withId(R.id.workoutNameText))
            .perform(typeText(text))
    }

    private fun enterDuration(duration: String) {
        onView(withId(R.id.durationText))
            .perform(typeText(duration))
    }

    private fun enterDate(year: Int, month: Int, day: Int) {
        onView(withId(R.id.datePickerLayout))
            .perform(click())
        onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(year, month, day))
        onView(withId(android.R.id.button1))
            .perform(click())
    }

    private fun enterPowerHourType(type: PowerHourType) {
        onView(withId(R.id.typeLayout))
            .perform(click())
        onData(equalTo(type))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
    }

    @Test
    fun verifyErrorIsDisplayedIfTryingToSaveWithNoName() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.workoutNameLayout, R.string.add_ph_name_missing)
    }

    @Test
    fun verifyErrorIsDisplayedIfTryingToSaveWithNoDuration() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(
            scenario,
            R.id.durationLayout,
            R.string.add_ph_duration_missing
        )
    }

    @Test
    fun verifyErrorIsDisplayedIfTryingToSaveWithNoType() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.typeLayout, R.string.add_ph_type_missing)
    }

    @Test
    fun verifyErrorIsDisplayedIfTryingToSaveWithNoDate() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.datePickerLayout, R.string.add_ph_date_missing)
    }

    @Test
    fun verifyErrorIsDisplayedIfTryingToSaveWithDurationThatIsNotANumber() {
        val scenario = getScenario()
        clickSaveButton()
        onView(withId(R.id.durationText))
            .perform(typeText("NaN"))
        checkErrorMessageIsDisplayed(
            scenario,
            R.id.durationLayout,
            R.string.add_ph_duration_not_number
        )
    }

    @Test
    fun verifyErrorIsClearedAfterTryingToSaveWithNoName() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.workoutNameLayout, R.string.add_ph_name_missing)

        enterWorkoutName("Name")
        clickSaveButton()
        checkErrorMessageIsCleared(R.id.workoutNameLayout)
    }

    @Test
    fun verifyErrorIsClearedAfterTryingToSaveWithNoDuration() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(
            scenario,
            R.id.durationLayout,
            R.string.add_ph_duration_missing
        )

        enterDuration("45")
        clickSaveButton()
        checkErrorMessageIsCleared(R.id.durationLayout)
    }

    @Test
    fun verifyErrorIsClearedAfterTryingToSaveWithNoType() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.typeLayout, R.string.add_ph_type_missing)

        enterPowerHourType(PowerHourType.Run)
        clickSaveButton()
        checkErrorMessageIsCleared(R.id.typeLayout)
    }

    @Test
    fun verifyErrorIsClearedAfterTryingToSaveWithNoDate() {
        val scenario = getScenario()
        clickSaveButton()
        checkErrorMessageIsDisplayed(scenario, R.id.datePickerLayout, R.string.add_ph_date_missing)

        enterDate(2021, 4, 18)
        clickSaveButton()
        checkErrorMessageIsCleared(R.id.datePickerLayout)
    }

    @Test
    fun verifyErrorIsClearedAfterTryingToSaveWithDurationThatIsNotANumber() {
        val scenario = getScenario()
        clickSaveButton()
        enterDuration("NaN")
        checkErrorMessageIsDisplayed(
            scenario,
            R.id.durationLayout,
            R.string.add_ph_duration_not_number
        )

        enterDuration("45")
        clickSaveButton()
        checkErrorMessageIsCleared(R.id.durationLayout)
    }

    @Test
    fun verifySaveWorksIfFieldsAreEnteredCorrectly() {
        // setup navigation to avoid errors on save
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        val scenario = getScenario()

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.mobile_navigation)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.workoutNameText))
            .perform(typeText("Name"))
        onView(withId(R.id.durationText))
            .perform(typeText("45"))
        enterPowerHourType(PowerHourType.Run)
        enterDate(2021, 4, 18)

        clickSaveButton()
    }

    @Test
    fun verifyIfEditingPowerHourTheNameIsAlreadyFilled() {
        getScenarioEdit()
        onView(withId(R.id.workoutNameText))
            .check(matches(withText("Test")))
    }

    @Test
    fun verifyIfEditingPowerHourTheDurationIsAlreadyFilled() {
        getScenarioEdit()
        onView(withId(R.id.durationText))
            .check(matches(withText("30.0")))
    }

    @Test
    fun verifyIfEditingPowerHourTheTypeIsAlreadyFilled() {
        getScenarioEdit()
        onView(withId(R.id.typeText))
            .check(matches(withText("Run")))
    }

    @Test
    fun verifyIfEditingPowerHourTheDateIsAlreadyFilled() {
        getScenarioEdit()
        onView(withId(R.id.datePickerText))
            .check(matches(withText(DateHelper.displayDate(DateHelper.todayEpoch))))
    }
}