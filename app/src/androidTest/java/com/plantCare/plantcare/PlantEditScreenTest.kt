package com.plantCare.plantcare

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.plantCare.plantcare.repository.FakePlantDetailRepository
import com.plantCare.plantcare.repository.FakePlantRepository
import com.plantCare.plantcare.service.FakeSensorService
import com.plantCare.plantcare.ui.screens.plantEditScreen.PlantEditScreen
import com.plantCare.plantcare.viewModel.EditMode
import com.plantCare.plantcare.viewModel.NoteEditViewModel
import com.plantCare.plantcare.viewModel.PlantEditViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class PlantEditScreenTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    lateinit var viewModel: PlantEditViewModel
    lateinit var sensorService: FakeSensorService
    lateinit var plantRepository: FakePlantRepository
    lateinit var plantDetailRepository: FakePlantDetailRepository

    @Before
    fun init() {
        hiltRule.inject()

        sensorService = FakeSensorService()
        plantRepository = FakePlantRepository()
        plantDetailRepository = FakePlantDetailRepository()

        viewModel = PlantEditViewModel(
            plantRepository = plantRepository,
            plantDetailsRepository = plantDetailRepository,
            sensorService = sensorService,
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "mode" to EditMode.ADD,
                    "plantId" to 1L,
                )
            ),
        )
    }

    @Test
    fun empty_plant_name_shows_error_on_save() {
        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("save", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Plant name can't be empty.").assertIsDisplayed()
    }

    @Test
    fun empty_plant_species_shows_error_on_save() {
        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("save", ignoreCase = true).performClick()
        composeTestRule.onNodeWithText("Plant species can't be empty.").assertIsDisplayed()
    }

    @Test
    fun sensor_button_scan_no_sensor_timeout() {
        sensorService.hasSensor = false
        sensorService.timeoutMillis = 1000L

        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Add Sensor").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Sensor").performClick()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.onNodeWithText("Scanning").assertIsDisplayed()

        composeTestRule.mainClock.advanceTimeBy(10000)

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Add Sensor")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Add Sensor").assertIsDisplayed()
        composeTestRule.onNodeWithText("Failed to find sensor", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun sensor_button_scan_has_sensor() {
        sensorService.hasSensor = true
        sensorService.timeoutMillis = 1000L

        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Add Sensor").performClick()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.onNodeWithText("Scanning").assertIsDisplayed()

        composeTestRule.mainClock.advanceTimeBy(10000)

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText("Remove Sensor")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Remove Sensor").assertIsDisplayed()
    }

    @Test
    fun day_selection() {
        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        // polish day name initials
        val toSelect = listOf("P", "W", "C")

        toSelect.map {
            composeTestRule.onAllNodesWithText(it).onFirst().performClick()
        }

        toSelect.map {
            composeTestRule.onAllNodesWithText(it).onFirst().assertIsOn()
        }
    }

    @Test
    fun day_unselection() {
        composeTestRule.setContent {
            PlantEditScreen(viewModel = viewModel)
        }

        // polish day name initials
        val toSelect = setOf("P", "W", "C", "S")
        val toUnselect = setOf("W", "S")

        toSelect.map {
            composeTestRule.onAllNodesWithText(it).onFirst().performClick()
        }


        toUnselect.map {
            composeTestRule.onAllNodesWithText(it).onFirst().performClick()
        }

        (toSelect - toUnselect).map {
            composeTestRule.onAllNodesWithText(it).onFirst().assertIsOn()
        }

        toUnselect.map {
            composeTestRule.onAllNodesWithText(it).onFirst().assertIsOff()
        }
    }
}