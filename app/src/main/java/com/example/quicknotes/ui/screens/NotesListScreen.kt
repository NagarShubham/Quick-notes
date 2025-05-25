package com.example.quicknotes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.quicknotes.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ANIMATION_DURATION = 300L
private const val DELETE_BUTTON_SCALE_EXPANDED = 1f
private const val DELETE_BUTTON_SCALE_COLLAPSED = 0f
private const val CARD_BACKGROUND_ALPHA = 0.3f
private val DEFAULT_SPACING = 16.dp
private val SMALL_SPACING = 8.dp
private val TINY_SPACING = 4.dp
private val DIALOG_CORNER_RADIUS = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    notes: List<Note>,
    onAddNoteClick: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
) {
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var deletingNoteId by remember { mutableStateOf<String?>(null) }

    NotesListScaffold(
        notes = notes,
        onAddClick = onAddNoteClick,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        content = { paddingValues ->
            NotesListContent(
                notes = notes,
                paddingValues = paddingValues,
                deletingNoteId = deletingNoteId,
                onDeleteClick = { noteToDelete = it },
                onNoteClick = onEditNote,
            )
        },
    )

    noteToDelete?.let { note ->
        DeleteNoteDialog(
            note = note,
            onDismiss = { noteToDelete = null },
            onConfirm = { note ->
                deletingNoteId = note.id
                noteToDelete = null
                handleNoteDeletion(
                    note = note,
                    onDelete = onDeleteNote,
                    onComplete = { deletingNoteId = null },
                )
            },
        )
    }
}

@Composable
private fun NotesListContent(
    notes: List<Note>,
    paddingValues: PaddingValues,
    deletingNoteId: String?,
    onDeleteClick: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    if (notes.isEmpty()) {
        EmptyNotesMessage(paddingValues)
    } else {
        NotesList(
            notes = notes,
            paddingValues = paddingValues,
            deletingNoteId = deletingNoteId,
            onDeleteClick = onDeleteClick,
            onNoteClick = onNoteClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesListScaffold(
    notes: List<Note>,
    onAddClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    ),
                ),
            ),
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "My Notes",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${notes.size} note${if (notes.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.7f,
                                ),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    actions = {
                        // Theme Toggle Button
                        val themeIconRotation by animateFloatAsState(
                            targetValue = if (isDarkTheme) 360f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                            label = "Theme toggle rotation",
                        )

                        IconButton(
                            onClick = onThemeToggle,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.5f,
                                    ),
                                    shape = CircleShape,
                                ),
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) {
                                    Icons.Default.FavoriteBorder
                                } else {
                                    Icons.Default.Favorite
                                },
                                contentDescription = if (isDarkTheme) {
                                    "Switch to Light Theme"
                                } else {
                                    "Switch to Dark Theme"
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .graphicsLayer {
                                        rotationZ = themeIconRotation
                                        scaleX = 1 + (themeIconRotation / 360f) * 0.2f
                                        scaleY = 1 + (themeIconRotation / 360f) * 0.2f
                                    },
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        // Add Note Button
                        IconButton(
                            onClick = onAddClick,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.5f,
                                    ),
                                    shape = CircleShape,
                                ),
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Note",
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 8.dp,
                    ),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Note",
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            containerColor = Color.Transparent,
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}

@Composable
private fun EmptyNotesMessage(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Notes Yet",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap the + button to create your first note",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    paddingValues: PaddingValues,
    deletingNoteId: String?,
    onDeleteClick: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(DEFAULT_SPACING),
        verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        items(
            items = notes,
            key = { note -> note.id },
        ) { note ->
            AnimatedNoteCard(
                note = note,
                isDeleting = note.id == deletingNoteId,
                onDeleteClick = { onDeleteClick(note) },
                onClick = { onNoteClick(note) },
            )
        }
    }
}

@Composable
private fun AnimatedNoteCard(
    note: Note,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = !isDeleting,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        NoteCard(
            note = note,
            isDeleting = isDeleting,
            onDeleteClick = onDeleteClick,
            onClick = onClick,
        )
    }
}

@Composable
private fun NoteCard(
    note: Note,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            NoteCardHeader(
                title = note.title,
                isDeleting = isDeleting,
                onDeleteClick = onDeleteClick,
            )
            Spacer(modifier = Modifier.height(8.dp))
            NoteCardContent(content = note.content)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                thickness = 1.dp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            NoteCardFooter(timestamp = note.timestamp)
        }
    }
}

@Composable
private fun NoteCardHeader(
    title: String,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        val deleteButtonScale by animateFloatAsState(
            targetValue = if (isDeleting) {
                DELETE_BUTTON_SCALE_COLLAPSED
            } else {
                DELETE_BUTTON_SCALE_EXPANDED
            },
            label = "Delete button scale",
        )
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.scale(deleteButtonScale),
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete Note",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun NoteCardContent(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun NoteCardFooter(timestamp: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = formatTimestamp(timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun DeleteNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirm: (Note) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DEFAULT_SPACING),
            shape = RoundedCornerShape(DIALOG_CORNER_RADIUS),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DeleteDialogContent(note.title)
                DeleteDialogButtons(
                    onDismiss = onDismiss,
                    onConfirm = { onConfirm(note) },
                )
            }
        }
    }
}

@Composable
private fun DeleteDialogContent(noteTitle: String) {
    Text(
        text = "Delete Note",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(modifier = Modifier.height(DEFAULT_SPACING))

    Text(
        text = "Are you sure you want to delete \"$noteTitle\"?",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun DeleteDialogButtons(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text("Cancel")
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            Text("Delete")
        }
    }
}

private fun handleNoteDeletion(
    note: Note,
    onDelete: (Note) -> Unit,
    onComplete: () -> Unit,
) {
    MainScope().launch(Dispatchers.IO) {
        delay(ANIMATION_DURATION)
        onDelete(note)
        onComplete()
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
