package com.example.board_gamer_app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.board_gamer_app.data.model.Event
import com.example.board_gamer_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()            //Manages reading/writing data
    private val auth = FirebaseAuth.getInstance()               //Manages authentication tasks
    private val _events = MutableStateFlow<List<Event>>(emptyList())    //List of Events, changes are tracked with MutableStateFlow, writing only by ViewModel
    val events: StateFlow<List<Event>> = _events.asStateFlow()  //Events which can be accessed by composables

    var date by mutableStateOf("")
        private set
    //for displaying in UI
    var time by mutableStateOf("")
        private set
    var timeInMillis by mutableLongStateOf(0L)          //for adding hour and minute to date
    var dateTimestamp by mutableLongStateOf(0L)         //Date (start of the day) in milliseconds since 1970, for sorting dates
        private set
    var currentUsername by mutableStateOf("")
    var userID = auth.currentUser?.uid ?: "Unbekannt"
    var showAddEventDialog by mutableStateOf(false)     //for showing dialog for adding an event
        private set
    var showCalendar by mutableStateOf(false)           //for showing calendar component
        private set
    var showTime by mutableStateOf(false)               //for showing time component
        private set
    var showDeleteEventDialog by mutableStateOf(false)  //for showing dialog to delete event
        private set

    //formats selected time for UI and saves time in milliseconds for calculation and sorting
    fun onTimeChange(hour: Int, minute: Int) {
        time = String.format("%02d:%02d", hour, minute)
        val milliseconds = hour * 3.6e+6 + minute * 60000
        timeInMillis = milliseconds.toLong()
    }
    fun onShowAddEventDialog() {showAddEventDialog = true}
    //For canceling input and closing dialog
    fun onDismissAddEventDialog() {
        showAddEventDialog = false
        date = ""
        time = ""
    }
    fun onShowCalendar() {showCalendar = true}
    fun onDismissCalendar() {showCalendar = false}
    fun onShowTime() {showTime = true}
    fun onDismissTime() {showTime = false}
    fun onDeleteEventDialog() { showDeleteEventDialog = true }
    fun onDismissDeleteEventDialog() { showDeleteEventDialog = false }

    init { loadEvents()
            loadCurrentUser()
    }

    //loading all events from database and order them by dateTimestamp,
    //with addSnapshotListener Firestore listens to the query and sends updates when data changes
    private fun loadEvents() {
        db.collection("events")
            .orderBy("dateTimestamp")
            .addSnapshotListener { snapshot, error ->
                if(error != null) return@addSnapshotListener
                _events.value = snapshot
                    ?.toObjects(Event::class.java)  //Firestore documents get converted to objects of event data class
                    ?: emptyList()
            }
    }

    //sets the currentUsername variable
    fun loadCurrentUser() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                currentUsername = user?.username?: "Unbekannt"
            }
    }

    //function for loading all players who are attending/not attending event
    fun loadPlayerNames(userIDs: List<String>, onResult: (Map<String, String>) -> Unit) {
        if(userIDs.isEmpty()) { onResult(emptyMap()); return }
        db.collection("users")
            .whereIn("userID", userIDs)
            .get()
            .addOnSuccessListener { documents ->
                val map = documents.associate { doc ->
                    doc.getString("userID")!! to (doc.getString("username") ?: "Unbekannt")
                }
                onResult(map)
            }
            .addOnFailureListener { onResult(emptyMap()) }
    }
    //the selected date (in milliseconds) is saved to dateTimestamp (for sorting) and
    //in the specified format to date (property of an event)
    fun onDateSelected(timestamp: Long?) {
        if(timestamp == null) return
        dateTimestamp = timestamp
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        date = formatter.format(Date(timestamp))
    }
    //add an event with date and time input from UI
    fun addEvent() {
        if(date.isEmpty() || time.isEmpty()) return
        val event = Event(
            date = date.trim(),
            dateTimestamp = dateTimestamp + timeInMillis,
            time = time.trim(),
            createdByUserID = auth.currentUser?.uid ?: "",
            playersAttending = arrayListOf(),
            playersNotAttending = arrayListOf(),
            gameMasterID = ""
        )
        val documentReference = db.collection("events").document()  //document() automatically generates an ID for the event
        documentReference.set(event.copy(eventID = documentReference.id))         //copy creates an event object with the same ID as the document
            .addOnSuccessListener { onDismissAddEventDialog() }
    }

    //when user clicks thumbs up this function gets called
    fun addPlayerToAttendingList(eventID: String) {
        val updates = hashMapOf<String, Any>(
            "playersAttending" to FieldValue.arrayUnion(userID),
            "playersNotAttending" to FieldValue.arrayRemove(userID)
        )
        db.collection("events")
            .document(eventID)
            .update(updates)
        }

    //when user clicks thumbs down this function gets called
    fun addPlayerToNotAttendingList(eventID: String) {
        val currentEvent = _events.value.find { it.eventID == eventID } ?: return
        val updates = hashMapOf(
            "playersNotAttending" to FieldValue.arrayUnion(userID),
            "playersAttending" to FieldValue.arrayRemove(userID),
            "gameMasterID" to if(currentEvent.gameMasterID == userID) "" else currentEvent.gameMasterID
        )
        db.collection("events")
            .document(eventID)
            .update(updates)
    }

    //add, update or remove gameMaster
    fun updateGameMaster(eventID: String) {
        val currentEvent = _events.value.find { it.eventID == eventID} ?: return
        val sortedEvents = _events.value.sortedBy { it.dateTimestamp }
        val currentIndex = sortedEvents.indexOfFirst { it.eventID == eventID }
        val previousEvent = if(currentIndex > 0) sortedEvents[currentIndex - 1] else null
        val nextEvent = if(currentIndex < sortedEvents.size - 1) sortedEvents[currentIndex + 1] else null
        val wasGameMasterInPreviousEvent = previousEvent?.gameMasterID == userID
        val isGameMasterInNextEvent = nextEvent?.gameMasterID == userID

        val newGameMasterID = if(currentEvent.gameMasterID != userID &&
            currentEvent.playersAttending.contains(userID) &&
            !wasGameMasterInPreviousEvent &&
            !isGameMasterInNextEvent)
            userID else ""

        db.collection("events")
            .document(eventID)
            .update("gameMasterID", newGameMasterID)
    }

    fun deleteEvent(eventID: String) {
        db.collection("events")
            .document(eventID)
            .delete()
    }
}