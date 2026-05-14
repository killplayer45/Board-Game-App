package com.example.board_gamer_app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    var profileImageUri by mutableStateOf<Uri?>(null)
    var isUploading by mutableStateOf(false)
    var uploadError by mutableStateOf<String?>(null)
    var uploadSuccess by mutableStateOf(false)

    suspend fun uploadProfileImage(imageUri: Uri, context: Context) {
        isUploading = true
        uploadError = null
        uploadSuccess = false

        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Cannot read image")
            
            val storageRef = storage.reference.child("profile_pictures/$userId/profile.jpg")
            
            storageRef.putStream(inputStream).await()
            
            profileImageUri = imageUri
            uploadSuccess = true
            inputStream.close()
        } catch (e: Exception) {
            uploadError = e.message ?: "Upload failed"
        } finally {
            isUploading = false
        }
    }

    suspend fun getProfileImageUrl(): String? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val storageRef = storage.reference.child("profile_pictures/$userId/profile.jpg")
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }
}
