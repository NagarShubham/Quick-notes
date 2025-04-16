package com.example.quicknotes.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.quicknotes.R
import com.example.quicknotes.common.NavigationRoutes
import com.example.quicknotes.db.NotesModel
import com.example.quicknotes.ui.theme.Typography
import com.example.quicknotes.ui.viewmodel.HomeViewModel

private val distance = 10.dp

//@Preview(showBackground = true)
@Composable
fun HomeScreen(navController: NavHostController, homeViewModel: HomeViewModel) {
    var notesList: List<NotesModel> = homeViewModel.noteList.collectAsStateWithLifecycle().value
    var isGrid by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
    ) {
        HomeHeader(isGrid = isGrid, onGridClick = { isGrid = !isGrid }) {
            homeViewModel.setSelectedNote(null)
            navController.navigate(NavigationRoutes.NoteScreen.route)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isGrid) 2 else 1), // Set the number of rows
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(distance),
            horizontalArrangement = Arrangement.spacedBy(distance)
        ) {
            items(notesList) {
                NotesCard(it) {
                    homeViewModel.setSelectedNote(it)
                    navController.navigate(NavigationRoutes.NoteScreen.route)
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
private fun HomeHeader(
    isGrid: Boolean = true,
    onGridClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 14.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Search",
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSearchClick),
            style = Typography.labelLarge
        )

        Icon(
            painter = painterResource(id = if (isGrid) R.drawable.ic_list_action else R.drawable.ic_round_grid_view),
            contentDescription = "Grid",
            modifier = Modifier
                .clip(CircleShape)
                .size(24.dp)
                .padding(2.dp)
                .clickable(onClick = onGridClick)
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun NotesCard(model: NotesModel, onNoteClick: () -> Unit) {
    Column(
        Modifier
            .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(14.dp)
            .clickable(onClick = onNoteClick)
    ) {
        Text(
            model.title,
            modifier = Modifier.fillMaxWidth(),
            style = Typography.labelLarge,
            maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        Text(
            model.content,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            style = Typography.bodySmall, maxLines = 7, overflow = TextOverflow.Ellipsis
        )
    }
}