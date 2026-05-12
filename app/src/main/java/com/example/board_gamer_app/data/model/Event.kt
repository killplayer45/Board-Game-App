package com.example.board_gamer_app.data.model
data class Event(
    val eventID: String = "",
    val date: String = "",
    val dateTimestamp: Long = 0L,
    val time: String = "",
    val createdByUserID: String = "",
    val location: String = "",
    var playersAttending: ArrayList<String> = arrayListOf(),
    var playersNotAttending: ArrayList<String> = arrayListOf(),
    var gameMaster: String = ""
)