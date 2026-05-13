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
import com.example.board_gamer_app.ui.screens.SuggestionScreen
import com.example.board_gamer_app.ui.screens.WelcomeScreen
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModel

@Composable
fun AppNavigation(authViewModel: AuthViewModel, eventViewModel: EventViewModel, suggestionsViewModel: SuggestionsViewModel, chatViewModel: ChatViewModel) {
    //Manages the navigation between screens
    val navController = rememberNavController()
    //Map a route to a screen of the app
    NavHost(navController = navController, startDestination = "homepage", builder = {
        composable("login"){
            LoginScreen(navController, authViewModel)
        }
        composable("signup") {
            RegistrationScreen(navController, authViewModel)
        }
        composable("homepage"){
            HomepageScreen(navController, authViewModel, eventViewModel, chatViewModel)
        }
        composable("welcome") {
            WelcomeScreen(navController, authViewModel)
        }
        composable("chat") {
            ChatScreen(navController, authViewModel, chatViewModel)
        }
        composable("settings") {
            SettingsScreen(navController, authViewModel, chatViewModel)
        }
        composable("event_detail/{eventID}") { backStackEntry ->
            val eventID = backStackEntry.arguments?.getString("eventID") ?: ""
            SuggestionScreen(navController = navController, eventID = eventID, eventViewModel = eventViewModel, suggestionsViewModel = suggestionsViewModel, authViewModel = authViewModel)
        }
    })
}