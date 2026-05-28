package com.example.board_gamer_app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.board_gamer_app.ui.MainActivity
import com.example.board_gamer_app.ui.screens.LoginScreen
import com.example.board_gamer_app.ui.viewmodels.AuthState
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuth: FirebaseAuth = mock(
        FirebaseAuth::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )
    private val mockDb: FirebaseFirestore = mock(
        FirebaseFirestore::class.java,
        org.mockito.Answers.RETURNS_DEEP_STUBS
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
            LoginScreen(navController = mockNavController, authViewModel = viewModel)
        }
    }

    @Test
    fun displayingLoginTitle() {
        composeTestRule.onAllNodesWithText("Anmelden")[0].assertIsDisplayed()
    }

    @Test
    fun displayingLoginSubtitle() {
        composeTestRule.onNodeWithText("Gib deine Daten ein, um fortzufahren.").assertIsDisplayed()
    }

    @Test
    fun displayingEmailLabel() {
        composeTestRule.onNodeWithText("Email-Adresse").assertIsDisplayed()
    }

    @Test
    fun displayingPasswordLabel() {
        composeTestRule.onNodeWithText("Passwort").assertIsDisplayed()
    }

    @Test
    fun displayingForgotPasswordButton() {
        composeTestRule.onNodeWithText("Passwort vergessen?").assertIsDisplayed()
    }

    @Test
    fun displayingLoginButton() {
        composeTestRule.onAllNodesWithText("Anmelden")[1].assertIsDisplayed()
    }

    @Test
    fun displayingRegistrationButton() {
        composeTestRule.onNodeWithText("Noch keinen Account? Registriere dich").assertIsDisplayed()
    }

    @Test
    fun passwordInputUpdatesViewModel() {
        viewModel.onPasswordChange("passwort123")
        assertEquals("passwort123", viewModel.password)
    }

    @Test
    fun loginButtonInvokesLogin() {
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")

        composeTestRule.onAllNodesWithText("Anmelden")[1].performClick()

        assert(viewModel.authState.value is AuthState.Error)
    }

    @Test
    fun clickingForgotPasswordWithEmptyEmail() {
        viewModel.onEmailChange("")
        composeTestRule.onNodeWithText("Passwort vergessen?").performClick()

        assert(viewModel.authState.value is AuthState.Error)
    }
}