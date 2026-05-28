package com.example.board_gamer_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class AuthViewModelTest {
    @get: Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockAuth: FirebaseAuth = mock()
    private val mockDb: FirebaseFirestore = mock()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        viewModel = AuthViewModel(mockAuth, mockDb)
    }

    @Test
    fun `login mit leerer Email setzt Error State`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("12345")
        viewModel.login()

        assertTrue(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun `login mit leeren Feldern zeigt korrekte Fehlermeldung`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.login()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("E-Mail oder Passwort fehlt", state.message)
    }

    @Test
    fun `signup mit fehlendem Username setzt Error State`() {
        viewModel.onEmailChange("test@test.de")
        viewModel.onPasswordChange("12345")
        viewModel.onPasswordCheckChange("12345")
        viewModel.onUsernameChange("")
        viewModel.onCityChange("Beispielstadt")
        viewModel.onZipChange("12345")
        viewModel.onStreetChange("Beispielstraße 1")
        viewModel.signup()

        assertTrue(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun `signup mit fehlenden Informationen zeigt korrekte Fehlermeldung`() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")
        viewModel.onUsernameChange("")
        viewModel.signup()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Es fehlen Informationen!", state.message)
    }

    @Test
    fun `signup mit nicht übereinstimmenden Passwörtern setzt Error State`() {
        viewModel.onEmailChange("test@test.de")
        viewModel.onPasswordChange("12345")
        viewModel.onPasswordCheckChange("123456789")
        viewModel.onUsernameChange("User")
        viewModel.onCityChange("Beispielstadt")
        viewModel.onZipChange("12345")
        viewModel.onStreetChange("Beispielstraße 1")
        viewModel.signup()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Das Passwort stimmt nicht überein", state.message)
    }

    @Test
    fun `updatePassword mit leerem Passwort setzt Error State`() {
        viewModel.onPasswordChange("")
        viewModel.updatePassword()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Passwort fehlt", state.message)
    }

    @Test
    fun `updatePassword mit nicht übereinstimmenden Passwörtern setzt Error State`() {
        viewModel.onPasswordChange("neuesPasswort12345")
        viewModel.onPasswordCheckChange("anderesneuesPasswort")
        viewModel.updatePassword()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Passwörter stimmen nicht überein", state.message)
    }

    @Test
    fun `resetPassword mit leerer Email setzt Error State`() {
        viewModel.onEmailChange("")
        viewModel.resetPassword()

        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Bitte Email eingeben", state.message)
    }

    @Test
    fun `onDismissPasswordDialog setzt Passwörter zurück`() {
        viewModel.onPasswordChange("passwort123")
        viewModel.onPasswordCheckChange("passwort123")
        viewModel.onDismissPasswordDialog()

        assertEquals("", viewModel.password)
        assertEquals("", viewModel.passwordCheck)
    }

    @Test
    fun `toggleDarkMode setzt isDarkMode`() {
        viewModel.toggleDarkMode(true)
        assertTrue(viewModel.isDarkMode)

        viewModel.toggleDarkMode(false)
        assertTrue(!viewModel.isDarkMode)
    }
}