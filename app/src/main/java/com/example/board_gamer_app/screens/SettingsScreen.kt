package com.example.board_gamer_app.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.board_gamer_app.ui.components.ProfileImagePicker
import com.example.board_gamer_app.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(profileViewModel.uploadSuccess) {
        if (profileViewModel.uploadSuccess) {
            Toast.makeText(context, "Profilbild hochgeladen!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(profileViewModel.uploadError) {
        profileViewModel.uploadError?.let {
            Toast.makeText(context, "Fehler: $it", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header Section
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Einstellungen",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Profile Image Section
        Text(
            text = "Profilbild",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ProfileImagePicker(
            imageUri = selectedImageUri,
            isLoading = profileViewModel.isUploading,
            onImageSelected = { uri ->
                selectedImageUri = uri
                coroutineScope.launch {
                    profileViewModel.uploadProfileImage(uri, context)
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Profile Information Section
        Text(
            text = "Profilinformationen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = "Paul",
            onValueChange = {},
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "paul@example.com",
            onValueChange = {},
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "Frankfurt",
            onValueChange = {},
            label = { Text("Stadt") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "60311",
            onValueChange = {},
            label = { Text("PLZ") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = "Spielstraße 1",
            onValueChange = {},
            label = { Text("Straße") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { Toast.makeText(context, "Profil aktualisiert", Toast.LENGTH_SHORT).show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Profil speichern")
        }
    }
}
