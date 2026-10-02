/*
 * Copyright 2026 Duck Apps Contributor
 * If you have any questions, suggestions, or other inquiries, please email Eltavine <me@eltavine.com>.
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
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.designsystem.theme.MotionTokens
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.evidence.DetectorStatus
import com.eltavine.duckdetector.core.ui.R
import com.eltavine.duckdetector.core.ui.presentation.StatusAppearance
import com.eltavine.duckdetector.core.ui.presentation.rememberStatusAppearance

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
    val pressed by headerInteraction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) MotionTokens.PressedScale else 1f,
        animationSpec = MotionTokens.PressScale,
        label = "cardPress",
    )
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = MotionTokens.smoothSpring(),
        label = "cardChevron",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                // A tall expanded card pivots on its top edge so its header does not jump.
                transformOrigin = TransformOrigin(0.5f, 0f)
            }
            .background(
                color = DuckTheme.palette.groupedSurface,
                shape = ShapeTokens.CornerExtraLargeIncreased,
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = headerInteraction,
                    indication = null,
                    role = Role.Button,
                    onClickLabel = toggleDescription,
                ) {
                    val next = !isExpanded
                    haptics.performHapticFeedback(
                        if (next) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                    )
                    if (expanded == null) {
                        internalExpanded = next
                    }
                    onExpandedChange?.invoke(next)
                },
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        style = DuckTypography.Headline,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    CompactStatusBadge(status = status)
                }
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation),
                )
            }

            WrapSafeText(
                text = verdict,
                modifier = Modifier.fillMaxWidth(),
                style = DuckTypography.Body.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = MotionTokens.smoothSpring(IntSize.VisibilityThreshold),
                expandFrom = Alignment.Top,
            ) + fadeIn(MotionTokens.FadeInOut),
            exit = shrinkVertically(
                animationSpec = MotionTokens.smoothSpring(IntSize.VisibilityThreshold),
                shrinkTowards = Alignment.Top,
            ) + fadeOut(MotionTokens.FadeInOut),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (subtitle.isNotBlank()) {
                    WrapSafeText(
                        text = subtitle,
                        modifier = Modifier.fillMaxWidth(),
                        style = DuckTypography.Footnote,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                headerFacts()
                if (summary.isNotBlank()) {
                    WrapSafeText(
                        text = summary,
                        modifier = Modifier.fillMaxWidth(),
                        style = DuckTypography.Callout,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                content()
                footerActions()
            }
        }

        if (!isExpanded) {
            collapsedOverview()
        }
    }
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
            .background(color = appearance.tintWash, shape = ShapeTokens.CornerMedium),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = appearance.iconTint,
        )
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
