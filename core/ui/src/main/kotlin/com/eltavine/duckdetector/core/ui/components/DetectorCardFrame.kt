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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.components.DuckPanel
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.designsystem.theme.miuixInsetSurfaceColor
import com.eltavine.duckdetector.core.designsystem.theme.MotionTokens
import com.eltavine.duckdetector.core.evidence.DetectorStatus
import com.eltavine.duckdetector.core.ui.R
import com.eltavine.duckdetector.core.ui.presentation.StatusAppearance
import com.eltavine.duckdetector.core.ui.presentation.rememberStatusAppearance
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.anim.folmeSpring
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.ExpandLess
import top.yukonga.miuix.kmp.icon.extended.ExpandMore
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleClip
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * A detector's card. Collapsed, it shows only what a reader scanning the dashboard needs: the
 * detector, its status and the verdict. Expanding it adds the [subtitle] describing what was
 * checked, the [headerFacts], the [summary], the [content] and the [footerActions].
 */
@Composable
public fun DetectorCardFrame(
    title: String,
    subtitle: String,
    status: DetectorStatus,
    verdict: String,
    summary: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    leadingBadgeIcon: ImageVector? = null,
    leadingBadgeStatus: DetectorStatus? = null,
    leadingBadgeContentDescription: String? = null,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    headerFacts: @Composable ColumnScope.() -> Unit = {},
    collapsedOverview: @Composable ColumnScope.() -> Unit = {},
    footerActions: @Composable ColumnScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val appearance = rememberStatusAppearance(status)
    val leadingBadgeAppearance = rememberStatusAppearance(leadingBadgeStatus ?: status)
    var internalExpanded by rememberSaveable(title) { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val toggleDescription = stringResource(
        if (isExpanded) R.string.card_collapse else R.string.card_expand,
    )
    val haptics = LocalHapticFeedback.current
    val headerInteraction = remember { MutableInteractionSource() }
    val miuix = LocalUiMode.current == UiMode.Miuix
    // M3 has a stable card outline. Its touch feedback is the native ripple only;
    // a pressed-corner morph felt delayed and changed the outline again on collapse.
    val chevronRotation = if (miuix) 0f else {
        val animated by animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(durationMillis = 180),
            label = "cardChevron",
        )
        animated
    }
    val toggleExpandedAction: () -> Unit = {
        val next = !isExpanded
        if (expanded == null) internalExpanded = next
        onExpandedChange?.invoke(next)
    }
    val toggleExpanded: () -> Unit = {
        if (miuix) {
            haptics.performHapticFeedback(
                if (!isExpanded) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
            )
        }
        toggleExpandedAction()
    }

    val cardContent: @Composable ColumnScope.() -> Unit = {
        // Collapsed: the native Card owns the entire hit area and indication, including its
        // padding and collapsed overview. Expanded: only the flush-to-edge header can collapse.
        // Do not clip the inset glyph row: its small badge deliberately extends past the tile.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .indication(headerInteraction, LocalIndication.current)
                .then(
                    if (!isExpanded) Modifier else if (miuix) {
                        Modifier
                            .squircleClip(16.dp)
                            .clickable(
                                interactionSource = headerInteraction,
                                indication = null,
                                role = Role.Button,
                                onClickLabel = toggleDescription,
                                onClick = toggleExpanded,
                            )
                    } else {
                        Modifier
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .clickable(
                                interactionSource = headerInteraction,
                                indication = null,
                                role = Role.Button,
                                onClickLabel = toggleDescription,
                                onClick = toggleExpanded,
                            )
                    },
                )
                .padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = if (miuix) 18.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CardGlyph(
                    icon = leadingIcon,
                    appearance = appearance,
                    badgeIcon = leadingBadgeIcon,
                    badgeAppearance = leadingBadgeAppearance,
                    badgeContentDescription = leadingBadgeContentDescription,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    WrapSafeText(
                        text = title,
                        style = DuckTypography.PanelTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (LocalUiMode.current == UiMode.Miuix) {
                    CompactStatusBadge(status = status)
                    MiuixIcon(
                        imageVector = if (isExpanded) MiuixIcons.ExpandLess else MiuixIcons.ExpandMore,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    CompactStatusBadge(status = status)
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp).rotate(chevronRotation),
                    )
                }
            }

            WrapSafeText(
                text = verdict,
                modifier = Modifier.fillMaxWidth(),
                style = DuckTypography.PanelBody.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (miuix && !isExpanded) collapsedOverview()
        }

        if (miuix) AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = folmeSpring(
                    damping = 1.0f,
                    response = 0.36f,
                    visibilityThreshold = IntSize.VisibilityThreshold,
                ),
                expandFrom = Alignment.Top,
            ) + fadeIn(animationSpec = folmeSpring(damping = 1.0f, response = 0.30f)),
            exit = shrinkVertically(
                animationSpec = folmeSpring(
                    damping = 1.0f,
                    response = 0.30f,
                    visibilityThreshold = IntSize.VisibilityThreshold,
                ),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(animationSpec = folmeSpring(damping = 1.0f, response = 0.23f)),
        ) {
            // Section rows stay folded by default, so the outer height transition measures
            // short summaries, not all of TEE's attestation values at once.
            Column(
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (subtitle.isNotBlank()) {
                    WrapSafeText(
                        text = subtitle,
                        modifier = Modifier.fillMaxWidth(),
                        style = DuckTypography.PanelSupporting,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                headerFacts()
                if (summary.isNotBlank()) {
                    WrapSafeText(
                        text = summary,
                        modifier = Modifier.fillMaxWidth(),
                        style = DuckTypography.PanelSupporting,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .squircleBackground(miuixInsetSurfaceColor(), 16.dp)
                        .squircleClip(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    content = content,
                )
                footerActions()
            }
        }

        // One state/size transition for the mutually exclusive Material overview and details.
        // Two independent AnimatedVisibility size animations cause the overview to release its
        // height while the details are still growing, producing a visible two-stage jump.
        if (!miuix) AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                (fadeIn(MotionTokens.FadeInOut) togetherWith fadeOut(MotionTokens.FadeInOut))
                    .using(
                        SizeTransform(clip = true) { _, _ ->
                            MotionTokens.smoothSpring(IntSize.VisibilityThreshold)
                        },
                    )
            },
            label = "materialDetectorBody",
        ) {
            if (it) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (subtitle.isNotBlank()) {
                        WrapSafeText(
                            text = subtitle,
                            modifier = Modifier.fillMaxWidth(),
                            style = DuckTypography.PanelSupporting,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    headerFacts()
                    if (summary.isNotBlank()) {
                        WrapSafeText(
                            text = summary,
                            modifier = Modifier.fillMaxWidth(),
                            style = DuckTypography.PanelSupporting,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    content()
                    footerActions()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 18.dp),
                    content = collapsedOverview,
                )
            }
        }

    }

    DuckPanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        onClick = if (isExpanded) null else toggleExpanded,
        materialShape = MaterialTheme.shapes.large,
        content = cardContent,
    )
}

@Composable
private fun CardGlyph(
    icon: ImageVector,
    appearance: StatusAppearance,
    badgeIcon: ImageVector?,
    badgeAppearance: StatusAppearance,
    badgeContentDescription: String?,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .then(
                if (LocalUiMode.current == UiMode.Miuix) {
                    Modifier.squircleBackground(miuixInsetSurfaceColor(), 14.dp)
                } else {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.medium,
                    )
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (LocalUiMode.current == UiMode.Miuix) {
            MiuixIcon(imageVector = icon, contentDescription = null, tint = appearance.iconTint)
        } else {
            Icon(imageVector = icon, contentDescription = null, tint = appearance.iconTint)
        }
        if (badgeIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 5.dp, y = 5.dp)
                    .size(20.dp)
                    .background(color = DuckTheme.palette.groupedSurface, shape = CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = badgeContentDescription,
                    tint = badgeAppearance.iconTint,
                )
            }
        }
    }
}
