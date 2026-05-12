package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel

@Composable
fun SettingsScreen(navController: NavController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            is AuthState.Info -> Toast.makeText(context, (authState.value as AuthState.Info).message, Toast.LENGTH_LONG).show()
            else -> Unit
        }
    }
    Scaffold( bottomBar = { NavBar(navController) }) {
            innerPadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            HeaderSection(authViewModel)
            ProfileInformation(authViewModel)
        }
    }
}

@Composable
fun HeaderSection(authViewModel: AuthViewModel) {
    Spacer(modifier = Modifier.height(40.dp))
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Einstellungen",
            fontSize = 30.sp
        )
        Button(onClick = {
            authViewModel.signout()
        }) { Text(text = "Logout") }
    }
}

@Composable
fun ProfileInformation(authViewModel: AuthViewModel) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()) {
        Text(text = "Profilinformationen",
            modifier = Modifier.padding(vertical = 20.dp))
        OutlinedTextField(
            value = authViewModel.username,
            onValueChange = { authViewModel.onUsernameChange(it) },
            label = {Text("Username")},
            placeholder = { Text(authViewModel.username) },
            modifier = Modifier.width(300.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = authViewModel.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.width(300.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = authViewModel.city,
            onValueChange = { authViewModel.onCityChange(it) },
            label = { Text("Stadt") },
            modifier = Modifier.width(300.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = authViewModel.zip,
            onValueChange = { authViewModel.onZipChange(it) },
            label = { Text("PLZ") },
            modifier = Modifier.width(300.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = authViewModel.street,
            onValueChange = { authViewModel.onStreetChange(it) },
            label = { Text("Straße") },
            modifier = Modifier.width(300.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = { focusManager.clearFocus()
            authViewModel.updateProfile() }) {
            Text("Speichern")
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    if(authViewModel.showPasswordDialog) {
        PasswordChangeDialog(authViewModel = authViewModel,
            onDismiss = { authViewModel.onDismissPasswordDialog() })
    }
    TextButton(onClick = { authViewModel.onShowPasswordDialog() }) {
        Text(text = "Passwort ändern",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun PasswordChangeDialog(authViewModel: AuthViewModel, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = {Text(text = "Passwort ändern")},
        text = {
            Column(verticalArrangement = Arrangement.Center) {
                OutlinedTextField(
                    value = authViewModel.password,
                    onValueChange = { authViewModel.onPasswordChange(it) },
                    label = { Text("Neues Passwort") },
                    placeholder = { Text("") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(300.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = authViewModel.passwordCheck,
                    onValueChange = { authViewModel.onPasswordCheckChange(it) },
                    label = { Text("Passwort widerholen") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(300.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { authViewModel.updatePassword()
            onDismiss() }) {
                Text(text = "Speichern")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) {
            Text(text = "Abbrechen")
        }}
    )
}