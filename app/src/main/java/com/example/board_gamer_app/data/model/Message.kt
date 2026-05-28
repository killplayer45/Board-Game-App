package com.example.board_gamer_app.data.model

data class Message(
    val sender: String = "",
    val text: String = "",
    val time: String = "",
    val isMe: Boolean = false,
    val senderId: String = "", // Hilfreich, um "isMe" später dynamisch zu berechnen
    val profileImageUrl: String = "",
    val timestamp: Long = 0L
)
