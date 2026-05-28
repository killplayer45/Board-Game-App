package com.example.board_gamer_app.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.material3.MaterialTheme
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel

@Composable
fun SettingsScreen(navController: NavController, authViewModel: AuthViewModel, chatViewModel: ChatViewModel, modifier: Modifier = Modifier) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Launcher for Gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.uploadProfilePicture(context, it) }
    }

    // Temp file for Camera
    val tempFile = File(context.cacheDir, "temp_image.jpg")
    val tempUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )

    // Launcher for Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            authViewModel.uploadProfilePicture(context, tempUri)
        }
    }

    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> { Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetToAuthenticated() }
            is AuthState.Info -> { Toast.makeText(context, (authState.value as AuthState.Info).message, Toast.LENGTH_LONG).show()
                authViewModel.resetToAuthenticated() }
            else -> Unit
        }
    }
    Scaffold( bottomBar = { NavBar(navController) }) {
            innerPadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scrollState)) {
            HeaderSection(authViewModel, chatViewModel)
            
            // Dark Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Dark Mode", fontSize = 18.sp)
                Switch(
                    checked = authViewModel.isDarkMode,
                    onCheckedChange = { authViewModel.toggleDarkMode(it) }
                )
            }

            // Profile Image Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (authViewModel.profileImageUrl.isNotEmpty()) {
                        Base64Image(
                            base64 = authViewModel.profileImageUrl,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    if (authViewModel.profileImageUrl.isEmpty()) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val galleryPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()) {
                        isGranted ->
                        if(isGranted) {
                            galleryLauncher.launch("image/*")
                        } else {
                            Toast.makeText(context, "Berechtigung verweigert", Toast.LENGTH_LONG).show()
                        }
                    }
                    IconButton(onClick = {
                        val permission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            android.Manifest.permission.READ_MEDIA_IMAGES}
                        else {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        galleryPermissionLauncher.launch(permission)
                    }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Galerie")
                    }
                    val cameraPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()) { isGranted ->
                        if(isGranted) {
                            cameraLauncher.launch(tempUri)
                        } else {
                            Toast.makeText(context, "Berechtigung verweigert", Toast.LENGTH_LONG).show()
                        }
                    }
                    IconButton(onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = "Kamera")
                    }
                }
            }

            ProfileInformation(authViewModel)
        }
    }
}

@Composable
fun HeaderSection(authViewModel: AuthViewModel, chatViewModel: ChatViewModel) {
    Spacer(modifier = Modifier.height(40.dp))
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Einstellungen",
            fontSize = 30.sp
        )
        Button(onClick = {
            authViewModel.signout(chatViewModel)
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
    Spacer(modifier = Modifier.height(20.dp))
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

@Composable
fun Base64Image(base64: String, modifier: Modifier = Modifier) {
    val bitmap = remember(base64) {
        try {
            val pureBase64 = base64.substringAfter("base64,")
            val bytes = android.util.Base64.decode(pureBase64, android.util.Base64.NO_WRAP)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bmp?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
    if(bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Profilbild",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}