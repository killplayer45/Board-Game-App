package com.example.board_gamer_app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.board_gamer_app.ui.theme.BoardGamerAppTheme
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set UI to the edge of the screen
        enableEdgeToEdge()
        //Creation of ViewModels
        val authViewModel: AuthViewModel by viewModels()
        val eventViewModel: EventViewModel by viewModels()
        setContent{
            BoardGamerAppTheme() {
                    AppNavigation(authViewModel = authViewModel, eventViewModel = eventViewModel)
            }
        }
    }
}