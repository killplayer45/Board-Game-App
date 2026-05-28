package com.example.board_gamer_app

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.board_gamer_app.ui.screens.SettingsScreen
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockAuth: FirebaseAuth = mock(
        FirebaseAuth::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )

    private val mockDb: FirebaseFirestore = mock(
        FirebaseFirestore::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS
    )

    private lateinit var authViewModel: AuthViewModel
    private lateinit var chatViewModel: ChatViewModel

    @Before
    fun setup() {
        authViewModel = AuthViewModel(mockAuth, mockDb)
        chatViewModel = ChatViewModel(mockDb, mockAuth)
        authViewModel.resetToAuthenticated()
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "settings") {
                composable("settings") {
                    SettingsScreen(navController = navController, authViewModel = authViewModel, chatViewModel = chatViewModel)
                }
                composable("login") {
                    Text("Login")
                }
                composable("homepage") {
                    Text("Homepage")
                }
                composable("chat") {
                    Text("Chat")
                }
            }
        }
    }

    @Test
    fun displayingSettingsTitle() {
        composeTestRule.onAllNodesWithText("Einstellungen")[0].assertIsDisplayed()
    }

    @Test
    fun displayingSubtitle() {
        composeTestRule.onNodeWithText("Profilinformationen").assertIsDisplayed()
    }

    @Test
    fun displayingLogoutButton() {
        composeTestRule.onNodeWithText("Logout").assertIsDisplayed()
    }

    @Test
    fun displayingDarkModeLabel() {
        composeTestRule.onNodeWithText("Dark Mode").assertIsDisplayed()
    }

    @Test
    fun displayingCameraButton() {
        composeTestRule.onNodeWithContentDescription("Kamera").assertIsDisplayed()
    }

    @Test
    fun displayingGalleryButton() {
        composeTestRule.onNodeWithContentDescription("Galerie").assertIsDisplayed()
    }

    @Test
    fun displayingUsernameLabel() {
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun displayingEmailLabel() {
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
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
        composeTestRule.onNodeWithText("Straße").assertIsDisplayed()
    }

    @Test
    fun displayingSaveButton() {
        composeTestRule.onNodeWithText("Speichern").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun displayingChangePasswordButton() {
        composeTestRule.onNodeWithText("Passwort ändern").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun darkModeIsInitiallyOff() {
        assertFalse(authViewModel.isDarkMode)
    }

    @Test
    fun darkModeToggleUpdatesViewModel() {
        authViewModel.toggleDarkMode(true)
        assertTrue(authViewModel.isDarkMode)
    }

    @Test
    fun darkModeToggleCanBeTurnedOff() {
        authViewModel.toggleDarkMode(true)
        authViewModel.toggleDarkMode(false)
        assertFalse(authViewModel.isDarkMode)
    }

    @Test
    fun clickingChangePasswordShowsDialog() {
        composeTestRule.onNodeWithText("Passwort ändern").performScrollTo().performClick()
        composeTestRule.onAllNodesWithText("Passwort ändern")[1].assertIsDisplayed()
    }

    @Test
    fun dismissingPasswordDialogClearsFields() {
        authViewModel.onPasswordChange("passwort123")
        authViewModel.onPasswordCheckChange("passwort123")
        authViewModel.onDismissPasswordDialog()

        assertTrue(authViewModel.password.isEmpty())
        assertTrue(authViewModel.passwordCheck.isEmpty())
    }

    @Test
    fun passwordMismatchSetsErrorState() {
        authViewModel.onPasswordChange("passwort123")
        authViewModel.onPasswordCheckChange("passwort12345")
        authViewModel.updatePassword()

        assert(authViewModel.authState.value is AuthState.Error)
    }

    @Test
    fun emptyPasswordSetsErrorState() {
        authViewModel.onPasswordChange("")
        authViewModel.updatePassword()

        assert(authViewModel.authState.value is AuthState.Error)
    }

    @Test
    fun usernameInputUpdatesViewModel() {
        authViewModel.onUsernameChange("Neuer Username")
        assertTrue(authViewModel.username == "Neuer Username")
    }

    @Test
    fun emailInputUpdatesViewModel() {
        authViewModel.onEmailChange("neue_email@test.de")
        assertTrue(authViewModel.email == "neue_email@test.de")
    }

    @Test
    fun cityInputUpdatesViewModel() {
        authViewModel.onCityChange("Neue Stadt")
        assertTrue(authViewModel.city == "Neue Stadt")
    }

    @Test
    fun zipInputUpdatesViewModel() {
        authViewModel.onZipChange("56789")
        assertTrue(authViewModel.zip == "56789")
    }

    @Test
    fun streetInputUpdatesViewModel() {
        authViewModel.onStreetChange("Neue Straße 1")
        assertTrue(authViewModel.street == "Neue Straße 1")
    }
}