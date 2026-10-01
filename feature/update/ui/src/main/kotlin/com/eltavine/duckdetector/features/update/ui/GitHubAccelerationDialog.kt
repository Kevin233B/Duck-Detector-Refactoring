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

package com.eltavine.duckdetector.features.update.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eltavine.duckdetector.core.designsystem.theme.ShapeTokens
import com.eltavine.duckdetector.core.ui.components.WrapSafeText
import io.github.xiaotong6666.uihelper.mode.LocalUiMode
import io.github.xiaotong6666.uihelper.mode.UiMode
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.window.WindowDialog

/** [onDecline] is the user's answer; [onDismiss] only closes the dialog and leaves the choice open. */
@Composable
fun GitHubAccelerationDialog(
    onEnable: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (LocalUiMode.current == UiMode.Miuix) {
        GitHubAccelerationDialogMiuix(
            onEnable = onEnable,
            onDecline = onDecline,
            onDismiss = onDismiss,
        )
    } else {
        GitHubAccelerationDialogMaterial(
            onEnable = onEnable,
            onDecline = onDecline,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun GitHubAccelerationDialogMaterial(
    onEnable: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = ShapeTokens.CornerExtraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(imageVector = Icons.Rounded.Speed, contentDescription = null)
        },
        title = {
            WrapSafeText(text = stringResource(R.string.github_acceleration_dialog_title))
        },
        text = {
            WrapSafeText(text = stringResource(R.string.github_acceleration_dialog_message))
        },
        confirmButton = {
            Button(onClick = onEnable) {
                WrapSafeText(text = stringResource(R.string.github_acceleration_dialog_enable))
            }
        },
        dismissButton = {
            TextButton(onClick = onDecline) {
                WrapSafeText(text = stringResource(R.string.github_acceleration_dialog_decline))
            }
        },
    )
}

@Composable
private fun GitHubAccelerationDialogMiuix(
    onEnable: () -> Unit,
    onDecline: () -> Unit,
    onDismiss: () -> Unit,
) {
    var show by remember { mutableStateOf(true) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val dismissWith: (() -> Unit) -> Unit = { action ->
        pendingAction = action
        show = false
    }

    WindowDialog(
        show = show,
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
        title = stringResource(R.string.github_acceleration_dialog_title),
        summary = stringResource(R.string.github_acceleration_dialog_message),
        onDismissRequest = { dismissWith(onDismiss) },
        onDismissFinished = {
            pendingAction?.invoke()
            pendingAction = null
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MiuixTextButton(
                text = stringResource(R.string.github_acceleration_dialog_decline),
                onClick = { dismissWith(onDecline) },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(16.dp))
            MiuixTextButton(
                text = stringResource(R.string.github_acceleration_dialog_enable),
                onClick = { dismissWith(onEnable) },
                modifier = Modifier.weight(1f),
                colors = MiuixButtonDefaults.textButtonColorsPrimary(),
            )
        }
    }
}
