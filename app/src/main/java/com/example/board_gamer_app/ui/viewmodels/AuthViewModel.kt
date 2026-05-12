package com.example.board_gamer_app.ui.viewmodels

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

//class for handling the authentication of users
class AuthViewModel : ViewModel(){
    //Manages login/registration/logout via Firebase Authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //State of authentication, updated automatically whenever changes happen
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    //State of authentication, which can be read by other composables
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    //Manages writing/reading data
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    //with mutableStateOf changes are tracked and UI gets updated
    var email by mutableStateOf("")
        private set     //with private set only ViewModel can change the value
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
    var passwordCheck by mutableStateOf("")
        private set
    var showPasswordDialog by mutableStateOf(false)
    //Functions for changing the values of the variables
    fun onEmailChange(value: String) { email = value }
    fun onPasswordChange(value: String) { password = value }
    fun onCityChange(value: String) { city = value }
    fun onZipChange(value: String) { zip = value }
    fun onStreetChange(value: String) { street = value }
    fun onUsernameChange(value: String) { username = value }
    fun onPasswordCheckChange(value: String) { passwordCheck = value }
    fun onShowPasswordDialog() { showPasswordDialog = true }
    fun onDismissPasswordDialog() { showPasswordDialog = false
    password = ""
    passwordCheck = "" }

    //checks if user is logged in when app starts
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
            }
    }

    fun login() {
        //Validation
        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("E-Mail oder Passwort fehlt")
            return
        }
        //Loading State (for displaying on Screen)
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    loadCurrentUser()
                    _authState.value = AuthState.Authenticated //User is navigated to Homepage
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Etwas ist schiefgelaufen")
                }
            }
    }
    fun signup() {
        //Validation
        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || city.isEmpty() || zip.isEmpty() || street.isEmpty()) {
            _authState.value = AuthState.Error("Es fehlen Informationen!")
            return
        }
        //Compare both passwords
        if(password != passwordCheck) {
            _authState.value = AuthState.Error("Das Passwort stimmt nicht überein")
            return
        }
        //Loading State (displaying on Screen)
        _authState.value = AuthState.Loading
        //Create User in Firestore Auth
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
        //Variable that references a User Object (of User data class)
        val user = User(
            userID = userID,    //Firebase Auth uid
            username = username.trim(),
            email = email.trim(),
            city = city.trim(),
            zip = zip.trim(),
            street = street.trim(),
            password = password.trim()
        )
        db.collection("users")
            .document(userID)   //userID as name
            .set(user)                         //creates new document
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Profil konnte nicht gepseichert werden!")
                }
            }
    }

    fun signout() {
        auth.signOut()
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
                "password" to password
            ))
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Info("Daten wurden geändert!")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Daten konnten nicht geändert werden")
                }
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
                    _authState.value = AuthState.Info("Email zum Zurücksetzen des Passworts wurde verschickt!")
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
                if(task.exception?.message?.contains("recent") == true) {
                    _authState.value = AuthState.Error("Bitte erneut einloggen, um das Passwort zu ändern")
                }
                _authState.value = AuthState.Error(task.exception?.message ?: "Passwort konnte nicht geändert werden")
            }
        }
    }
}
//sealed class holds the possible States and provides type-safety, objects make sure that the same instance is used
sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    data class Error(val message: String): AuthState()
    data class Info (val message: String): AuthState()
}