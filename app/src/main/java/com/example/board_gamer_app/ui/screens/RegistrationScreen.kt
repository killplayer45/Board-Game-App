package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun RegistrationScreen(navController: NavController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    //for scrolling on the page and remembering scroll position
    val scrollState = rememberScrollState()
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate("homepage")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(modifier = modifier
                .verticalScroll((scrollState))
                .fillMaxSize()
    ) {
        RegistrationHeaderText()
        RegistrationMain(authViewModel)
        RegistrationButton(authViewModel, navController)
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun RegistrationHeaderText(modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Registrieren",
            fontSize = 40.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Konto erstellen",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun RegistrationMain(authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 40.dp)) {
        Text(
            text = "Username",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.username,
            onValueChange = { authViewModel.onUsernameChange(it) },
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Email-Adresse",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.email,
            placeholder = {Text("")},
            onValueChange = { authViewModel.onEmailChange(it) },
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Stadt",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.city,
            onValueChange = { authViewModel.onCityChange(it) },
            placeholder = {Text("")},
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "PLZ",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.zip,
            onValueChange = { authViewModel.onZipChange(it) },
            placeholder = {Text("")},
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Straße + Hausnummer",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.street,
            onValueChange = { authViewModel.onStreetChange(it) },
            placeholder = {Text("")},
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
            onValueChange = { authViewModel.onPasswordChange(it)},
            placeholder = {Text("")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Passwort wiederholen",
            fontSize = 16.sp
        )
        TextField(
            value = authViewModel.passwordCheck,
            onValueChange = { authViewModel.onPasswordCheckChange(it) },
            placeholder = {Text("")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(330.dp),
            singleLine = true
        )
    }
}

@Composable
fun RegistrationButton(authViewModel: AuthViewModel, navController: NavController, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                authViewModel.signup()
                },
            modifier = Modifier
                .width(300.dp)
                .padding(top = 40.dp),
            colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFfcba03),
            contentColor = Color.White
        )
        ) {
            Text(text = "Registrieren",
                fontSize = 20.sp)
        }
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "Schon einen Account? Zum Login")
        }
    }
}