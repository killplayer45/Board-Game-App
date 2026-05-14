@file:JvmName("ChatScreenKt")

package com.example.board_gamer_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import com.example.board_gamer_app.data.model.Message
import com.example.board_gamer_app.ui.viewmodels.AuthViewModel
import com.example.board_gamer_app.ui.viewmodels.ChatViewModel

@Composable
fun ChatScreen(navController: NavController, authViewModel: AuthViewModel, chatViewModel: ChatViewModel) {
    var chatInput by remember { mutableStateOf("") }
    val messages = chatViewModel.messages
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold( bottomBar = { NavBar(navController) }) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = "Chatverlauf",
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Hier Text eingeben") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (chatInput.isNotBlank()) {
                                chatViewModel.sendMessage(chatInput)
                                chatInput = ""
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Senden",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )
            }
        }
    }

}

@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isMe) {
            UserAvatarWithInfo(message)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (message.isMe) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isMe) 16.dp else 0.dp,
                    bottomEnd = if (message.isMe) 0.dp else 16.dp
                ),
                color = if (message.isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (message.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = message.time,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        if (message.isMe) {
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatarWithInfo(message)
        }
    }
}

@Composable
fun UserAvatarWithInfo(message: Message) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(36.dp)
        ) {
            if (message.profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(message.profileImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = if(message.isMe) "@Me" else message.sender.split(" ").first(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    //ChatScreen(navController = NavController(LocalContext.current), authViewModel = AuthViewModel)
}