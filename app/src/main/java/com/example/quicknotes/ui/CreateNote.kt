package com.example.quicknotes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import com.example.quicknotes.R
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.theme.Typography
import com.example.quicknotes.ui.viewmodel.HomeViewModel

//@Preview(showBackground = true)
@Composable
fun CreateNote(navController: NavHostController, homeViewModel: HomeViewModel) {
    var titleText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        CreateHeader(onBackPress = { navController.popBackStack() }, onSavePress = {
            if (titleText.isEmpty() && noteText.isEmpty()) return@CreateHeader
            homeViewModel.addNote(
                NotesModel(
                    title = titleText,
                    note = noteText,
                    updatedAt = System.currentTimeMillis()
                )
            )
            navController.popBackStack()
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
            value = noteText,
            onValueChange = { noteText = it },
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
private fun CreateHeader(onBackPress: () -> Unit, onSavePress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "BackToHome",
            modifier = Modifier.clickable(onClick = onBackPress)
        )

        Text(stringResource(R.string.save), modifier = Modifier.clickable(onClick = onSavePress))
    }
}