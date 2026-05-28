package com.example.board_gamer_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavController
import com.example.board_gamer_app.ui.screens.RegistrationScreen
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class RegistrationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuth: FirebaseAuth = mock(
        FirebaseAuth::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )

    private val mockDb: FirebaseFirestore = mock(
        FirebaseFirestore::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )

    private val mockNavController: NavController = mock(
        NavController::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        viewModel = AuthViewModel(mockAuth, mockDb)
        viewModel.setUnauthenticated()
        composeTestRule.setContent {
            RegistrationScreen(navController = mockNavController, authViewModel = viewModel)
        }
    }

    @Test
    fun displayingRegistrationTitle() {
        composeTestRule.onAllNodesWithText("Registrieren")[0].assertIsDisplayed()
    }

    @Test
    fun displayingSubtitle() {
        composeTestRule.onNodeWithText("Konto erstellen").assertIsDisplayed()
    }

    @Test
    fun displayingUsernameLabel() {
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun displayingEmailLabel() {
        composeTestRule.onNodeWithText("Email-Adresse").assertIsDisplayed()
    }

    @Test
    fun displayingCityLabel() {
        composeTestRule.onNodeWithText("Stadt").assertIsDisplayed()
    }

    @Test
    fun displayingZipLabel() {
        composeTestRule.onNodeWithText("PLZ").assertIsDisplayed()
    }

    @Test
    fun displayingStreetLabel() {
        composeTestRule.onNodeWithText("Straße + Hausnummer").assertIsDisplayed()
    }

    @Test
    fun displayingPasswordLabel() {
        composeTestRule.onNodeWithText("Passwort").assertIsDisplayed()
    }

    @Test
    fun displayingRepeatPasswordLabel() {
        composeTestRule.onNodeWithText("Passwort wiederholen").assertIsDisplayed()
    }

    @Test
    fun displayingRegistrationButton() {
        composeTestRule.onAllNodesWithText("Registrieren")[1].performScrollTo().assertIsDisplayed()
    }

    @Test
    fun displayingLoginButton() {
        composeTestRule.onNodeWithText("Schon einen Account? Zum Login").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun usernameInputUpdatesViewModel() {
        viewModel.onUsernameChange("User")
        assertEquals("User", viewModel.username)
    }

    @Test
    fun emailInputUpdatesViewModel() {
        viewModel.onEmailChange("test@test.de")
        assertEquals("test@test.de", viewModel.email)
    }

    @Test
    fun cityInputUpdatesViewModel() {
        viewModel.onCityChange("Beispielstadt")
        assertEquals("Beispielstadt", viewModel.city)
    }

    @Test
    fun zipInputUpdatesViewModel() {
        viewModel.onZipChange("12345")
        assertEquals("12345", viewModel.zip)
    }

    @Test
    fun streetInputUpdatesViewModel() {
        viewModel.onStreetChange("Beispielstraße")
        assertEquals("Beispielstraße", viewModel.street)
    }

    @Test
    fun passwordInputUpdatesViewModel() {
        viewModel.onPasswordChange("passwort123")
        assertEquals("passwort123", viewModel.password)
    }

    @Test
    fun passwordCheckInputUpdatesViewModel() {
        viewModel.onPasswordCheckChange("passwort123")
        assertEquals("passwort123", viewModel.passwordCheck)
    }

    @Test
    fun registrationButtonWithEmptyFieldsSetsErrorState() {
        viewModel.onUsernameChange("")
        viewModel.onEmailChange("")
        viewModel.onCityChange("")
        viewModel.onStreetChange("")
        viewModel.onZipChange("")
        viewModel.onPasswordChange("")
        viewModel.onPasswordCheckChange("")

        viewModel.signup()
        assert(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun registrationButtonWithMismatchingPasswordsSetsErrorState() {
        viewModel.onUsernameChange("User")
        viewModel.onEmailChange("test@test.de")
        viewModel.onCityChange("Beispielstadt")
        viewModel.onStreetChange("Beispielstraße 1")
        viewModel.onZipChange("12345")
        viewModel.onPasswordChange("passwort123")
        viewModel.onPasswordCheckChange("passwort12345")

        viewModel.signup()
        val state = viewModel.authState.value as AuthState.Error
        assertEquals("Das Passwort stimmt nicht überein", state.message)
    }

    @Test
    fun registrationButtonWithMissingUsernameSetsErrorState() {
        viewModel.onUsernameChange("")
        viewModel.onEmailChange("test@test.de")
        viewModel.onCityChange("Beispielstadt")
        viewModel.onStreetChange("Beispielstraße 1")
        viewModel.onZipChange("12345")
        viewModel.onPasswordChange("passwort123")
        viewModel.onPasswordCheckChange("passwort123")

        viewModel.signup()
        assert(viewModel.authState.value is AuthState.Error)
    }
}