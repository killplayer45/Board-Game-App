package com.example.board_gamer_app.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.board_gamer_app.data.model.Message
import com.example.board_gamer_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class ChatViewModel(private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
                    private val auth: FirebaseAuth = FirebaseAuth.getInstance()) : ViewModel() {

    var currentUsername = ""
    var currentProfileImageUrl = ""

    //Chat messages stored in a list
    val messages = mutableStateListOf<Message>()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                currentUsername = user?.username ?: "Nicht gefunden"
                currentProfileImageUrl = user?.profileImageUrl ?: ""
                observeMessages()
            }
    }

    //updates in messages collection in Firestore are tracked with SnapshotListener
    private fun observeMessages() {
        db.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener {snapshot, error ->
                if(error != null) return@addSnapshotListener
                val currentUserID = auth.currentUser?.uid
                messages.clear()    //Clear old list
                snapshot?.documents?.forEach { doc ->
                    val message = doc.toObject(Message::class.java) ?: return@forEach
                    messages.add(message.copy(
                        isMe = message.senderId == currentUserID    //mark message as own or from another user
                    ))
                }
            }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userID = auth.currentUser?.uid ?: return

        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        val timeString = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

        val docRef = db.collection("messages").document()   //auto-generates an ID for message
        val message = Message(
            sender = currentUsername,
            text = text.trim(),
            time = timeString,
            senderId = userID,
            profileImageUrl = currentProfileImageUrl,
            timestamp = System.currentTimeMillis()
        )
        docRef.set(message)
    }

    //clears username and messages when user logs out and new user logs in
    fun reload() {
        messages.clear()
        currentUsername = ""
        loadCurrentUser()
    }
}

class ChatViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel() as T
    }
}