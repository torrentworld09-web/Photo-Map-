package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GridLayoutMode
import com.example.data.model.Post
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassCircleIconButton
import com.example.ui.components.PostCard
import com.example.ui.theme.BrandBlue

@Composable
fun PostsGridScreen(
    title: String,
    posts: List<Post>,
    currentLayoutMode: GridLayoutMode,
    onBackClick: () -> Unit,
    onLayoutChange: (GridLayoutMode) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onPostClick: (Post) -> Unit,
    onPostLongClick: (Post) -> Unit,
    onToggleFavorite: (Post) -> Unit,
    onAddClick: () -> Unit
) {
    var layoutMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar with Functional Back Button (Theme-aware, no extra empty space)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GlassCircleIconButton(
                            onClick = onBackClick,
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${posts.size} items",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = onFilterClick) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Grid layout switcher
                        Box {
                            IconButton(onClick = { layoutMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "Change Layout",
                                    tint = BrandBlue
                                )
                            }

                            DropdownMenu(
                                expanded = layoutMenuExpanded,
                                onDismissRequest = { layoutMenuExpanded = false }
                            ) {
                                GridLayoutMode.entries.forEach { mode ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(mode.displayName)
                                                if (mode == currentLayoutMode) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = BrandBlue,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            layoutMenuExpanded = false
                                            onLayoutChange(mode)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Empty state if no posts
            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No photos yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button below to add your first photo post.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Post items in selected grid layout
                when (currentLayoutMode) {
                    GridLayoutMode.LIST_VIEW, GridLayoutMode.LARGE_CARD_VIEW, GridLayoutMode.LARGE_GRID -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(posts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    layoutMode = currentLayoutMode,
                                    onClick = { onPostClick(post) },
                                    onLongClick = { onPostLongClick(post) },
                                    onToggleFavorite = { onToggleFavorite(post) }
                                )
                            }
                        }
                    }
                    else -> {
                        // Multi-column grid
                        val columns = when (currentLayoutMode) {
                            GridLayoutMode.TWO_COLUMN, GridLayoutMode.MEDIUM_GRID, GridLayoutMode.MASONRY -> 2
                            GridLayoutMode.THREE_COLUMN, GridLayoutMode.SMALL_GRID, GridLayoutMode.TELEGRAM_COMPACT -> 3
                            GridLayoutMode.FOUR_COLUMN, GridLayoutMode.COMPACT_GRID -> 4
                            else -> 2
                        }

                        val spacing = if (columns >= 4) 4.dp else 10.dp

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 90.dp),
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            verticalArrangement = Arrangement.spacedBy(spacing)
                        ) {
                            items(posts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    layoutMode = currentLayoutMode,
                                    onClick = { onPostClick(post) },
                                    onLongClick = { onPostLongClick(post) },
                                    onToggleFavorite = { onToggleFavorite(post) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button (+)
        FloatingActionButton(
            onClick = onAddClick,
            containerColor = BrandBlue,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Photo")
        }
    }
}
