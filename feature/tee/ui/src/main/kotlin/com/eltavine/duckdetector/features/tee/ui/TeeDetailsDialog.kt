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

package com.eltavine.duckdetector.features.tee.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Details
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TeeDetailsDialog(
    show: Boolean,
    exportText: String,
    certificateCount: Int,
    onDismiss: () -> Unit,
) {
    val lineCount = remember(exportText) {
        exportText.lineSequence().count { it.isNotBlank() }
    }

    TeeDialogFrame(
        show = show,
        title = "TEE details",
        subtitle = "Structured export for debugging, sharing, and manual verification.",
        icon = Icons.Rounded.Details,
        onDismiss = onDismiss,
        heroContent = {
            TeeDialogSurface(tone = TeeDialogTone.High) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    WrapSafeText(
                        text = "Current attestation snapshot",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    WrapSafeText(
                        text = "Review the current attestation export for debugging, evidence sharing, and manual verification.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TeeDialogMetricChip(
                            icon = Icons.Rounded.Details,
                            label = "Lines",
                            value = lineCount.toString(),
                        )
                        TeeDialogMetricChip(
                            icon = Icons.Rounded.VerifiedUser,
                            label = "Certificates",
                            value = certificateCount.toString(),
                        )
                    }
                }
            }
        },
    ) {
        if (exportText.isBlank()) {
            TeeDialogSurface(tone = TeeDialogTone.Low) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    WrapSafeText(
                        text = "No export available",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    WrapSafeText(
                        text = "Run the detector again after a full attestation pass to populate the raw TEE report.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            TeeDialogSurface(tone = TeeDialogTone.Low) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TeeDialogSurface(tone = TeeDialogTone.Highest, cornerRadius = 12.dp) {
                            Icon(
                                imageVector = Icons.Rounded.Details,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(18.dp),
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            WrapSafeText(
                                text = "Normalized report body",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            WrapSafeText(
                                text = "Optimized for inspection and copy-paste.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    TeeDialogSurface(tone = TeeDialogTone.High, cornerRadius = 12.dp) {
                        SelectionContainer {
                            WrapSafeText(
                                text = exportText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

internal enum class TeeDialogTone { Low, High, Highest }

/** Match the original Material surfaces in Material mode; use MIUIX squircle cards in MIUIX mode. */
@Composable
internal fun TeeDialogSurface(
    tone: TeeDialogTone,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        val scheme = MiuixTheme.colorScheme
        MiuixCard(
            modifier = modifier,
            cornerRadius = cornerRadius,
            insideMargin = PaddingValues(0.dp),
            colors = MiuixCardDefaults.defaultColors(
                color = when (tone) {
                    TeeDialogTone.Low -> scheme.surfaceContainer
                    TeeDialogTone.High -> scheme.surfaceContainerHigh
                    TeeDialogTone.Highest -> scheme.surfaceContainerHighest
                },
            ),
        ) { content() }
    } else {
        Surface(
            modifier = modifier,
            color = when (tone) {
                TeeDialogTone.Low -> MaterialTheme.colorScheme.surfaceContainerLow
                TeeDialogTone.High -> MaterialTheme.colorScheme.surfaceContainerHigh
                TeeDialogTone.Highest -> MaterialTheme.colorScheme.surfaceContainerHighest
            },
            shape = when {
                cornerRadius == 16.dp -> ShapeTokens.CornerExtraLarge
                tone == TeeDialogTone.Highest -> ShapeTokens.CornerLarge
                else -> ShapeTokens.CornerLargeIncreased
            },
            content = content,
        )
    }
}

@Composable
internal fun TeeDialogFrame(
    show: Boolean,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onDismiss: () -> Unit,
    heroContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        WindowDialog(
            show = show,
            title = title,
            summary = subtitle,
            onDismissRequest = onDismiss,
            maxWidth = 560.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                heroContent?.invoke()
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    content = content,
                )
                MiuixTextButton(
                    text = stringResource(R.string.tee_dialog_close),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
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
                            imageVector = icon,
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
                            text = title,
                            style = MaterialTheme.typography.titleLargeEmphasized,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        WrapSafeText(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                heroContent?.invoke()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    content = content,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        WrapSafeText(
                            text = stringResource(R.string.tee_dialog_close),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun TeeDialogMetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    TeeDialogSurface(tone = TeeDialogTone.Highest, modifier = modifier, cornerRadius = 12.dp) {
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
