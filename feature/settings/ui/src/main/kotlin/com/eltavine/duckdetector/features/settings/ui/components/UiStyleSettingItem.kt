/*
 * Copyright 2026 Duck Apps Contributor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eltavine.duckdetector.features.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.xiaotong6666.uihelper.popup.OffsetAnchoredPopupMenu
import io.github.xiaotong6666.uihelper.popup.PopupMenuAlignment
import io.github.xiaotong6666.uihelper.popup.PopupMenuGroup
import io.github.xiaotong6666.uihelper.popup.PopupMenuItem
import io.github.xiaotong6666.uihelper.popup.trackPopupMenuPressPosition
import kotlin.math.roundToInt

/** KSU-style segmented settings row with an M3E menu; MIUIX keeps its native preference. */
@Composable
internal fun UiStyleSettingItem(
    title: String,
    description: String,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    if (items.isEmpty()) return
    var expanded by remember { mutableStateOf(false) }
    var anchorOffset by remember { mutableStateOf(IntOffset.Zero) }
    val haptics = LocalHapticFeedback.current
    val safeIndex = selectedIndex.coerceIn(0, items.lastIndex)
    Box(modifier = Modifier.trackPopupMenuPressPosition { position ->
        // Anchor to the top edge of the settings row. Tracking the press Y would push
        // the two-item popup over the next CRL setting when the trailing value is tapped.
        anchorOffset = IntOffset(position.x.roundToInt(), 0)
    }) {
        SettingsItem(
            headline = title,
            shapes = settingsItemShapes(index = 0, count = 1),
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.VirtualKey)
                expanded = true
            },
            leadingContent = { SettingsIconTile(Icons.Rounded.DisplaySettings) },
            supportingContent = { Text(description) },
            trailingContent = {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.small)
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        items[safeIndex],
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(
                        Icons.Rounded.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            },
        )
        OffsetAnchoredPopupMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            anchorOffset = anchorOffset,
            alignment = PopupMenuAlignment.TopEnd,
            groups = listOf(
                PopupMenuGroup(items.mapIndexed { index, label ->
                    PopupMenuItem(
                        label = label,
                        selected = index == safeIndex,
                        onClick = { onItemSelected(index) },
                    )
                }),
            ),
        )
    }
}
