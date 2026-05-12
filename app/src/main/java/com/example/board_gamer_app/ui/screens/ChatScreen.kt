package com.example.board_gamer_app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel

@Composable
fun ChatScreen(navController: NavController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    Scaffold( bottomBar = { NavBar(navController) }) {
            innerPadding ->
        Column(modifier = modifier.fillMaxSize().padding(innerPadding)) {

        }
    }
    Text(text = "chat")
}