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

package com.eltavine.duckdetector.features.memory.detector

import android.content.Context
import android.webkit.WebView
import com.eltavine.duckdetector.core.detector.Detector
import com.eltavine.duckdetector.core.detector.DetectorScanner
import com.eltavine.duckdetector.core.detector.DetectorSpecificApi
import com.eltavine.duckdetector.core.evidence.DetectorId
import com.eltavine.duckdetector.core.report.DetectorReport
import com.eltavine.duckdetector.features.memory.data.repository.MemoryRepository
import com.eltavine.duckdetector.features.memory.domain.MemoryReport
import com.eltavine.duckdetector.features.memory.presentation.MemoryCardModelMapper
import com.eltavine.duckdetector.features.memory.presentation.model.MemoryCardModel
import com.eltavine.duckdetector.features.memory.presentation.toDetectorReport

/**
 * Memory: inspects this process natively for function hooks, memory map anomalies, file
 * descriptors, signal handlers, and vDSO and dynamic linker tampering.
 *
 * Collected by [MemoryRepository], judged by `MemoryReport.toDetectorStatus()` in the domain
 * layer, and described by [MemoryCardModelMapper].
 */
@DetectorSpecificApi
public object MemoryDetector : Detector<MemoryReport, MemoryCardModel> {
    override val id: DetectorId = DetectorId("memory")

    /**
     * The exact installed APK path of the current WebView provider, or nothing when the device
     * has no provider or package resolution fails. minSdk 29 always has
     * [WebView.getCurrentWebViewPackage].
     */
    private fun resolveWebViewProviderCodePaths(context: Context): List<String> {
        val providerPackageName = WebView.getCurrentWebViewPackage()?.packageName
            ?: return emptyList()
        return runCatching {
            listOf(context.packageManager.getApplicationInfo(providerPackageName, 0).sourceDir)
        }.getOrDefault(emptyList())
    }

    /**
     * The installed WebView provider's APK loads its native libraries uncompressed directly from
     * the APK (frameworks/base core/java/android/webkit/WebViewFactory behind
     * android:extractNativeLibs="false"), so the Bionic linker's relocations turn private
     * copy-on-write pages into anonymous pages on an executable system-path mapping in every
     * WebView client process, this one included. That artifact is loader behavior, not
     * injection, so its exact installed path is handed to the repository as a known benign
     * code container, like the repository already treats ART's code caches.
     */
    override fun createScanner(context: Context): DetectorScanner<MemoryReport> =
        MemoryRepository(benignCodePaths = resolveWebViewProviderCodePaths(context))

    override fun loadingReport(): MemoryReport = MemoryReport.loading()

    override fun describe(report: MemoryReport): MemoryCardModel = MemoryCardModelMapper().map(report)

    override fun export(model: MemoryCardModel): DetectorReport = model.toDetectorReport()
}
