package com.example.board_gamer_app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.board_gamer_app.ui.screens.ChatScreen
import com.example.board_gamer_app.ui.screens.HomepageScreen
import com.example.board_gamer_app.ui.screens.LoginScreen
import com.example.board_gamer_app.ui.screens.RegistrationScreen
import com.example.board_gamer_app.ui.screens.SettingsScreen
import com.example.board_gamer_app.ui.screens.WelcomeScreen
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel, eventViewModel: EventViewModel) {
    //Manages the navigation between screens
    val navController = rememberNavController()
    //Map a route to a screen of the app
    NavHost(navController = navController, startDestination = "welcome", builder = {
        composable("login"){
            LoginScreen(navController, authViewModel)
        }
        composable("signup") {
            RegistrationScreen(navController, authViewModel)
        }
        composable("homepage"){
            HomepageScreen(navController, authViewModel, eventViewModel)
        }
        composable("welcome") {
            WelcomeScreen(navController, authViewModel)
        }
        composable("chat") {
            ChatScreen(navController, authViewModel)
        }
        composable("settings") {
            SettingsScreen(navController, authViewModel)
        }
    })
}