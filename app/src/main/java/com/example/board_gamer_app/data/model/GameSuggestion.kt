package com.example.board_gamer_app.data.model

data class GameSuggestion(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val suggestedBy: String = "",
    val positiveVotes: List<String> = emptyList(),
    val negativeVotes: List<String> = emptyList(),
    val eventID: String = ""
) {
    val positiveVotesCount get() = positiveVotes.size
    val negativeVotesCount get() = negativeVotes.size

    fun hasPositiveVote(username: String) = positiveVotes.contains(username)
    fun hasNegativeVote(username: String) = negativeVotes.contains(username)
}

