package com.example.board_gamer_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.board_gamer_app.ui.viewmodels.EventViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class EventViewModelTest {

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockAuth: FirebaseAuth = mock(FirebaseAuth::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS)
    private val mockDb: FirebaseFirestore = mock(FirebaseFirestore::class.java, org.mockito.Answers.RETURNS_DEEP_STUBS)
    private lateinit var viewModel: EventViewModel

    @Before
    fun setup() {
        viewModel = EventViewModel(mockDb, mockAuth)
    }

    @Test
    fun `addEvent mit leerem Datum`() {
        viewModel.onTimeChange(18, 0)
        val eventsBefore = viewModel.events.value
        viewModel.addEvent()

        assertEquals(eventsBefore, viewModel.events.value)
    }

    @Test
    fun `addEvent mit leerer Uhrzeit`() {
        viewModel.onDateSelected(System.currentTimeMillis())
        val eventsBefore = viewModel.events.value
        viewModel.addEvent()

        assertEquals(eventsBefore, viewModel.events.value)
    }

    @Test
    fun `onTimeChange formatiert die Uhrzeit korrekt`() {
        viewModel.onTimeChange(9, 5)
        assertEquals("09:05", viewModel.time)
    }

    @Test
    fun `onTimeChange mit zweistelligen Werten formatiert korrekt`() {
        viewModel.onTimeChange(18, 30)
        assertEquals("18:30", viewModel.time)
    }

    @Test
    fun `onTimeChange berechnet Millisekunden korrekt`() {
        viewModel.onTimeChange(1, 0)
        assertEquals(3_600_000L, viewModel.timeInMillis)
    }

    @Test
    fun `onDateSelected mit null`() {
        viewModel.onDateSelected(null)
        assertEquals("", viewModel.date)
    }

    @Test
    fun `onDateSelected formatiert Datum korrekt`() {
        //01.01.2025 00:00 UTC
        viewModel.onDateSelected(1735689600000L)
        assertTrue(viewModel.date.isNotEmpty())
        assertTrue(viewModel.date.contains("2025"))
    }

    @Test
    fun `onDismissAddEventDialog setzt Datum und Uhrzeit zurück`() {
        viewModel.onDateSelected(System.currentTimeMillis())
        viewModel.onTimeChange(18, 30)
        viewModel.onDismissAddEventDialog()
        assertEquals("", viewModel.date)
        assertEquals("", viewModel.time)
    }

    @Test
    fun `onShowAddEventDialog setzt showAddEventDialog auf true`() {
        viewModel.onShowAddEventDialog()
        assertTrue(viewModel.showAddEventDialog)
    }

    @Test
    fun `onDismissAddEventDialog setzt showAddEventDialog auf false`() {
        viewModel.onShowAddEventDialog()
        viewModel.onDismissAddEventDialog()
        assertTrue(!viewModel.showAddEventDialog)
    }

    @Test
    fun `onShowCalendar setzt showCalendar auf true`() {
        viewModel.onShowCalendar()
        assertTrue(viewModel.showCalendar)
    }

    @Test
    fun `onDismissCalendar setzt showCalendar auf false`() {
        viewModel.onShowCalendar()
        viewModel.onDismissCalendar()
        assertTrue(!viewModel.showCalendar)
    }

    @Test
    fun `onShowTime setzt showTime auf true`() {
        viewModel.onShowTime()
        assertTrue(viewModel.showTime)
    }

    @Test
    fun `onDismissTime setzt showTime auf false`() {
        viewModel.onShowTime()
        viewModel.onDismissTime()
        assertTrue(!viewModel.showTime)
    }

    @Test
    fun `updateGameMaster bei nicht gefundendem Event`() {
        //Event-Liste enthält kein Event mit der ID
        viewModel.updateGameMaster("nicht vorhandene ID")
    }

    @Test
    fun `addPlayerToNotAttendingList wenn Event nicht gefunden`() {
        viewModel.addPlayerToNotAttendingList("nicht vorhandene ID")
    }
}