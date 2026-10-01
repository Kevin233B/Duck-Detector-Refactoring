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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.evidence.DetectorStatus
import com.eltavine.duckdetector.core.ui.presentation.rememberStatusAppearance
import io.github.xiaotong6666.uihelper.adaptive.LabeledValueLayout
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
public fun DetectorDetailRowBlock(
    label: String,
    value: String,
    status: DetectorStatus,
    modifier: Modifier = Modifier,
    // 让上层在不改版式的前提下给 value 文本附加隐藏手势或语义。
    // Lets callers attach hidden gestures or semantics to the value text without changing the row layout.
    valueModifier: Modifier = Modifier,
    detail: String? = null,
    detailMonospace: Boolean = false,
    statusIcon: ImageVector? = null,
    verticalPadding: Dp = 12.dp,
) {
    val appearance = rememberStatusAppearance(status)

    if (LocalUiMode.current == UiMode.Miuix) {
        // Avoid maxIntrinsicWidth(Infinity) for diagnostic values. It forces every long
        // certificate / property line to be measured repeatedly during expansion and scroll.
        // Labels and values get separate full-width lines with a clear MIUIX type hierarchy.
        Column(
            modifier = modifier.fillMaxWidth().padding(vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            WrapSafeText(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MiuixIcon(
                    imageVector = statusIcon ?: appearance.icon,
                    contentDescription = null,
                    tint = appearance.iconTint,
                    modifier = Modifier.padding(top = 2.dp).size(16.dp),
                )
                WrapSafeText(
                    text = value,
                    modifier = Modifier.weight(1f).then(valueModifier),
                    style = MiuixTheme.textStyles.body1.copy(lineHeight = 22.sp),
                    color = MiuixTheme.colorScheme.onSurface,
                )
            }
            detail?.takeIf { it.isNotBlank() }?.let { raw ->
                WrapSafeText(
                    text = raw,
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp),
                    style = MiuixTheme.textStyles.body2.copy(
                        fontFamily = if (detailMonospace) FontFamily.Monospace else null,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                    ),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        LabeledValueLayout(
            label = {
                WrapSafeText(
                    text = label,
                    style = DuckTypography.Callout,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            value = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = statusIcon ?: appearance.icon,
                        contentDescription = null,
                        tint = appearance.iconTint,
                        modifier = Modifier.size(16.dp),
                    )
                    WrapSafeText(
                        text = value,
                        modifier = valueModifier,
                        style = DuckTypography.CalloutEmphasized,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )
        detail?.takeIf { it.isNotBlank() }?.let { resolvedDetail ->
            WrapSafeText(
                text = resolvedDetail,
                modifier = Modifier.fillMaxWidth(),
                style = DuckTypography.PanelSupporting.let { base ->
                    if (detailMonospace) base.copy(fontFamily = FontFamily.Monospace) else base
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
