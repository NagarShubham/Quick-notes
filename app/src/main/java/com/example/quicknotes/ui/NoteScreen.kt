package com.example.quicknotes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.quicknotes.R
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.theme.Typography
import com.example.quicknotes.ui.viewmodel.HomeViewModel

//@Preview(showBackground = true)
@Composable
fun NoteScreen(homeViewModel: HomeViewModel, onBackPress: () -> Unit) {
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        homeViewModel.getSelectedNote()?.let {
            titleText = it.title
            contentText = it.content
        }
    }

    Column(Modifier.fillMaxSize()) {
        CreateHeader(
            onBackPress = onBackPress, onSavePress = {
                if (titleText.isEmpty() && contentText.isEmpty()) return@CreateHeader

                var temp = NotesModel(
                    title = titleText,
                    content = contentText,
                    updatedAt = System.currentTimeMillis()
                )

                homeViewModel.getSelectedNote()?.let {
                    temp = temp.copy(id = it.id)
                }

                homeViewModel.addNote(temp)
                onBackPress.invoke()
            },
            isDelete = homeViewModel.getSelectedNote() != null,
            onDelete = {
                homeViewModel.deleteNote(homeViewModel.getSelectedNote()!!)
                onBackPress.invoke()
            })

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.Gray
        )
        TextField(
            value = titleText,
            onValueChange = { titleText = it },
            placeholder = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = Typography.labelLarge,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            )
        )
        TextField(
            value = contentText,
            onValueChange = { contentText = it },
            placeholder = { Text(stringResource(R.string.note)) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = Typography.labelLarge,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
private fun CreateHeader(
    onBackPress: () -> Unit,
    onSavePress: () -> Unit,
    isDelete: Boolean = false,
    onDelete: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "BackToHome",
            modifier = Modifier
                .clickable(onClick = onBackPress)
                .size(24.dp)
                .padding(2.dp)
        )
        Spacer(Modifier.weight(1f))

        AnimatedVisibility(isDelete) {
            Icon(
                painter = painterResource(R.drawable.ic_delete_24),
                contentDescription = stringResource(R.string.delete),
                modifier = Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .clickable(onClick = onDelete)
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_save_24),
            contentDescription = stringResource(R.string.save),
            modifier = Modifier
                .size(26.dp)
                .padding(2.dp)
                .clickable(onClick = onSavePress)
        )
    }
}