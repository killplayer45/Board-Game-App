package com.example.board_gamer_app.ui.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.board_gamer_app.data.model.*
//import com.example.board_gamer_app.util.updateVote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SuggestionsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()        //manages database tasks
    private val _suggestions = MutableStateFlow<List<GameSuggestion>>(emptyList())      //Suggestions State managed by this ViewModel
    val suggestions: StateFlow<List<GameSuggestion>> = _suggestions.asStateFlow()               //Suggestions State accessible for other components (read-only)
    private val _reviews = MutableStateFlow<List<HostReview>>(emptyList())              //Reviews State managed by this ViewModel
    val reviews: StateFlow<List<HostReview>> = _reviews.asStateFlow()                           //Reviews State accessible for other components (read-only)

    //Dialog State
    var titleInput by mutableStateOf("")
        private set
    var descriptionInput by mutableStateOf("")
        private set
    var reviewInput by mutableStateOf("")
        private set
    var selectedRating by mutableIntStateOf(0)
        private set
    var deleteSuggestionDialog by mutableStateOf(false)
        private set
    var deleteReviewDialog by mutableStateOf(false)
        private set
    var selectedSuggestion by mutableStateOf("")        //for deleting suggestion
    var selectedReview by mutableStateOf("")            //for deleting review

    fun onTitleChange(value: String) { titleInput = value }
    fun onDescriptionChange(value: String) { descriptionInput = value }
    fun onReviewChange(value: String) { reviewInput = value }
    fun onRatingChange(value: Int) { selectedRating = value }
    fun onDismissDialog() {
        titleInput = ""
        descriptionInput = ""
    }
    fun onDeleteSuggestionDialog() { deleteSuggestionDialog = true }
    fun onDismissDeleteSuggestionDialog() { deleteSuggestionDialog = false }
    fun onDeleteReviewDialog() { deleteReviewDialog = true }
    fun onDismissDeleteReviewDialog() { deleteReviewDialog = false }

    //load suggestions for specific event
    fun loadSuggestions(eventID: String) {
        db.collection("suggestions")
            .whereEqualTo("eventID", eventID)
            .addSnapshotListener { snapshot, error ->
                if(error != null) return@addSnapshotListener
                _suggestions.value = snapshot
                    ?.toObjects(GameSuggestion::class.java)
                    ?: emptyList()
            }
    }

    //load reviews for an event
    fun loadReviews(eventID: String) {
        db.collection("reviews")
            .whereEqualTo("eventID", eventID)
            .addSnapshotListener { snapshot, error ->
                if(error != null) return@addSnapshotListener
                _reviews.value = snapshot
                    ?.toObjects(HostReview::class.java)
                    ?: emptyList()
            }
    }

    //add username to positive or negative votes list
    fun vote(suggestionID: String, positive: Boolean, currentUsername: String) {
        val field = if(positive) "positiveVotes" else "negativeVotes"
        val oppositeField = if(positive) "negativeVotes" else "positiveVotes"

        db.collection("suggestions")
            .document(suggestionID)
            .update(
                mapOf(
                    field to FieldValue.arrayUnion(currentUsername),
                    oppositeField to FieldValue.arrayRemove(currentUsername)
                )
            )
    }

    fun addSuggestion(eventID: String, currentUsername: String) {
        if (titleInput.isBlank()) return

        val docRef = db.collection("suggestions").document()
        val suggestion = GameSuggestion(
            id = docRef.id,
            title = titleInput.trim(),
            description = descriptionInput.trim(),
            suggestedBy = currentUsername,
            eventID = eventID
        )
        docRef.set(suggestion)
            .addOnSuccessListener { onDismissDialog() }
    }

    fun addReview(eventID: String, currentUsername: String) {
        if(reviewInput.isBlank() || selectedRating == 0) return

        val docRef = db.collection("reviews").document()
        val review = HostReview(
            id = docRef.id,
            user = currentUsername,
            text = reviewInput.trim(),
            rating = selectedRating,
            eventID = eventID
        )

        docRef.set(review)
            .addOnSuccessListener {
                reviewInput = ""
                selectedRating = 0 }
    }

    fun deleteSuggestion(suggestionID: String) {
        db.collection("suggestions")
            .document(suggestionID)
            .delete()
        onDismissDeleteSuggestionDialog()
    }

    fun deleteRating(reviewID: String) {
        db.collection("reviews")
            .document(reviewID)
            .delete()
        onDismissDeleteReviewDialog()
    }
}