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

package com.eltavine.duckdetector.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.theme.MotionTokens
import com.eltavine.duckdetector.core.ui.R
import io.github.xiaotong6666.uihelper.adaptive.rememberExpandableSectionState
import io.github.xiaotong6666.uihelper.adaptive.ExpandableSectionBody
import io.github.xiaotong6666.uihelper.common.StatusTag
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.HorizontalDivider as MiuixDivider
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore

/**
 * One expandable evidence group across both skins. MIUIX uses native folme/highlight;
 * Material uses a tonal inset and native clickable Surface / ripple.
 */
@Composable
public fun DetectorSectionFrame(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    severity: SectionSeverity? = null,
    stateKey: String? = null,
    showDivider: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val miuix = LocalUiMode.current == UiMode.Miuix
    // Positional identity by default. Dynamic lists can supply an immutable evidence ID.
    // A localized display title must never be the saved-state identity.
    val sectionState = rememberExpandableSectionState(identity = stateKey)
    val sectionExpanded = sectionState.expanded
    val toggleSection = { sectionState.toggle() }
    if (miuix) {
        val interactionSource = remember { MutableInteractionSource() }
        Column(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 46.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        role = Role.Button,
                    ) { toggleSection() }
                    .padding(horizontal = 16.dp, vertical = 9.dp)
                    .semantics(mergeDescendants = true) { heading() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MiuixIcon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MiuixTheme.colorScheme.primary,
                )
                MiuixText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MiuixTheme.textStyles.headline1,
                    color = MiuixTheme.colorScheme.onSurface,
                )
                if (severity != null) {
                    val accent = when (severity) {
                        SectionSeverity.HIGH -> MiuixTheme.colorScheme.error
                        SectionSeverity.MEDIUM -> DuckTheme.palette.caution
                        SectionSeverity.PROBE_ERROR -> DuckTheme.palette.critical
                    }
                    // A genuine translucent tint, unlike the opaque errorContainer. Keep the
                    // same compact square shape as the homepage High label.
                    StatusTag(
                        label = sectionSeverityLabel(severity),
                        backgroundColor = accent.copy(alpha = 0.16f),
                        contentColor = accent,
                    )
                }
                MiuixIcon(
                    imageVector = if (sectionExpanded) MiuixIcons.ExpandLess else MiuixIcons.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                )
            }
            ExpandableSectionBody(
                expanded = sectionExpanded,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                content = content,
            )
            if (showDivider) {
                MiuixDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        return
    }
    val arrowRotation by animateFloatAsState(
        targetValue = if (sectionExpanded) 180f else 0f,
        animationSpec = MotionTokens.IconRotation,
        label = "materialSectionChevron",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Surface(
            onClick = toggleSection,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = MaterialTheme.shapes.large,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .semantics(mergeDescendants = true) { heading() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                WrapSafeText(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (severity != null) {
                    MaterialSeverityTag(
                        status = severity.representativeStatus(),
                        label = sectionSeverityLabel(severity),
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).rotate(arrowRotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        AnimatedVisibility(
            visible = sectionExpanded,
            enter = expandVertically(
                animationSpec = MotionTokens.smoothSpring(IntSize.VisibilityThreshold),
            ) + fadeIn(MotionTokens.FadeInOut),
            exit = shrinkVertically(
                animationSpec = MotionTokens.smoothSpring(IntSize.VisibilityThreshold),
            ) + fadeOut(MotionTokens.FadeInOut),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                content = content,
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = DuckTheme.palette.separator,
            )
        }
    }
}

@Composable
private fun sectionSeverityLabel(severity: SectionSeverity): String = stringResource(
    when (severity) {
        SectionSeverity.HIGH -> R.string.severity_high
        SectionSeverity.MEDIUM -> R.string.severity_medium
        SectionSeverity.PROBE_ERROR -> R.string.status_info_error
    },
)
