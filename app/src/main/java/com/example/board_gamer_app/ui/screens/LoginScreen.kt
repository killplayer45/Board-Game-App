package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate("homepage")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            is AuthState.Info -> Toast.makeText(context, (authState.value as AuthState.Info).message, Toast.LENGTH_LONG).show()
            else -> Unit
        }
    }
    Column() {
        LoginHeaderText()
        LoginMain(authViewModel)
        LoginOptions(authViewModel)
        LoginButton(navController, authViewModel)
    }
}

@Composable
fun LoginHeaderText(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(150.dp))
        Text(
            text = "Anmelden",
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Gib deine Daten ein, um fortzufahren.",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun LoginMain(authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 40.dp)) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Email-Adresse",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.email,
            onValueChange = { authViewModel.onEmailChange(it) },
            placeholder = { Text("") },
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Passwort",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.password,
            onValueChange = { authViewModel.onPasswordChange(it) },
            placeholder = { Text("") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
    }
}

@Composable
fun LoginOptions(authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(start = 40.dp),
        verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = {authViewModel.resetPassword()}) {
            Text(text = "Passwort vergessen?")
        }
    }
}

@Composable
fun LoginButton(navController: NavController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = { authViewModel.login() },
            modifier = Modifier
                .width(300.dp)
                .padding(20.dp)
        ) {
            Text(text = "Anmelden",
                fontSize = 20.sp)
        }
        TextButton(onClick = {
            navController.navigate("signup")
        }) {
            Text(text = "Noch keinen Account? Registriere dich")
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
