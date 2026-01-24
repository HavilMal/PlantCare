package com.plantCare.plantcare

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.plantCare.plantcare.repository.FakeNotesRepository
import com.plantCare.plantcare.ui.screens.noteEditScreen.NoteEditScreen
import com.plantCare.plantcare.viewModel.EditMode
import com.plantCare.plantcare.viewModel.NoteEditViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NoteEditScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    lateinit var viewModel: NoteEditViewModel

    @Before
    fun init() {
        hiltRule.inject()

        viewModel = NoteEditViewModel(
            notesRepository = FakeNotesRepository(),
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "mode" to EditMode.ADD,
                    "plantId" to 1L,
                    "noteId" to 1L
                )
            )
        )
    }

    @Test
    fun noteEditScreen_InputTitle_DisplaysText() {
        composeTestRule.setContent {
            NoteEditScreen(viewModel = viewModel)
        }

        composeTestRule
            .onNodeWithText("Title")
            .performTextInput("My Great Note")

        composeTestRule
            .onNodeWithText("My Great Note")
            .assertIsDisplayed()
    }

    @Test
    fun noteEditScreen_empty_title_shows_error_on_save() {
        composeTestRule.setContent {
            NoteEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("save").performClick()

        composeTestRule.onNodeWithText("Note title can't be empty").assertIsDisplayed()
    }

    @Test
    fun noteEditScreen_empty_content_shows_error_on_save() {
        composeTestRule.setContent {
            NoteEditScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithContentDescription("save").performClick()

        composeTestRule.onNodeWithText(
            "Note content can't be empty"
        ).assertIsDisplayed()
    }
}