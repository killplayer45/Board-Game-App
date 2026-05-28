package com.example.board_gamer_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.board_gamer_app.ui.theme.BoardGamerAppTheme
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.AuthViewModelFactory
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModelFactory
import com.example.board_gamer_app.ui.viewmodels.EventViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModelFactory
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModel
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set UI to the edge of the screen
        enableEdgeToEdge()
        //Creation of ViewModels
        val authViewModel: AuthViewModel by viewModels() { AuthViewModelFactory() }
        val eventViewModel: EventViewModel by viewModels() { EventViewModelFactory() }
        val suggestionsViewModel: SuggestionsViewModel by viewModels() { SuggestionsViewModelFactory() }
        val chatViewModel: ChatViewModel by viewModels() { ChatViewModelFactory() }
        setContent{
            BoardGamerAppTheme(darkTheme = authViewModel.isDarkMode) {
                    AppNavigation(authViewModel = authViewModel, eventViewModel = eventViewModel, suggestionsViewModel = suggestionsViewModel, chatViewModel = chatViewModel)
            }
        }
    }
}