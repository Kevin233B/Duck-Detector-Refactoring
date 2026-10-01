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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.features.settings.ui.R
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.chrome.DetailPageHost
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator as MiuixCircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OpenSourceLicensesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalResources.current
    val libraries by produceLibraries {
        AboutLibrariesJsonOverrides.apply(
            resources
                .openRawResource(R.raw.aboutlibraries)
                .bufferedReader()
                .use { it.readText() },
        )
    }
    val libraryCount = libraries?.libraries?.size
    val miuixLibraries = remember(libraries) { libraries?.libraries?.sortedBy { it.name.lowercase() }.orEmpty() }
    var selectedLibrary by remember { mutableStateOf<Library?>(null) }
    var presentedLibrary by remember { mutableStateOf<Library?>(null) }

    if (LocalUiMode.current == UiMode.Miuix) {
        DetailPageHost(
            title = stringResource(R.string.licenses_screen_title),
            subtitle = stringResource(R.string.licenses_screen_subtitle),
            onBack = onBack,
        ) { contentPadding, pageModifier ->
            Column(
                modifier = pageModifier.fillMaxSize().padding(contentPadding).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MiuixCard(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = PaddingValues(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MiuixIcon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            MiuixText(
                                text = stringResource(R.string.licenses_inventory_title),
                                style = MiuixTheme.textStyles.headline1,
                                color = MiuixTheme.colorScheme.onSurface,
                            )
                            MiuixText(
                                text = stringResource(R.string.licenses_inventory_subtitle),
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                        }
                        MiuixText(
                            text = libraryCount?.toString() ?: "…",
                            style = MiuixTheme.textStyles.headline1,
                            color = MiuixTheme.colorScheme.primary,
                        )
                    }
                }
                if (libraries == null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        MiuixCircularProgressIndicator()
                    }
                } else {
                    // AboutLibraries' M3 container paints an unshaped common surface underneath
                    // the custom rows. Native MIUIX list items must own their own squircle edge.
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(count = miuixLibraries.size) { index ->
                            val library = miuixLibraries[index]
                            LicenseLibraryRow(
                                library = library,
                                onClick = {
                                    presentedLibrary = library
                                    selectedLibrary = library
                                },
                            )
                        }
                    }
                }
            }
        }
        (selectedLibrary ?: presentedLibrary)?.let { library ->
            LicenseDetailsDialog(
                show = selectedLibrary != null,
                library = library,
                onDismiss = { selectedLibrary = null },
                onDismissFinished = {
                    if (selectedLibrary == null) presentedLibrary = null
                },
            )
        }
        return
    }

    // Same native M3E large collapsing bar as KSU/InstallerX; the app's DuckNavHost
    // remains the sole route owner, and DetailPageHost is only visual chrome.
    Box(modifier = modifier.fillMaxSize()) {
        DetailPageHost(
            title = stringResource(R.string.licenses_screen_title),
            subtitle = stringResource(R.string.licenses_screen_subtitle),
            onBack = onBack,
        ) { contentPadding, pageModifier ->
            Column(
                modifier = pageModifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp),
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            WrapSafeText(
                                text = stringResource(R.string.licenses_inventory_title),
                                style = MaterialTheme.typography.titleMediumEmphasized,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            WrapSafeText(
                                text = stringResource(R.string.licenses_inventory_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ) {
                            WrapSafeText(
                                text = libraryCount?.toString() ?: "…",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }

                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(vertical = 2.dp),
                    colors = LibraryDefaults.libraryColors(
                        libraryBackgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                        libraryContentColor = MaterialTheme.colorScheme.onSurface,
                        licenseChipColors = LibraryDefaults.chipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    ),
                    onLibraryClick = { library ->
                        selectedLibrary = library
                        true
                    },
                    libraryRow = { _, library, _, toggle, _ ->
                        LicenseLibraryRow(library = library, onClick = toggle)
                    },
                )
            }
        }
    }

    selectedLibrary?.let { library ->
        LicenseDetailsDialog(show = true, library = library, onDismiss = { selectedLibrary = null })
    }
}
