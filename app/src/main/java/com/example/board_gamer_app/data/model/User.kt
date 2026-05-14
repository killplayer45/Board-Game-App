package com.example.board_gamer_app.data.model

data class User(
    val userID: String = "",
    val username: String = "",
    val email: String = "",
    val city: String = "",
    val zip: String="",
    val street: String = "",
    val password: String = "",
    val profileImageUrl: String = ""
)
