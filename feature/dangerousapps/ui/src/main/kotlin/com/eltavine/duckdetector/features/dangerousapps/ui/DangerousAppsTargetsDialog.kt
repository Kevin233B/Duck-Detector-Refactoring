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

package com.eltavine.duckdetector.features.dangerousapps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import com.eltavine.duckdetector.features.dangerousapps.presentation.model.DangerousAppsTargetAppModel
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixDivider
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DangerousAppsTargetsDialog(
    show: Boolean,
    targets: List<DangerousAppsTargetAppModel>,
    onDismiss: () -> Unit,
) {
    val categoryCount = targets.map { it.category }.distinct().size

    if (LocalUiMode.current == UiMode.Miuix) {
        WindowDialog(
            show = show,
            title = stringResource(R.string.target_app_list_title),
            summary = stringResource(R.string.target_app_list_summary),
            onDismissRequest = onDismiss,
            maxWidth = 560.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DangerousAppsDialogMetricChip(Icons.Rounded.Apps, stringResource(R.string.target_app_list_targets), targets.size.toString())
                    DangerousAppsDialogMetricChip(Icons.Rounded.Category, stringResource(R.string.target_app_list_categories), categoryCount.toString())
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .squircleBackground(MiuixTheme.colorScheme.surfaceContainer, 16.dp)
                        .squircleClip(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    itemsIndexed(targets) { index, target ->
                        DangerousAppsTargetRow(target)
                        if (index < targets.lastIndex) MiuixDivider()
                    }
                }
                MiuixTextButton(text = stringResource(R.string.target_app_list_close), onClick = onDismiss, modifier = Modifier.fillMaxWidth())
            }
        }
        return
    }

    if (!show) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 720.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Apps,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(22.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        WrapSafeText(
                            text = stringResource(R.string.target_app_list_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        WrapSafeText(
                            text = stringResource(R.string.target_app_list_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DangerousAppsDialogMetricChip(
                        icon = Icons.Rounded.Apps,
                        label = stringResource(R.string.target_app_list_targets),
                        value = targets.size.toString(),
                    )
                    DangerousAppsDialogMetricChip(
                        icon = Icons.Rounded.Category,
                        label = stringResource(R.string.target_app_list_categories),
                        value = categoryCount.toString(),
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = ShapeTokens.CornerExtraLarge,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        itemsIndexed(targets) { index, target ->
                            DangerousAppsTargetRow(target = target)
                            if (index < targets.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f),
                                    thickness = 1.dp,
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        WrapSafeText(
                            text = stringResource(R.string.target_app_list_close),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DangerousAppsTargetRow(
    target: DangerousAppsTargetAppModel,
) {
    val miuix = LocalUiMode.current == UiMode.Miuix
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            WrapSafeText(
                text = target.appName,
                modifier = Modifier.weight(1f),
                style = if (miuix) MiuixTheme.textStyles.headline1 else MaterialTheme.typography.titleSmall,
                color = if (miuix) MiuixTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface,
            )
            if (miuix) Row(
                modifier = Modifier
                    .squircleBackground(MiuixTheme.colorScheme.surfaceContainerHighest, 6.dp)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.Shield, null, tint = MiuixTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                MiuixText(target.category, style = MiuixTheme.textStyles.footnote1)
            } else Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = ShapeTokens.CornerFull,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp),
                    )
                    WrapSafeText(
                        text = target.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        WrapSafeText(
            text = target.packageName,
            style = (if (miuix) MiuixTheme.textStyles.footnote1 else MaterialTheme.typography.bodySmall)
                .copy(fontFamily = FontFamily.Monospace),
            color = if (miuix) MiuixTheme.colorScheme.onSurfaceVariantSummary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DangerousAppsDialogMetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        MiuixCard(
            cornerRadius = 12.dp,
            insideMargin = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            colors = MiuixCardDefaults.defaultColors(color = MiuixTheme.colorScheme.surfaceContainerHighest),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, null, tint = MiuixTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    MiuixText(label, style = MiuixTheme.textStyles.footnote1)
                    MiuixText(value, style = MiuixTheme.textStyles.body2)
                }
            }
        }
        return
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = ShapeTokens.CornerLarge,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                WrapSafeText(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                WrapSafeText(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
