package com.example.board_gamer_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SuggestionsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule= InstantTaskExecutorRule()

    private val mockDb: FirebaseFirestore = mock(
        FirebaseFirestore::class.java,
        org.mockito.Answers.RETURNS_DEEP_STUBS
    )
    private lateinit var viewModel: SuggestionsViewModel

    @Before
    fun setup() {
        viewModel = SuggestionsViewModel(mockDb)
    }

    @Test
    fun `addSuggestion mit leerem Titel fügt keine Suggestion hinzu`() {
        viewModel.onTitleChange("   ")
        viewModel.addSuggestion("event1", "TestUser")
        //Parameter werden nicht zurückgesetzt, wenn Funktion addSuggestion nicht ausgeführt wird
        assertEquals("   ", viewModel.titleInput)
    }

    @Test
    fun `addReview mit leerem Text fügt kein Review hinzu`() {
        viewModel.onReviewChange("")
        viewModel.onRatingChange(4)
        viewModel.addReview("event1", "TestUser")

        assertEquals(4, viewModel.selectedRating)
    }

    @Test
    fun `addReview mit Rating 0 fügt kein Review hinzu`() {
        viewModel.onReviewChange("Toller Abend")
        viewModel.onRatingChange(0)
        viewModel.addReview("event1", "TestUser")

        assertEquals("Toller Abend", viewModel.reviewInput)
    }

    @Test
    fun `addReview mit leerem Text nach trim() fügt kein Review hinzu`() {
        viewModel.onReviewChange("   ")
        viewModel.onRatingChange(3)
        viewModel.addReview("event1", "TestUser")

        assertEquals(3, viewModel.selectedRating)
    }

    @Test
    fun `onDismissDialog setzt Titel zurück`() {
        viewModel.onTitleChange("Catan")
        viewModel.onDismissDialog()

        assertEquals("", viewModel.titleInput)
    }

    @Test
    fun `onDismissDialog setzt Beschreibung zurück`() {
        viewModel.onDescriptionChange("Strategiespiel")
        viewModel.onDismissDialog()

        assertEquals("", viewModel.descriptionInput)
    }

    @Test
    fun `onTitleChange aktualisiert titleInput`() {
        viewModel.onTitleChange("Catan")
        assertEquals("Catan", viewModel.titleInput)
    }

    @Test
    fun `onDescriptionChange aktualisiert descriptionInput`() {
        viewModel.onDescriptionChange("Strategiespiel")
        assertEquals("Strategiespiel", viewModel.descriptionInput)
    }

    @Test
    fun `onReviewChange aktualisert reviewInput`() {
        viewModel.onReviewChange("Toller Abend")
        assertEquals("Toller Abend", viewModel.reviewInput)
    }

    @Test
    fun `onRatingChange aktualisiert ratingInput`() {
        viewModel.onRatingChange(5)
        assertEquals(5, viewModel.selectedRating)
    }

    @Test
    fun `onDeleteSuggestionDialog setzt deleteSuggestionDialog auf true`() {
        viewModel.onDeleteSuggestionDialog()
        assertTrue(viewModel.deleteSuggestionDialog)
    }

    @Test
    fun `onDismissDeleteSuggestionDialog setzt deleteSuggestionDialog auf false`() {
        viewModel.onDeleteSuggestionDialog()
        viewModel.onDismissDeleteSuggestionDialog()
        assertTrue(!viewModel.deleteSuggestionDialog)
    }

    @Test
    fun `onDeleteReveiwDialog setzt deleteReviewDialog auf true`() {
        viewModel.onDeleteReviewDialog()
        assertTrue(viewModel.deleteReviewDialog)
    }

    @Test
    fun `onDismissDeleteReviewDialog setzt deleteReviewDialog auf false`() {
        viewModel.onDeleteReviewDialog()
        viewModel.onDismissDeleteReviewDialog()
        assertTrue(!viewModel.deleteReviewDialog)
    }
}