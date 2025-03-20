package com.example.quicknotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quicknotes.R
import com.example.quicknotes.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun HomeScreen() {

    val distance = 12.dp
    val isGrid = remember {
        mutableStateOf(true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = distance, end = distance, top = 16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .padding(vertical = 4.dp, horizontal = 10.dp)
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                "Search", modifier = Modifier
                    .padding(start = 5.dp),
                style = Typography.labelSmall
            )

            Icon(
                painter = painterResource(if (isGrid.value) R.drawable.ic_list_action else R.drawable.ic_round_grid_view),
                contentDescription = "Grid",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable {
                        isGrid.value = isGrid.value.not()
                    }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(if (isGrid.value) 2 else 1), // Set the number of rows
            modifier = Modifier
                .fillMaxSize()
                .padding(top = distance),
            verticalArrangement = Arrangement.spacedBy(distance),
            horizontalArrangement = Arrangement.spacedBy(distance)
        ) {
            items(5) { index ->
                NotesCard()
            }
        }

    }
}


//@Preview(showBackground = true)
@Composable
private fun NotesCard() {
    val str =
        "High level element that displays text and provides semantics / accessibility information.\n" +
                "The default style uses the LocalTextStyle provided by the MaterialTheme / components. If you are setting your own style, you may want to consider first retrieving LocalTextStyle, and using TextStyle. copy to keep any theme defined attributes, only modifying the specific attributes you"
    Column(
        Modifier
            .defaultMinSize(minWidth = 120.dp, minHeight = 120.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Text(
            str,
            modifier = Modifier.fillMaxWidth(),
            style = Typography.labelLarge,
            maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        Text(
            str,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            style = Typography.bodySmall, maxLines = 7, overflow = TextOverflow.Ellipsis
        )
    }
}