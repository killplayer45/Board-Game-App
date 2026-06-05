package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.board_gamer_app.data.model.Event
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModel
import com.example.board_gamer_app.data.model.User
import com.example.board_gamer_app.ui.component.SuggestionItem
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel

@Composable
fun SuggestionScreen(navController: NavController, eventID: String, eventViewModel: EventViewModel, suggestionsViewModel: SuggestionsViewModel, authViewModel: AuthViewModel, modifier: Modifier = Modifier
) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> { Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetToAuthenticated() }
            is AuthState.Info -> { Toast.makeText(context, (authState.value as AuthState.Info).message, Toast.LENGTH_LONG).show()
                authViewModel.resetToAuthenticated() }
            else -> Unit
        }
    }

    val suggestions by suggestionsViewModel.suggestions.collectAsStateWithLifecycle()
    val reviews by suggestionsViewModel.reviews.collectAsStateWithLifecycle()
    val currentUsername = eventViewModel.currentUsername
    val events = eventViewModel.events.collectAsStateWithLifecycle()
    val event = events.value.find { it.eventID == eventID } ?: return

    LaunchedEffect(eventID) {
        suggestionsViewModel.loadSuggestions(eventID)
        suggestionsViewModel.loadReviews(eventID)
    }

    // Determine the "Game of the Evening" (highest positive votes)
    val gameOfTheEvening = suggestions.maxByOrNull { it.positiveVotesCount }?.takeIf { it.positiveVotesCount > 0 }

    var selectedTermin by remember { mutableStateOf("") }

    LaunchedEffect(reviews) {
        if(selectedTermin.isEmpty() && reviews.isNotEmpty()) {
            selectedTermin = reviews.first().eventID
        }
    }

    var gameMasterUser by remember { mutableStateOf<User?>(null)}
    LaunchedEffect(event.gameMasterID) {
        if(event.gameMasterID.isNotEmpty()) {
            authViewModel.loadUserByID(event.gameMasterID) { user ->
                gameMasterUser = user
            }
        } else {
            gameMasterUser = null
        }
    }
    Scaffold( bottomBar = { NavBar(navController) }) {
            innerPadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Terminübersicht",
                    fontSize = 30.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(onClick = { eventViewModel.onDeleteEventDialog() }, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Termin löschen",
                        tint = Color.Red,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            if(eventViewModel.showDeleteEventDialog) {
                DeleteEventDialog(onConfirm = { eventViewModel.deleteEvent(eventID)
                    eventViewModel.onDismissDeleteEventDialog()
                    navController.navigate("homepage")},
                    onDismiss = { eventViewModel.onDismissDeleteEventDialog() })
            }
            if(suggestionsViewModel.deleteSuggestionDialog) {
                DeleteSuggestionDialog(onConfirm = { suggestionsViewModel.deleteSuggestion(suggestionsViewModel.selectedSuggestion)
                    },
                    onDismiss = { suggestionsViewModel.onDismissDeleteSuggestionDialog() })
            }
            if(suggestionsViewModel.deleteReviewDialog) {
                DeleteRatingDialog(onConfirm = { suggestionsViewModel.deleteRating(suggestionsViewModel.selectedReview) },
                    onDismiss = { suggestionsViewModel.onDismissDeleteReviewDialog() })
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Text(text = "Datum: ${event.date}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Uhrzeit: ${event.time}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Spielvorschläge", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    gameOfTheEvening?.let { game ->
                        Surface(
                            modifier = Modifier.padding(vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Spiel des Abends: ${gameOfTheEvening.title}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        OutlinedTextField(
                            value = suggestionsViewModel.titleInput,
                            onValueChange = { suggestionsViewModel.onTitleChange(it) },
                            placeholder = { Text("Titel des Spiels") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = suggestionsViewModel.descriptionInput,
                            onValueChange = { suggestionsViewModel.onDescriptionChange(it) },
                            placeholder = { Text("Kurze Beschreibung") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                if (suggestionsViewModel.titleInput.isNotBlank()) {
                                    IconButton(onClick = {
                                        suggestionsViewModel.addSuggestion(eventID, currentUsername)
                                    }) {
                                        Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // List of Suggestions
                items(suggestions, key = { it.id }) { suggestion ->
                    SuggestionItem(
                        suggestion = suggestion,
                        currentUsername = currentUsername,
                        onVote = { isPositive ->
                            suggestionsViewModel.vote(suggestion.id, isPositive, currentUsername)
                        }, suggestionsViewModel = suggestionsViewModel
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp))
                    PlayerList(event = event, eventViewModel = eventViewModel) }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "Gastgeberbewertung", fontSize = 24.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(event.gameMasterID.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(48.dp)
                            ) {
                                if (authViewModel.profileImageUrl.isNotEmpty()) {
                                    Base64Image(
                                        base64 = authViewModel.profileImageUrl,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            if(event.gameMasterID.isNotEmpty()) {
                                gameMasterUser?.let { gm ->
                                    Text(
                                        gm.username,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "${gm.street}, ${gm.zip} ${gm.city}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            } else {
                                Text(text = "Kein Spielleiter eingetragen",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Review History
                    reviews.filter { it.eventID == selectedTermin }.forEach { review ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .combinedClickable(onLongClick = { suggestionsViewModel.onDeleteReviewDialog()
                                    suggestionsViewModel.selectedReview = review.id }, onClick = {}),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                tonalElevation = 2.dp
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(review.user, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Row {
                                            repeat(5) { i ->
                                                Icon(
                                                    if (i < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                    null,
                                                    modifier = Modifier.size(12.dp),
                                                    tint = if (i < review.rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
                                    }
                                    Text(review.text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = { suggestionsViewModel.onRatingChange(index + 1) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (index < suggestionsViewModel.selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (index < suggestionsViewModel.selectedRating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = suggestionsViewModel.reviewInput,
                        onValueChange = { suggestionsViewModel.onReviewChange(it) },
                        placeholder = { Text("Hier Text eingeben") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                if(event.gameMasterID.isNotEmpty()) {
                                    suggestionsViewModel.addReview(eventID, currentUsername)
                                } else {
                                    Toast.makeText(context, "Kein Spielleiter zur Bewertung eingetragen", Toast.LENGTH_LONG).show()
                                }
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteEventDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Termin löschen") },
        text = { Text("Möchtest du den Termin wirklich löschen?")},
        confirmButton = { Button(onClick = { onConfirm() }) {
            Text("Bestätigen")
        }},
        dismissButton = { Button(onClick = { onDismiss() }) {
            Text("Abbrechen")
        } }
    )
}

@Composable
fun DeleteSuggestionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Spielvorschlag löschen") },
        text = { Text("Möchtest du den Spielvorschlag wirklich löschen?")},
        confirmButton = { Button(onClick = { onConfirm() }) {
            Text("Bestätigen")
        }},
        dismissButton = { Button(onClick = { onDismiss() }) {
            Text("Abbrechen")
        } }
    )
}

@Composable
fun DeleteRatingDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Bewertung löschen") },
        text = { Text("Möchtest du die Bewertung wirklich löschen?")},
        confirmButton = { Button(onClick = { onConfirm() }) {
            Text("Bestätigen")
        }},
        dismissButton = { Button(onClick = { onDismiss() }) {
            Text("Abbrechen")
        } }
    )
}


@Composable
fun PlayerList(event: Event, eventViewModel: EventViewModel) {
    var attendingPlayers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var notAttendingPlayers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(event.eventID) {
        eventViewModel.loadPlayerNames(event.playersAttending) { attendingPlayers = it }
        eventViewModel.loadPlayerNames(event.playersNotAttending) { notAttendingPlayers = it }
    }
    Column {
        Text(
            text = "Zusagen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (attendingPlayers.isEmpty()) {
            Text(text = "Noch keine Zusagen")
        } else {
            attendingPlayers.values.forEach { name ->
                Text(name, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Absagen",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        if (notAttendingPlayers.isEmpty()) {
            Text("Noch keine Absagen")
        } else {
            notAttendingPlayers.values.forEach { name ->
                Text(name, fontSize = 14.sp)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SuggestionsScreenPreview() {
    //SuggestionsScreen()
}