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

package com.eltavine.duckdetector.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Inset tiles inside a MIUIX surface card. The stock light scheme's secondaryVariant is
 * #F0F0F0; retain the previous dark surface and dynamic-color roles rather than forcing
 * the much brighter dark secondaryVariant (#434343) into existing cards.
 */
@Composable
public fun miuixInsetSurfaceColor(): Color {
    val colors = MiuixTheme.colorScheme
    return if (MiuixTheme.isDynamicColor || colors.background.luminance() < 0.5f) {
        colors.surfaceContainerHighest
    } else {
        colors.secondaryVariant
    }
}
