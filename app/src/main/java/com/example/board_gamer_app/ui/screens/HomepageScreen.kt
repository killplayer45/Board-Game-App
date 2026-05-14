package com.example.board_gamer_app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.board_gamer_app.R
import com.example.board_gamer_app.data.model.Event
import com.example.board_gamer_app.ui.viewmodels.AuthState
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel
import com.example.board_gamer_app.ui.viewmodels.EventViewModel
import java.util.Calendar

@Composable
fun HomepageScreen(navController: NavController, authViewModel: AuthViewModel, eventViewModel: EventViewModel, chatViewModel: ChatViewModel, modifier: Modifier = Modifier) {
    //AuthState is saved and observed
    val authState = authViewModel.authState.collectAsStateWithLifecycle()
    //used for accessing resources like Toast
    val context = LocalContext.current
    //LaunchedEffect enables side effects like navigation or Toast when authState.value changes (after composition)
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> { eventViewModel.loadCurrentUser()
            chatViewModel.reload() }
            is AuthState.Unauthenticated -> navController.navigate("welcome")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    //Values of events from ViewModel are tracked and saved in variable
    val events by eventViewModel.events.collectAsStateWithLifecycle()
    if(eventViewModel.showDialog) {
        AddEventDialog(eventViewModel = eventViewModel,
            onDismiss = {eventViewModel.onDismissDialog()})
    }
    Scaffold( bottomBar = { NavBar(navController) }) {
        innerPadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            HomepageHeader(eventViewModel)
            //For dynamically generated list of events
            LazyColumn {
                items(events) {event ->
                    EventSection(event = event,
                        onClick = { navController.navigate("event_detail/${event.eventID}")},
                        eventViewModel = eventViewModel)
                }
            }
        }
    }
}

@Composable
fun HomepageHeader(eventViewModel: EventViewModel, modifier: Modifier = Modifier) {
    Spacer(modifier = Modifier.height(40.dp))
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(start = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround) {
        Text(
            text = "Termine",
            fontSize = 40.sp
        )
        FloatingActionButton(onClick = { eventViewModel.onShowDialog()}, containerColor = Color(0xFFfcba03), contentColor = Color.White, modifier = Modifier.padding(start = 60.dp).scale(0.8F)) {
            Icon(painter = painterResource(R.drawable.add_48dp_000000_fill0_wght400_grad0_opsz48),
                contentDescription = "Termin hinzufügen Button")
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun EventSection(event: Event, onClick: () -> Unit, eventViewModel: EventViewModel, modifier: Modifier = Modifier) {
    val backgroundColorAttending = if(event.playersAttending.contains(eventViewModel.currentUsername)) Color(0xFF1ff262) else Color.White
    val backgroundColorNotAttending = if(event.playersNotAttending.contains(eventViewModel.currentUsername)) Color(0xFFf52a52) else Color.White
    val crownIcon = if(event.gameMaster == eventViewModel.currentUsername) R.drawable.crown_48dp_ffffff_fill1_wght400_grad0_opsz48 else R.drawable.crown_24dp_ffffff_fill0_wght400_grad0_opsz24
    Box {
        Button(
            onClick = onClick,
            shape = RectangleShape,
            modifier = Modifier
                .height(80.dp)
                .width(300.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFfcba03),
                contentColor = Color.White
            )
        )
        {
            Text(
                text = "${event.date}  ${event.time}",
                textAlign = TextAlign.Left,
                fontSize = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }
        IconButton(onClick = { eventViewModel.updateGameMaster(event.eventID)}, modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 10.dp)
            .size(70.dp)) {
            Icon(
                painter = painterResource(crownIcon),
                contentDescription = "Spielleiter Icon",
                modifier = Modifier
                    .size(50.dp),
                tint = Color.White
            )
        }
        if(event.gameMaster == eventViewModel.currentUsername) {
        Icon(painter = painterResource(R.drawable.check_circle_48dp_000000_fill1_wght500_grad0_opsz48),
            contentDescription = "Spielleiter Check Icon",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp, top = 15.dp),
            tint = Color.Black
        )}
    }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            IconButton(
                onClick = { eventViewModel.addPlayerToAttendingList(event.eventID) },
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RectangleShape
                    )
                    .background(color = backgroundColorAttending)
            ) {
                Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Daumen hoch",
                    modifier = Modifier
                        .size(60.dp)
                )
            }
            IconButton(
                onClick = { eventViewModel.addPlayerToNotAttendingList(event.eventID)},
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RectangleShape
                    )
                    .background(color = backgroundColorNotAttending)
            ) {
                Icon(
                    Icons.Default.ThumbDown,
                    contentDescription = "Daumen runter",
                    modifier = Modifier
                        .size(60.dp)
                )
            }
        }
    Spacer(modifier = Modifier.height(40.dp))
}

@Composable
fun NavBar(navController: NavController) {
    //saves current Screen for highlighting the nav bar symbol
    val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == "homepage",
            onClick = { navController.navigate("homepage")},
            label = { Text("Kalender") },
            icon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Events Icon"
                )
            }
        )
        NavigationBarItem(
            selected = currentScreen == "chat",
            onClick = { navController.navigate("chat")},
            label = { Text("Chat") },
            icon = {
                Icon(Icons.Default.ChatBubble,
                    contentDescription = "Chat Icon"
                )
            }
        )
        NavigationBarItem(
            selected = currentScreen == "settings",
            onClick = { navController.navigate("settings")},
            label = { Text("Einstellungen") },
            icon = {
                Icon(Icons.Default.Settings,
                    contentDescription = "Einstellungen Icon"
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(eventViewModel: EventViewModel, onDismiss: () -> Unit) {
    if(eventViewModel.showCalendar){
        DatePickerModal(onDateSelected = {timestamp ->
            eventViewModel.onDateSelected(timestamp)
        },
            onDismiss = { eventViewModel.onDismissCalendar()})
    }
    if(eventViewModel.showTime){
        TimePicker(onConfirm = {selectedTime ->
            eventViewModel.onTimeChange(selectedTime.hour, selectedTime.minute)
        },
            onDismiss = { eventViewModel.onDismissTime() })
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Termin hinzufügen")},
        text = {
            Column(verticalArrangement = Arrangement.Center) {
                OutlinedTextField(
                    value = eventViewModel.date,
                    onValueChange = { },
                    label = {Text(text = "Datum")},
                    placeholder = {Text(text = "")},
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { eventViewModel.onShowCalendar() }) {
                            Icon(painter = painterResource(R.drawable.event_note_48dp_000000_fill0_wght400_grad0_opsz48),
                                contentDescription = "Kalender Icon")
                        }
                    },
                    readOnly = true
                )
                OutlinedTextField(
                    value = eventViewModel.time,
                    onValueChange = { },
                    label = {Text(text = "Zeit")},
                    placeholder = {Text("")},
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { eventViewModel.onShowTime() }) {
                            Icon(painter = painterResource(R.drawable.timer_48dp_000000_fill0_wght400_grad0_opsz48),
                                contentDescription = "Zeit Icon")
                        }
                    },
                    readOnly = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { eventViewModel.addEvent() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFfcba03)
                )){
                Text(text = "Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss)
            {Text("Abbrechen")}
        }
    )
}

@Composable
fun DatePickerModal(onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Abbrechen")
            }
        }
    ) { DatePicker(state = datePickerState) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(onConfirm: (TimePickerState) -> Unit, onDismiss: () -> Unit) {
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )
    AlertDialog(onDismissRequest = onDismiss,
        title = {Text("Uhrzeit wählen")},
        text = {
            TimeInput(
                state = timePickerState
            )
        },
        confirmButton = {Button(onClick = {onConfirm(timePickerState)
            onDismiss()}) {
            Text(text = "OK")
        }},
        dismissButton = {Button(onClick = onDismiss) {
            Text(text = "Abbrechen")
        }})
}