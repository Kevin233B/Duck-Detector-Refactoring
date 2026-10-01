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

package com.eltavine.duckdetector.features.settings.ui.licenses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.common.StatusTag
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.util.author
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.utils.PressFeedbackType

private const val DESCRIPTION_MAX_LINES = 2

@Composable
internal fun LazyItemScope.LicenseLibraryRow(
    library: Library,
    onClick: () -> Unit,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        MiuixCard(
            modifier = Modifier.animateItem().fillMaxWidth(),
            cornerRadius = 16.dp,
            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            pressFeedbackType = PressFeedbackType.None,
            showIndication = true,
            onClick = onClick,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        MiuixText(
                            text = library.name,
                            style = MiuixTheme.textStyles.headline1.copy(hyphens = Hyphens.None),
                            color = MiuixTheme.colorScheme.onSurface,
                        )
                        library.author.takeIf { it.isNotBlank() }?.let { author ->
                            MiuixText(
                                text = author,
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                    }
                    library.artifactVersion?.takeIf { it.isNotBlank() }?.let { version ->
                        LicensePill(text = version, version = true)
                    }
                }
                library.description?.takeIf { it.isNotBlank() }?.let { description ->
                    MiuixText(
                        text = description,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = DESCRIPTION_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (library.licenses.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        library.licenses.forEach { license -> LicensePill(license.name) }
                    }
                }
            }
        }
        return
    }
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .animateItem()
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(containerColor = DuckTheme.palette.groupedSurface),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    // A library's name is a proper name: it may wrap between words but is never hyphenated.
                    Text(
                        text = library.name,
                        style = DuckTypography.Headline.copy(hyphens = Hyphens.None),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    library.author
                        .takeIf { it.isNotBlank() }
                        ?.let { author ->
                            WrapSafeText(
                                text = author,
                                style = DuckTypography.Footnote,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                }

                library.artifactVersion
                    ?.takeIf { it.isNotBlank() }
                    ?.let { version -> LicensePill(text = version) }
            }

            library.description
                ?.takeIf { it.isNotBlank() }
                ?.let { description ->
                    WrapSafeText(
                        text = description,
                        style = DuckTypography.Footnote,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = DESCRIPTION_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

            if (library.licenses.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    library.licenses.forEach { license ->
                        LicensePill(text = license.name)
                    }
                }
            }
        }
    }
}

/** A short label, such as a version or a license name, in a capsule on the inset fill. */
@Composable
internal fun LicensePill(
    text: String,
    modifier: Modifier = Modifier,
    version: Boolean = false,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        if (version) {
            // Match Duck's High/Warning status tag, rather than MIUIX Badge's oval shape.
            StatusTag(
                label = text,
                modifier = modifier,
                backgroundColor = MiuixTheme.colorScheme.surfaceContainerHighest,
                contentColor = MiuixTheme.colorScheme.onSurface,
            )
        } else {
            MiuixText(
                text = text,
                modifier = modifier
                    .squircleBackground(MiuixTheme.colorScheme.surfaceContainerHighest, 6.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurface,
            )
        }
        return
    }
    WrapSafeText(
        text = text,
        modifier = modifier
            .background(color = DuckTheme.palette.groupedInset, shape = ShapeTokens.CornerFull)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        style = DuckTypography.Caption,
        color = MaterialTheme.colorScheme.onSurface,
    )
}
