package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SortOption
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LocalGlassTheme

data class FilterState(
    val sortOption: SortOption = SortOption.NEWEST,
    val favoritesOnly: Boolean = false,
    val cameraOnly: Boolean = false,
    val galleryOnly: Boolean = false,
    val importedOnly: Boolean = false,
    val offlineOnly: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortSheet(
    initialFilter: FilterState,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var tempSort by remember { mutableStateOf(initialFilter.sortOption) }
    var tempFavorites by remember { mutableStateOf(initialFilter.favoritesOnly) }
    var tempCamera by remember { mutableStateOf(initialFilter.cameraOnly) }
    var tempGallery by remember { mutableStateOf(initialFilter.galleryOnly) }
    var tempImported by remember { mutableStateOf(initialFilter.importedOnly) }
    var tempOffline by remember { mutableStateOf(initialFilter.offlineOnly) }

    val glassTheme = LocalGlassTheme.current
    val isDark = glassTheme.isDark
    val sheetBg = if (isDark) Color(0xFF0F172A) else Color.White
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = sheetBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Filter & Sort",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Section
            Text(
                text = "Sort By",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            SortOption.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { tempSort = option }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = tempSort == option,
                        onClick = { tempSort = option }
                    )
                    Text(
                        text = option.displayName,
                        fontSize = 14.sp,
                        color = textPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Filter Section
            Text(
                text = "Filter By",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FilterToggleRow(label = "⭐ Favorites", checked = tempFavorites, textColor = textPrimary, onCheckedChange = { tempFavorites = it })
            FilterToggleRow(label = "📷 Camera Photos", checked = tempCamera, textColor = textPrimary, onCheckedChange = { tempCamera = it })
            FilterToggleRow(label = "🖼 Gallery Photos", checked = tempGallery, textColor = textPrimary, onCheckedChange = { tempGallery = it })
            FilterToggleRow(label = "📥 Imported Photos", checked = tempImported, textColor = textPrimary, onCheckedChange = { tempImported = it })
            FilterToggleRow(label = "☁ Offline", checked = tempOffline, textColor = textPrimary, onCheckedChange = { tempOffline = it })

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempSort = SortOption.NEWEST
                        tempFavorites = false
                        tempCamera = false
                        tempGallery = false
                        tempImported = false
                        tempOffline = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = {
                        onApply(
                            FilterState(
                                sortOption = tempSort,
                                favoritesOnly = tempFavorites,
                                cameraOnly = tempCamera,
                                galleryOnly = tempGallery,
                                importedOnly = tempImported,
                                offlineOnly = tempOffline
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun FilterToggleRow(
    label: String,
    checked: Boolean,
    textColor: Color = Color(0xFF1E293B),
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = textColor)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
