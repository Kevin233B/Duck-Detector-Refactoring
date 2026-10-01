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

package com.eltavine.duckdetector.features.update.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import com.eltavine.duckdetector.features.update.domain.AvailableNightlyUpdate
import com.eltavine.duckdetector.features.update.ui.R
import io.github.xiaotong6666.uihelper.dialog.UpdatePromptChange
import io.github.xiaotong6666.uihelper.dialog.UpdatePromptDialogMiuix
import io.github.xiaotong6666.uihelper.dialog.UpdatePromptMetadata
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

@Composable
fun NightlyUpdateDialog(
    show: Boolean,
    currentVersionName: String,
    update: AvailableNightlyUpdate,
    downloadEnabled: Boolean,
    onDismiss: () -> Unit,
    onViewChanges: () -> Unit,
    onDownload: () -> Unit,
    onDismissFinished: () -> Unit = {},
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        NightlyUpdateDialogMiuix(
            show = show,
            currentVersionName = currentVersionName,
            update = update,
            downloadEnabled = downloadEnabled,
            onDismiss = onDismiss,
            onViewChanges = onViewChanges,
            onDownload = onDownload,
            onDismissFinished = onDismissFinished,
        )
        return
    }
    if (!show) return
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .heightIn(max = 680.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SystemUpdate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            WrapSafeText(
                                text = stringResource(R.string.update_dialog_title),
                                style = MaterialTheme.typography.titleLargeEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            WrapSafeText(
                                text = stringResource(
                                    R.string.update_version_change,
                                    currentVersionName,
                                    update.manifest.versionName,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        UpdateMetadataRow(
                            icon = Icons.Rounded.Source,
                            label = stringResource(R.string.update_branch_hash_label),
                            value = "${update.manifest.branch} · ${update.manifest.commit.sha.take(8)}",
                            monospace = true,
                        )
                        UpdateMetadataRow(
                            icon = Icons.Rounded.AccountCircle,
                            label = stringResource(R.string.update_author_label),
                            value = update.manifest.commit.authorName,
                        )
                        UpdateMetadataRow(
                            icon = Icons.Rounded.Schedule,
                            label = stringResource(R.string.update_time_label),
                            value = formatUpdateTime(update.manifest.builtAtUtc),
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            WrapSafeText(
                                text = stringResource(R.string.update_changelog_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            update.changelog.forEach { commit ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 7.dp)
                                            .size(7.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = CircleShape,
                                            ),
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        WrapSafeText(
                                            text = commit.subject,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        WrapSafeText(
                                            text = commit.sha.take(8),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                            val remainingCommitCount = update.remainingCommitCount
                            val remainingCommits = when {
                                remainingCommitCount == null ->
                                    stringResource(R.string.update_remaining_commits_unknown)
                                remainingCommitCount > 0 -> pluralStringResource(
                                    R.plurals.update_remaining_commits,
                                    remainingCommitCount,
                                    remainingCommitCount,
                                )
                                else -> null
                            }
                            if (remainingCommits != null) {
                                WrapSafeText(
                                    text = remainingCommits,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    TextButton(
                        onClick = onViewChanges,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        WrapSafeText(
                            text = stringResource(R.string.update_view_changes),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                        ) {
                            WrapSafeText(
                                text = stringResource(R.string.update_later),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        Button(
                            onClick = onDownload,
                            enabled = downloadEnabled,
                            modifier = Modifier
                                .weight(1.5f)
                                .heightIn(min = 48.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            WrapSafeText(
                                text = stringResource(R.string.update_download),
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NightlyUpdateDialogMiuix(
    show: Boolean,
    currentVersionName: String,
    update: AvailableNightlyUpdate,
    downloadEnabled: Boolean,
    onDismiss: () -> Unit,
    onViewChanges: () -> Unit,
    onDownload: () -> Unit,
    onDismissFinished: () -> Unit,
) {
    UpdatePromptDialogMiuix(
        show = show,
        title = stringResource(R.string.update_dialog_title),
        summary = stringResource(
            R.string.update_version_change,
            currentVersionName,
            update.manifest.versionName,
        ),
        metadata = listOf(
            UpdatePromptMetadata(
                label = stringResource(R.string.update_branch_hash_label),
                value = "${update.manifest.branch} · ${update.manifest.commit.sha.take(8)}",
                icon = Icons.Rounded.Source,
                monospace = true,
            ),
            UpdatePromptMetadata(
                label = stringResource(R.string.update_author_label),
                value = update.manifest.commit.authorName,
                icon = Icons.Rounded.AccountCircle,
            ),
            UpdatePromptMetadata(
                label = stringResource(R.string.update_time_label),
                value = formatUpdateTime(update.manifest.builtAtUtc),
                icon = Icons.Rounded.Schedule,
            ),
        ),
        changesTitle = stringResource(R.string.update_changelog_title),
        changes = update.changelog.map { commit ->
            UpdatePromptChange(title = commit.subject, reference = commit.sha.take(8))
        },
        moreChangesLabel = when (val count = update.remainingCommitCount) {
            null -> stringResource(R.string.update_remaining_commits_unknown)
            in 1..Int.MAX_VALUE -> pluralStringResource(R.plurals.update_remaining_commits, count, count)
            else -> null
        },
        viewAllLabel = stringResource(R.string.update_view_changes),
        onViewAll = onViewChanges,
        dismissLabel = stringResource(R.string.update_later),
        confirmLabel = stringResource(R.string.update_download),
        confirmEnabled = downloadEnabled,
        onDismiss = onDismiss,
        onConfirm = onDownload,
        onDismissFinished = onDismissFinished,
    )
}
