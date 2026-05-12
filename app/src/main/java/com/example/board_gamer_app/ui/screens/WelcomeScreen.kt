package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.board_gamer_app.R
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel

@Composable
fun WelcomeScreen(navController: NavController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> navController.navigate("homepage")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        WelcomeImage()
        WelcomeText()
        WelcomeButtons(navController)
        AGBText()
    }
}
@Composable
fun WelcomeImage() {
    val image = painterResource(R.drawable.welcome_image)
    Image(
        painter = image,
        contentDescription = "Board Game Image",
        modifier = Modifier
            .clip(CircleShape)
            .width(200.dp)
        )
    Spacer(modifier = Modifier.height(40.dp))
}
@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Willkomen in der Board-Gamer-App!",
            fontSize = 40.sp,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .padding(8.dp)
        )
        Text(
            text = "Plane und organisiere Spieleabende mit Freunden.",
            fontSize = 16.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}
@Composable
fun WelcomeButtons(navController: NavController, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Button(onClick = {
            navController.navigate("signup")
        },
            modifier = modifier
                .width(200.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFfcba03),
                contentColor = Color.White
            )
        ) {
            Text("Registrieren")
        }
        Button(onClick = {
            navController.navigate("login")
        },
            modifier = Modifier
                .width(200.dp)
        ) {
            Text("Anmelden")
        }
    }
}
@Composable
fun AGBText() {
    Text(
        text = "Mit der Anmeldung erkläre ich mich mit den Allgemeinen Geschäftsbedingungen und der Datenschutzerklärung einverstanden.",
        fontSize = 12.sp,
        lineHeight = 24.sp,
        textAlign = TextAlign.Center,
        color = Color.LightGray
    )
}