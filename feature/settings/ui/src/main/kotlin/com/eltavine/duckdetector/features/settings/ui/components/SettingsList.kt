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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.adaptive.SettingsGroupHeader
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** Align M3E segmented items with KSU / InstallerX's native 16dp row inset. */
private val SettingsItemInset = 16.dp

private val SettingsItemPadding = PaddingValues(horizontal = SettingsItemInset, vertical = 14.dp)

@Composable
internal fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    badge: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (LocalUiMode.current == UiMode.Miuix) 2.dp else 10.dp),
    ) {
        if (LocalUiMode.current == UiMode.Miuix) {
            // The counter must not switch this header back to the Material title row.
            // SmallTitle supplies the same 28dp/8dp inset as About and leaves room
            // below the heading before the contributor wall starts.
            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingsGroupHeader(text = title)
                if (badge != null) {
                    WrapSafeText(
                        text = badge,
                        modifier = Modifier
                            .background(color = MiuixTheme.colorScheme.surfaceContainer, shape = ShapeTokens.CornerFull)
                            .padding(horizontal = 8.dp, vertical = 1.dp),
                        style = MiuixTheme.textStyles.footnote1.copy(fontFeatureSettings = "tnum"),
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        } else Row(
            modifier = Modifier.padding(horizontal = SettingsItemInset),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WrapSafeText(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            if (badge != null) {
                WrapSafeText(
                    text = badge,
                    modifier = Modifier
                        .background(color = DuckTheme.palette.groupedSurface, shape = ShapeTokens.CornerFull)
                        .padding(horizontal = 8.dp, vertical = 1.dp),
                    style = DuckTypography.Caption.copy(fontFeatureSettings = "tnum"),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        content()
    }
}

/** Rows that read as one block: separated by the segmented gap and shaped by [settingsItemShapes]. */
@Composable
internal fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    if (LocalUiMode.current == UiMode.Miuix) {
        // One native container for the whole section, with full-bleed preference rows.
        MiuixCard(modifier = Modifier.fillMaxWidth(), insideMargin = PaddingValues(0.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), content = content)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            content = content,
        )
    }
}

@Composable
internal fun SettingsItem(
    headline: String,
    shapes: ListItemShapes,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: ListItemColors = settingsItemColors(),
    leadingContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        BasicComponent(
            modifier = modifier,
            startAction = leadingContent,
            endActions = trailingContent?.let { trailing -> { trailing() } },
            onClick = onClick,
            enabled = enabled,
            insideMargin = SettingsItemPadding,
        ) {
            MiuixText(
                text = headline,
                style = MiuixTheme.textStyles.headline1,
                color = MiuixTheme.colorScheme.onSurface,
            )
            CompositionLocalProvider(
                LocalTextStyle provides MiuixTheme.textStyles.body2,
                LocalContentColor provides MiuixTheme.colorScheme.onSurfaceVariantSummary,
            ) {
                supportingContent?.invoke()
            }
        }
        return
    }
    // Native trailingContent reserves its width for the *entire* text column,
    // including supporting text. Controls in the headline row let subtitles
    // extend underneath the switch/dropdown and place the control too high.
    val headlineContent: @Composable () -> Unit = {
        WrapSafeText(text = headline, style = MaterialTheme.typography.bodyLargeEmphasized)
    }
    val materialSupporting: (@Composable () -> Unit)? = supportingContent?.let { supporting ->
        {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            ) { supporting() }
        }
    }
    if (onClick == null) {
        SegmentedListItem(
            shapes = shapes,
            modifier = modifier,
            enabled = enabled,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            verticalAlignment = Alignment.CenterVertically,
            supportingContent = materialSupporting,
            colors = colors,
            contentPadding = SettingsItemPadding,
            content = headlineContent,
        )
    } else {
        SegmentedListItem(
            onClick = onClick,
            shapes = shapes,
            modifier = modifier,
            enabled = enabled,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            verticalAlignment = Alignment.CenterVertically,
            supportingContent = materialSupporting,
            colors = colors,
            contentPadding = SettingsItemPadding,
            content = headlineContent,
        )
    }
}

/** Disabled rows keep their colors: a row is disabled only while it is busy, not unavailable. */
@Composable
internal fun settingsItemColors(
    containerColor: Color = DuckTheme.palette.groupedSurface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    supportingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
): ListItemColors = ListItemDefaults.segmentedColors(
    containerColor = containerColor,
    contentColor = contentColor,
    leadingContentColor = contentColor,
    trailingContentColor = supportingColor,
    supportingContentColor = supportingColor,
    disabledContainerColor = containerColor,
    disabledContentColor = contentColor,
    disabledLeadingContentColor = contentColor,
    disabledTrailingContentColor = supportingColor,
    disabledSupportingContentColor = supportingColor,
)

@Composable
internal fun SettingsIconTile(
    icon: ImageVector,
    tint: Color = if (LocalUiMode.current == UiMode.Miuix) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
) {
    SettingsIconTile {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
    }
}

/** About-section glyphs follow the MIUIX color scheme, independently of status-label colors. */
@Composable
internal fun AboutLeadingIcon(icon: ImageVector) {
    SettingsIconTile(
        icon = icon,
        tint = if (LocalUiMode.current == UiMode.Miuix) aboutMiuixIconColor() else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
internal fun aboutMiuixIconColor(): Color =
    if (MiuixTheme.colorScheme.background.luminance() < 0.5f) Color.White else Color.Black

@Composable
internal fun SettingsIconTile(
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier.size(28.dp),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
internal fun SettingsFootnote(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SettingsItemInset)
            .padding(top = if (LocalUiMode.current == UiMode.Miuix) 6.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 1.dp)
                    .size(18.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (title != null) {
                WrapSafeText(
                    text = title,
                    style = if (LocalUiMode.current == UiMode.Miuix) MiuixTheme.textStyles.footnote1 else DuckTypography.FootnoteEmphasized,
                    color = if (LocalUiMode.current == UiMode.Miuix) MiuixTheme.colorScheme.onSurfaceVariantSummary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            WrapSafeText(
                text = text,
                style = if (LocalUiMode.current == UiMode.Miuix) MiuixTheme.textStyles.footnote2 else DuckTypography.Footnote,
                color = if (LocalUiMode.current == UiMode.Miuix) MiuixTheme.colorScheme.onSurfaceVariantSummary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
