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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quicknotes.R
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.viewmodel.HomeViewModel

//@Preview(showBackground = true)
@Composable
fun CreateNote(navController: NavHostController, homeViewModel: HomeViewModel) {
    var titleText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }



    Column(Modifier.fillMaxSize()) {
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
                modifier = Modifier.clickable { navController.popBackStack() }
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Delete"
            )

            Text("Save", modifier = Modifier.clickable {
                if (titleText.isEmpty() && noteText.isEmpty()) return@clickable

                homeViewModel.addNote(
                    NotesModel(
                        title = titleText,
                        note = noteText,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                navController.popBackStack()
            })
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.Gray
        )

        TextField(
            value = titleText,
            onValueChange = { newText -> titleText = newText },
            placeholder = { Text("Title") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        TextField(
            value = noteText,
            onValueChange = { newText -> noteText = newText },
            placeholder = { Text("Note") },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }


}