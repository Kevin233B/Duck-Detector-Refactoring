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

package com.eltavine.duckdetector.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.components.DuckPanel
import com.eltavine.duckdetector.core.designsystem.theme.DuckTypography
import com.eltavine.duckdetector.core.evidence.DetectionSeverity
import com.eltavine.duckdetector.core.evidence.DetectorStatus
import com.eltavine.duckdetector.core.ui.components.DetectorHairline
import com.eltavine.duckdetector.core.ui.components.MiuixStatusLabel
import com.eltavine.duckdetector.core.ui.components.MaterialSeverityTag
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import com.eltavine.duckdetector.core.ui.presentation.rememberStatusAppearance
import com.eltavine.duckdetector.features.dashboard.presentation.model.DashboardFindingModel
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode

private val FindingInset = 18.dp
private const val FINDING_DETAIL_MAX_LINES = 3

@Composable
internal fun DashboardFindingsCard(
    findings: List<DashboardFindingModel>,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (LocalUiMode.current == UiMode.Material) DashboardFindingsHeader(findings = findings)
        DuckPanel {
            findings.forEachIndexed { index, finding ->
                DashboardFindingRow(finding = finding)
                if (index < findings.lastIndex) {
                    DetectorHairline(startInset = FindingInset)
                }
            }
        }
    }
}

@Composable
private fun DashboardFindingsHeader(
    findings: List<DashboardFindingModel>,
) {
    val headerStatus = findings.firstOrNull()?.status ?: DetectorStatus.allClear()
    val appearance = rememberStatusAppearance(headerStatus)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = appearance.icon,
            contentDescription = null,
            tint = appearance.iconTint,
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) { heading() },
        ) {
            WrapSafeText(
                text = "Top findings",
                style = DuckTypography.Title3,
                color = MaterialTheme.colorScheme.onSurface,
            )
            WrapSafeText(
                text = "Priority review queue",
                style = DuckTypography.Footnote,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        WrapSafeText(
            text = findings.size.toString(),
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.small,
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLargeEmphasized.copy(fontFeatureSettings = "tnum"),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}

@Composable
private fun DashboardFindingRow(
    finding: DashboardFindingModel,
) {
    val appearance = rememberStatusAppearance(finding.status)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FindingInset, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = appearance.icon,
                contentDescription = null,
                tint = appearance.iconTint,
                modifier = Modifier.size(16.dp),
            )
            WrapSafeText(
                text = finding.detectorTitle,
                modifier = Modifier.weight(1f),
                style = if (LocalUiMode.current == UiMode.Miuix) DuckTypography.PanelCaption else DuckTypography.FootnoteEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (LocalUiMode.current == UiMode.Miuix) {
                MiuixStatusLabel(status = finding.status, label = findingSeverityLabel(finding))
            } else {
                MaterialSeverityTag(
                    status = finding.status,
                    label = findingSeverityLabel(finding),
                )
            }
        }
        WrapSafeText(
            text = finding.headline,
            modifier = Modifier.fillMaxWidth(),
            style = DuckTypography.PanelTitle,
            color = MaterialTheme.colorScheme.onSurface,
        )
        // The full evidence stays in the detector's card; a finding only points to it.
        WrapSafeText(
            text = finding.detail,
            modifier = Modifier.fillMaxWidth(),
            style = DuckTypography.PanelSupporting,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = FINDING_DETAIL_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun findingSeverityLabel(
    finding: DashboardFindingModel,
): String {
    return when (finding.status.severity) {
        DetectionSeverity.DANGER -> stringResource(R.string.dashboard_severity_high)
        DetectionSeverity.WARNING -> stringResource(R.string.dashboard_severity_medium)
        DetectionSeverity.INFO -> stringResource(R.string.dashboard_severity_check)
        DetectionSeverity.ALL_CLEAR -> stringResource(R.string.dashboard_severity_clear)
    }
}
