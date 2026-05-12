package com.example.board_gamer_app.data.model

data class Rating(
    val ratingID: String = "",
    val stars: Int = 0,
    val createdByUserID: String = "",
    val eventID: String = "",
    val comment: String = ""
)