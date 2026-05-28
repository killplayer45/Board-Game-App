package com.example.board_gamer_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class ChatViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockAuth: FirebaseAuth = mock(FirebaseAuth::class.java,
        org.mockito.Answers.RETURNS_DEEP_STUBS)
    private val mockDb: FirebaseFirestore = mock(FirebaseFirestore::class.java,
        org.mockito.Answers.RETURNS_DEEP_STUBS)
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel(mockDb, mockAuth)
    }

    @Test
    fun `sendMessage mit leerem Text fügt keine Nachricht hinzu`() {
        viewModel.sendMessage("")
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun `sendMessage mit ausschließlich Leerzeichen fügt keine Nachricht hinzu`() {
        viewModel.sendMessage("   ")
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun `reload setzt currentUsername zurück`() {
        viewModel.currentUsername = "TestUser"
        viewModel.reload()
        assertEquals("", viewModel.currentUsername)
    }

    @Test
    fun `reload leert Nachrichten`() {
        viewModel.reload()
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun `messages Liste ist initial leer`() {
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun `currentUsername ist initial leer`() {
        assertEquals("", viewModel.currentUsername)
    }

    @Test
    fun `currentProfileImageUrl ist initial leer`() {
        assertEquals("", viewModel.currentProfileImageUrl)
    }
}