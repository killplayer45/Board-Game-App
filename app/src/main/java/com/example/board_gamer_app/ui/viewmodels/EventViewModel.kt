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
    //Manages reading/writing data
    private val db = FirebaseFirestore.getInstance()
    //Manages authentication
    private val auth = FirebaseAuth.getInstance()
    //List of Events, changes are tracked with MutableStateFlow, writing only by ViewModel
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    //Events which can be accessed by composables
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    var date by mutableStateOf("")
        private set
    var time by mutableStateOf("")
        private set
    var showDialog by mutableStateOf(false) //Holding boolean value for showing dialog for adding an event
        private set
    var showCalendar by mutableStateOf(false) //Holding boolean value for showing calendar component
        private set
    var showTime by mutableStateOf(false)   //Holding boolean value for showing time component
        private set
    var dateTimestamp by mutableLongStateOf(0L) //Date in milliseconds since 1970, for sorting dates
        private set
    var currentUsername by mutableStateOf("")

    fun onTimeChange(hour: Int, minute: Int) {time = String.format("%02d:%02d", hour, minute)}
    fun onShowDialog() {showDialog = true}
    //For canceling input and closing dialog
    fun onDismissDialog() {
        showDialog = false
        date = ""
        time = ""
    }
    fun onShowCalendar() {showCalendar = true}
    fun onDismissCalendar() {showCalendar = false}
    fun onShowTime() {showTime = true}
    fun onDismissTime() {showTime = false}

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

    //sets the currentUsername variable depending on the current User
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

    //the selected date (in milliseconds) is saved to dateTimestamp (for sorting) and in the specified format to date (property of an event)
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
            dateTimestamp = dateTimestamp,
            time = time.trim(),
            createdByUserID = auth.currentUser?.uid ?: "",
            location = "",
            playersAttending = arrayListOf(),
            playersNotAttending = arrayListOf(),
            gameMaster = ""
        )
        //document() automatically generates an ID for the event
        //copy creates an event object with the same ID as the document
        val documentReference = db.collection("events").document()
        documentReference.set(event.copy(eventID = documentReference.id))
            .addOnSuccessListener { onDismissDialog() }
    }

    //when user clicks thumbs up this function gets called
    fun addPlayerToAttendingList(eventID: String) {
        if(currentUsername.isEmpty()) return
        val updates = hashMapOf<String, Any>(
            "playersAttending" to FieldValue.arrayUnion(currentUsername),
            "playersNotAttending" to FieldValue.arrayRemove(currentUsername)
        )
        db.collection("events")
            .document(eventID)
            .update(updates)
        }

    //when user clicks thumbs down this function gets called
    fun addPlayerToNotAttendingList(eventID: String) {
        if(currentUsername.isEmpty()) return
        val currentEvent = _events.value.find { it.eventID == eventID } ?: return

        val updates = hashMapOf(
            "playersNotAttending" to FieldValue.arrayUnion(currentUsername),
            "playersAttending" to FieldValue.arrayRemove(currentUsername),
            "gameMaster" to if(currentEvent.gameMaster == currentUsername) "" else currentEvent.gameMaster
        )
        db.collection("events")
            .document(eventID)
            .update(updates)
    }

    //add or remove gamemaster
    fun updateGameMaster(eventID: String) {
        val currentEvent = _events.value.find { it.eventID == eventID} ?: return
        val sortedEvents = _events.value.sortedBy { it.dateTimestamp }
        val currentIndex = sortedEvents.indexOfFirst { it.eventID == eventID }
        val previousEvent = if(currentIndex > 0) sortedEvents[currentIndex - 1] else null
        val nextEvent = if(currentIndex < sortedEvents.size - 1) sortedEvents[currentIndex + 1] else null
        val wasGameMasterInPreviousEvent = previousEvent?.gameMaster == currentUsername
        val isGameMasterInNextEvent = nextEvent?.gameMaster == currentUsername

        if(currentEvent.gameMaster != currentUsername &&
            currentEvent.playersAttending.contains(currentUsername) &&
            !wasGameMasterInPreviousEvent &&
            !isGameMasterInNextEvent){
            val userID = auth.currentUser?.uid ?: return
            db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(User::class.java)

                    db.collection("events")
                        .document(eventID)
                        .update(mapOf(
                            "gameMaster" to currentUsername,
                            "gameMasterStreet" to (user?.street ?: ""),
                            "gameMasterZip" to (user?.zip ?: ""),
                            "gameMasterCity" to (user?.city ?: ""),
                            "gameMasterProfilePicture" to (user?.profileImageUrl ?: "")
                        ))
                }
        } else {
            db.collection("events")
                .document(eventID)
                .update(mapOf(
                    "gameMaster" to "",
                    "gameMasterStreet" to "",
                    "gameMasterZip" to "",
                    "gameMasterCity" to ""
                ))
        }
    }
}