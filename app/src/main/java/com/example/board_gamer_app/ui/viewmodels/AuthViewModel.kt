package com.example.board_gamer_app.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.board_gamer_app.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var city by mutableStateOf("")
        private set
    var zip by mutableStateOf("")
        private set
    var street by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var profileImageUrl by mutableStateOf("")
        private set
    var passwordCheck by mutableStateOf("")
        private set
    var showPasswordDialog by mutableStateOf(false)
    var isDarkMode by mutableStateOf(false)

    fun onEmailChange(value: String) { email = value }
    fun onPasswordChange(value: String) { password = value }
    fun onCityChange(value: String) { city = value }
    fun onZipChange(value: String) { zip = value }
    fun onStreetChange(value: String) { street = value }
    fun onUsernameChange(value: String) { username = value }
    fun onPasswordCheckChange(value: String) { passwordCheck = value }
    fun onShowPasswordDialog() { showPasswordDialog = true }
    fun onDismissPasswordDialog() { 
        showPasswordDialog = false
        password = ""
        passwordCheck = "" 
    }
    fun toggleDarkMode(enabled: Boolean) { isDarkMode = enabled }

    init {
        checkAuthStatus()
        loadCurrentUser()
    }

    fun checkAuthStatus() {
        if(auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    private fun loadCurrentUser() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                username = user?.username ?: ""
                email = user?.email ?: ""
                city = user?.city ?: ""
                zip = user?.zip ?: ""
                street = user?.street ?: ""
                password = user?.password ?: ""
                profileImageUrl = user?.profileImageUrl ?: ""
            }
    }

    fun login() {
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("E-Mail oder Passwort fehlt")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    loadCurrentUser()
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Etwas ist schiefgelaufen")
                }
            }
    }

    fun signup() {
        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || city.isEmpty() || zip.isEmpty() || street.isEmpty()) {
            _authState.value = AuthState.Error("Es fehlen Informationen!")
            return
        }
        if(password != passwordCheck) {
            _authState.value = AuthState.Error("Das Passwort stimmt nicht überein")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email.trim(), password.trim())
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    saveUserToFirestore()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Etwas ist schiefgelaufen")
                }
            }
    }

    private fun saveUserToFirestore() {
        val userID = auth.currentUser?.uid ?: return
        val user = User(
            userID = userID,
            username = username.trim(),
            email = email.trim(),
            city = city.trim(),
            zip = zip.trim(),
            street = street.trim(),
            password = password.trim(),
            profileImageUrl = profileImageUrl
        )
        db.collection("users")
            .document(userID)
            .set(user)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Profil konnte nicht gepseichert werden!")
                }
            }
    }

    fun signout(chatViewModel: ChatViewModel) {
        auth.signOut()
        chatViewModel.reload()
        _authState.value = AuthState.Unauthenticated
    }

    fun updateProfile() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userID)
            .update(mapOf(
                "username" to username,
                "email" to email,
                "city" to city,
                "zip" to zip,
                "street" to street,
                "password" to password,
                "profileImageUrl" to profileImageUrl
            ))
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Info("Daten wurden geändert!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Daten konnten nicht geändert werden")
                }
            }
    }

    fun uploadProfilePicture(context: Context, uri: Uri) {
        _authState.value = AuthState.Loading
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Verkleinern auf max 300x300 für Firestore Base64 (Limit beachten)
            val scaledBitmap = if (bitmap.width > 300 || bitmap.height > 300) {
                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                if (ratio > 1) Bitmap.createScaledBitmap(bitmap, 300, (300 / ratio).toInt(), true)
                else Bitmap.createScaledBitmap(bitmap, (300 * ratio).toInt(), 300, true)
            } else bitmap

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytes = outputStream.toByteArray()
            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            
            profileImageUrl = "data:image/jpeg;base64,$base64String"
            updateProfile()
            _authState.value = AuthState.Info("Profilbild wurde aktualisiert!")
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Fehler beim Verarbeiten des Bildes")
        }
    }

    fun resetPassword() {
        if(email.isEmpty()) {
            _authState.value = AuthState.Error("Bitte Email eingeben")
            return
        }
        _authState.value = AuthState.Loading
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Info("Email verschickt!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Email konnte nicht gesendet werden")
                }
            }
    }

    fun updatePassword() {
        if(password.isEmpty()) {
            _authState.value = AuthState.Error("Passwort fehlt")
            return
        }
        if(password != passwordCheck) {
            _authState.value = AuthState.Error("Passwörter stimmen nicht überein")
            return
        }
        auth.currentUser?.updatePassword(password.trim())?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                _authState.value = AuthState.Info("Passwort wurde geändert!")
            } else {
                _authState.value = AuthState.Error(task.exception?.message ?: "Passwort konnte nicht geändert werden")
            }
        }
    }
}

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    data class Error(val message: String): AuthState()
    data class Info (val message: String): AuthState()
}
