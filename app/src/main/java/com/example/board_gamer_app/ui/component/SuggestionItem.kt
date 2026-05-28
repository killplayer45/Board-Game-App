package com.example.board_gamer_app.ui.component

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.board_gamer_app.data.model.GameSuggestion
import com.example.board_gamer_app.ui.viewmodels.SuggestionsViewModel

@Composable
fun SuggestionItem(
    suggestion: GameSuggestion,
    currentUsername: String,
    onVote: (Boolean) -> Unit,
    suggestionsViewModel: SuggestionsViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(onLongClick = { suggestionsViewModel.onDeleteSuggestionDialog()
                                             suggestionsViewModel.selectedSuggestion = suggestion.id }, onClick = {})
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(suggestion.title, fontWeight = FontWeight.Bold)
                Text(suggestion.description, fontSize = 13.sp)
            }

            Row {
                IconButton(onClick = { onVote(true) }) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (suggestion.hasPositiveVote(currentUsername)) Color.Green else Color.Gray
                    )
                }

                IconButton(onClick = { onVote(false) }) {
                    Icon(
                        Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (suggestion.hasNegativeVote(currentUsername)) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}