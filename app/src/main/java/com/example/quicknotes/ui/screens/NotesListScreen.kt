package com.example.quicknotes.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quicknotes.R
import com.example.quicknotes.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ANIMATION_DURATION = 300L
private const val DELETE_BUTTON_SCALE_EXPANDED = 1f
private const val DELETE_BUTTON_SCALE_COLLAPSED = 0f
private val DEFAULT_SPACING = 16.dp
private val SMALL_SPACING = 8.dp

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
    var isGridView by rememberSaveable { mutableStateOf(false) }

    NotesListScaffold(
        notes = notes,
        onAddClick = onAddNoteClick,
        isDarkTheme = isDarkTheme,
        onThemeToggle = onThemeToggle,
        isGridView = isGridView,
        onViewToggle = { isGridView = !isGridView },
        content = { paddingValues ->
            NotesListContent(
                notes = notes,
                paddingValues = paddingValues,
                deletingNoteId = deletingNoteId,
                onDeleteClick = { noteToDelete = it },
                onNoteClick = onEditNote,
                isGridView = isGridView,
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
    isGridView: Boolean,
) {
    if (notes.isEmpty()) {
        EmptyNotesMessage(paddingValues)
    } else {
        AnimatedContent(
            targetState = isGridView,
            transitionSpec = {
                val enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 150,
                        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
                    )
                ) + scaleIn(
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 100,
                        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
                    ),
                    initialScale = 0.98f
                )

                val exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 450,
                        easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                    )
                ) + scaleOut(
                    animationSpec = tween(
                        durationMillis = 450,
                        easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                    ),
                    targetScale = 1.02f
                )

                enter.togetherWith(exit)
            },
            label = "layout_transition"
        ) { targetIsGridView ->
            if (targetIsGridView) {
                NotesGrid(
                    notes = notes,
                    paddingValues = paddingValues,
                    deletingNoteId = deletingNoteId,
                    onDeleteClick = onDeleteClick,
                    onNoteClick = onNoteClick,
                )
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotesListScaffold(
    notes: List<Note>,
    onAddClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isGridView: Boolean,
    onViewToggle: () -> Unit,
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
                                text = stringResource(R.string.my_notes),
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
                        IconButton(
                            onClick = onViewToggle,
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.5f,
                                    ),
                                ),
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Animated transition between icons
                                AnimatedContent(
                                    targetState = isGridView,
                                    transitionSpec = {
                                        (fadeIn(animationSpec = tween(200)) +
                                                scaleIn(
                                                    initialScale = 0.7f,
                                                    animationSpec = tween(200)
                                                )).togetherWith(
                                            fadeOut(animationSpec = tween(200)) +
                                                    scaleOut(
                                                        targetScale = 0.7f,
                                                        animationSpec = tween(200)
                                                    )
                                        )
                                    },
                                    label = "icon_transition"
                                ) { gridViewState ->
                                    Icon(
                                        painterResource(
                                            if (gridViewState) {
                                                R.drawable.ic_list_action
                                            } else {
                                                R.drawable.ic_round_grid_view
                                            }
                                        ),
                                        contentDescription = if (gridViewState) {
                                            stringResource(R.string.switch_to_list_view)
                                        } else {
                                            stringResource(R.string.switch_to_grid_view)
                                        },
                                        modifier = Modifier
                                            .size(24.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                        }

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
                                painter = if (isDarkTheme) {
                                    painterResource(R.drawable.ic_light_mode_24dp)
                                } else {
                                    painterResource(R.drawable.ic_dark_mode_24dp)
                                },
                                contentDescription = if (isDarkTheme) {
                                    stringResource(R.string.switch_to_light_theme)
                                } else {
                                    stringResource(R.string.switch_to_dark_theme)
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
                        contentDescription = stringResource(R.string.add_note),
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
                text = stringResource(R.string.no_notes_yet),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tap_the_button_to_create_your_first_note),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
            notes.indexOf(note)
            AnimatedNoteCard(
                note = note,
                isDeleting = note.id == deletingNoteId,
                onDeleteClick = { onDeleteClick(note) },
                onClick = { onNoteClick(note) },
                isGridView = false,
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotesGrid(
    notes: List<Note>,
    paddingValues: PaddingValues,
    deletingNoteId: String?,
    onDeleteClick: (Note) -> Unit,
    onNoteClick: (Note) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(DEFAULT_SPACING),
        verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        items(
            items = notes,
            key = { note -> note.id },
        ) { note ->
            notes.indexOf(note)
            AnimatedNoteCard(
                note = note,
                isDeleting = note.id == deletingNoteId,
                onDeleteClick = { onDeleteClick(note) },
                onClick = { onNoteClick(note) },
                isGridView = true,
                modifier = Modifier.animateItemPlacement(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
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
    isGridView: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isDeleting) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 0.4f else 1f,
        animationSpec = tween(
            durationMillis = 400,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
        ),
        label = "card_alpha"
    )

    AnimatedVisibility(
        visible = !isDeleting,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 500,
                easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
            )
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.95f
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 350,
                easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = 350,
                easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
            ),
            targetScale = 0.85f
        ),
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    alpha = animatedAlpha
                }
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
                .then(modifier)
        ) {
            NoteCard(
                note = note,
                isDeleting = isDeleting,
                onDeleteClick = onDeleteClick,
                onClick = onClick,
                isGridView = isGridView,
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
    isGridView: Boolean = false,
) {
    val cardElevation by animateDpAsState(
        targetValue = if (isGridView) 4.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "card_elevation"
    )

    val cardCornerRadius by animateDpAsState(
        targetValue = if (isGridView) 20.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "card_corner_radius"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isGridView) {
                    Modifier.aspectRatio(1f)
                } else {
                    Modifier.padding(vertical = 4.dp)
                }
            )
            .clickable(onClick = onClick)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            ),
        shape = RoundedCornerShape(cardCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation,
            pressedElevation = cardElevation + 2.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(if (isGridView) 12.dp else 16.dp)
                .fillMaxWidth()
                .then(
                    if (isGridView) {
                        Modifier.fillMaxHeight()
                    } else {
                        Modifier
                    }
                ),
        ) {
            NoteCardHeader(
                title = note.title,
                isDeleting = isDeleting,
                onDeleteClick = onDeleteClick,
                isCompact = isGridView,
            )
            Spacer(modifier = Modifier.height(if (isGridView) 4.dp else 8.dp))

            if (isGridView) {
                // In grid view, use weight to fill available space
                Box(modifier = Modifier.weight(1f)) {
                    NoteCardContent(
                        content = note.content,
                        maxLines = 4,
                    )
                }
            } else {
                NoteCardContent(
                    content = note.content,
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Divider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                    thickness = 1.dp,
                )
            }

            Spacer(modifier = Modifier.height(if (isGridView) 4.dp else 8.dp))
            NoteCardFooter(
                timestamp = note.timestamp,
                isCompact = isGridView,
            )
        }
    }
}

@Composable
private fun NoteCardHeader(
    title: String,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    isCompact: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = if (isCompact) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                )
            } else {
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                )
            },
            maxLines = if (isCompact) 2 else 1,
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
            modifier = Modifier
                .scale(deleteButtonScale)
                .size(if (isCompact) 36.dp else 48.dp),
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_note),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp),
            )
        }
    }
}

@Composable
private fun NoteCardContent(
    content: String,
    maxLines: Int = 3,
) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun NoteCardFooter(
    timestamp: Long,
    isCompact: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(if (isCompact) 14.dp else 16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isCompact) formatTimestampCompact(timestamp) else formatTimestamp(timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DeleteNoteDialog(
    note: Note,
    onDismiss: () -> Unit,
    onConfirm: (Note) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.delete_note),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        },
        text = {
            Text(
                stringResource(R.string.delete_warning_message),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(note)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

private fun handleNoteDeletion(
    note: Note,
    onDelete: (Note) -> Unit,
    onComplete: () -> Unit,
) {
    kotlinx.coroutines.MainScope().launch(Dispatchers.IO) {
        delay(ANIMATION_DURATION)
        onDelete(note)
        onComplete()
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

private fun formatTimestampCompact(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
