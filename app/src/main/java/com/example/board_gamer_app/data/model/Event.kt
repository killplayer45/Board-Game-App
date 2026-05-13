package com.example.board_gamer_app.data.model
data class Event(
    val eventID: String = "",
    val date: String = "",
    val dateTimestamp: Long = 0L,
    val time: String = "",
    val createdByUserID: String = "",
    val location: String = "",
    val playersAttending: ArrayList<String> = arrayListOf(),
    val playersNotAttending: ArrayList<String> = arrayListOf(),
    val gameMaster: String = "",
    val gameMasterStreet: String = "",
    val gameMasterZip: String = "",
    val gameMasterCity: String = "",
)