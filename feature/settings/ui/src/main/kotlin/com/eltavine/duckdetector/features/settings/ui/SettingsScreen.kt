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

package com.eltavine.duckdetector.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.components.StatusBarProtection
import com.eltavine.duckdetector.core.designsystem.theme.DuckTheme
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import com.eltavine.duckdetector.features.settings.presentation.model.SettingsUiState
import com.eltavine.duckdetector.features.settings.ui.components.AboutSection
import com.eltavine.duckdetector.features.settings.ui.components.ConsentSettingItem
import com.eltavine.duckdetector.features.settings.ui.components.ContributorNameWordmark
import com.eltavine.duckdetector.features.settings.ui.components.ContributorsSection
import com.eltavine.duckdetector.features.settings.ui.components.SettingsSection
import com.eltavine.duckdetector.features.settings.ui.components.SettingsGroup
import com.eltavine.duckdetector.features.settings.ui.components.UiStyleSettingItem
import com.eltavine.duckdetector.features.settings.ui.components.SettingsFootnote
import com.eltavine.duckdetector.features.settings.ui.components.SettingsSwitchItem
import io.github.xiaotong6666.uihelper.adaptive.SettingsDropdownItem
import io.github.xiaotong6666.uihelper.adaptive.SettingsGroup as AdaptiveSettingsGroup
import io.github.xiaotong6666.uihelper.adaptive.SettingsToggleItem
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    consentToggles: List<ConsentToggle>,
    onCheckForUpdates: () -> Unit,
    onGitHubAccelerationChange: (Boolean) -> Unit,
    onUiModeChange: (UiMode) -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
    pageModifier: Modifier = Modifier,
) {
    // License details live in the app's single MIUIX NavDisplay, not a second pager/AnimatedContent.
    if (LocalUiMode.current == UiMode.Miuix && scaffoldPadding != null) {
        SettingsMiuixPage(
            uiState = uiState,
            consentToggles = consentToggles,
            onCheckForUpdates = onCheckForUpdates,
            onGitHubAccelerationChange = onGitHubAccelerationChange,
            onUiModeChange = onUiModeChange,
            onOpenLicenses = onOpenLicenses,
            scaffoldPadding = scaffoldPadding,
            pageModifier = pageModifier,
        )
    } else {
        SettingsPage(
            uiState = uiState,
            consentToggles = consentToggles,
            onCheckForUpdates = onCheckForUpdates,
            onGitHubAccelerationChange = onGitHubAccelerationChange,
            onUiModeChange = onUiModeChange,
            onOpenLicenses = onOpenLicenses,
            modifier = modifier,
            scaffoldPadding = scaffoldPadding,
        )
    }
}

@Composable
private fun SettingsPage(
    uiState: SettingsUiState,
    consentToggles: List<ConsentToggle>,
    onCheckForUpdates: () -> Unit,
    onGitHubAccelerationChange: (Boolean) -> Unit,
    onUiModeChange: (UiMode) -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues? = null,
) {
    val uiMode = LocalUiMode.current
    val materialOverscrollEffect = rememberOverscrollEffect()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DuckTheme.palette.groupedBackground)
            .then(if (uiMode == UiMode.Material) Modifier.overscroll(materialOverscrollEffect) else Modifier),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (scaffoldPadding != null) Modifier.padding(scaffoldPadding) else Modifier.windowInsetsPadding(WindowInsets.safeDrawing))
                .verticalScroll(rememberScrollState(), overscrollEffect = if (uiMode == UiMode.Material) materialOverscrollEffect else null)
                .padding(horizontal = if (uiMode == UiMode.Miuix) 12.dp else 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                if (scaffoldPadding == null) {
                    WrapSafeText(
                        text = stringResource(R.string.settings_title),
                        modifier = Modifier.semantics { heading() },
                        style = if (uiMode == UiMode.Material) MaterialTheme.typography.headlineLarge
                            else DuckTypography.PageTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
                    val styleItems = listOf(
                        stringResource(R.string.settings_style_miuix),
                        stringResource(R.string.settings_style_material),
                    )
                    val onStyleSelected: (Int) -> Unit = { index ->
                        onUiModeChange(if (index == 0) UiMode.Miuix else UiMode.Material)
                    }
                    if (uiMode == UiMode.Miuix) {
                        AdaptiveSettingsGroup {
                            SettingsDropdownItem(
                                title = stringResource(R.string.settings_ui_style_title),
                                description = stringResource(R.string.settings_ui_style_description),
                                items = styleItems,
                                selectedIndex = 0,
                                icon = Icons.Rounded.DisplaySettings,
                                onItemSelected = onStyleSelected,
                            )
                        }
                    } else {
                        SettingsGroup {
                            UiStyleSettingItem(
                                title = stringResource(R.string.settings_ui_style_title),
                                description = stringResource(R.string.settings_ui_style_description),
                                items = styleItems,
                                selectedIndex = 1,
                                onItemSelected = onStyleSelected,
                            )
                        }
                    }
                }

                if (consentToggles.isNotEmpty()) {
                    SettingsSection(title = stringResource(R.string.settings_section_detection)) {
                        if (uiMode == UiMode.Miuix) {
                            consentToggles.forEach { toggle ->
                                AdaptiveSettingsGroup {
                                    SettingsToggleItem(
                                        checked = toggle.checked,
                                        title = stringResource(toggle.setting.title),
                                        description = stringResource(toggle.setting.summary),
                                        icon = toggle.setting.icon,
                                        onToggle = { toggle.onCheckedChange(!toggle.checked) },
                                    )
                                }
                                com.eltavine.duckdetector.features.settings.ui.components.SettingsFootnote(
                                    text = stringResource(toggle.setting.footer),
                                )
                            }
                        } else {
                            consentToggles.forEach { toggle -> ConsentSettingItem(toggle = toggle) }
                        }
                    }
                }

                SettingsSection(title = stringResource(R.string.settings_section_network)) {
                    if (uiMode == UiMode.Miuix) {
                        AdaptiveSettingsGroup {
                            SettingsToggleItem(
                                checked = uiState.gitHubAccelerationEnabled,
                                title = stringResource(R.string.github_acceleration_title),
                                description = stringResource(R.string.github_acceleration_summary),
                                icon = Icons.Rounded.Speed,
                                onToggle = { onGitHubAccelerationChange(!uiState.gitHubAccelerationEnabled) },
                            )
                        }
                        SettingsFootnote(text = stringResource(R.string.github_acceleration_footer))
                    } else {
                        SettingsSwitchItem(
                            headline = stringResource(R.string.github_acceleration_title),
                            summary = stringResource(R.string.github_acceleration_summary),
                            footer = stringResource(R.string.github_acceleration_footer),
                            icon = Icons.Rounded.Speed,
                            checked = uiState.gitHubAccelerationEnabled,
                            onCheckedChange = onGitHubAccelerationChange,
                        )
                    }
                }

                AboutSection(
                    uiState = uiState,
                    onCheckForUpdates = onCheckForUpdates,
                    onOpenLicenses = onOpenLicenses,
                )

                ContributorsSection()
                ContributorNameWordmark()
                Spacer(modifier = Modifier.height(if (scaffoldPadding == null) 96.dp else 20.dp))
            }
        }

        if (scaffoldPadding == null) {
            StatusBarProtection(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}
