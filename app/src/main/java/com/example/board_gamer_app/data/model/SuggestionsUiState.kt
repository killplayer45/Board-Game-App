package com.example.board_gamer_app.data.model

data class SuggestionsUiState(
    val suggestions: List<GameSuggestion> = emptyList(),
    val reviews: List<HostReview> = emptyList(),
    val terminRatings: Map<String, Int> = emptyMap(),
    val terminTimes: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)