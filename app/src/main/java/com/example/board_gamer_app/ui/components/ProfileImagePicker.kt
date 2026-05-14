package com.example.board_gamer_app.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.board_gamer_app.util.ImagePickerUtil

@Composable
fun ProfileImagePicker(
    imageUri: Uri?,
    isLoading: Boolean = false,
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var cameraPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
        showDialog = false
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != null) {
            onImageSelected(cameraPhotoUri!!)
        }
        showDialog = false
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && cameraPhotoUri != null) {
            cameraLauncher.launch(cameraPhotoUri!!)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Image Display
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showDialog = true }
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Klicke zum Bild ändern",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Selection Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Profilbild auswählen") },
            text = { Text("Wähle eine Quelle für dein Profilbild") },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Abbrechen")
                }
            },
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        // Buttons below dialog
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val file = ImagePickerUtil.createImageFile(context)
                    cameraPhotoUri = ImagePickerUtil.getPhotoUri(context, file)
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Icon(
                    Icons.Default.AddAPhoto,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kamera")
            }

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Galerie")
            }
        }
    }
}
