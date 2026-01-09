package com.plantCare.plantcare.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.plantCare.plantcare.ui.components.InlinedText
import com.plantCare.plantcare.ui.components.SquareButton
import com.plantCare.plantcare.ui.theme.spacing

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WithPermission(
    modifier: Modifier = Modifier,
    requestedPermissions: List<String>,

    content: @Composable (MultiplePermissionsState) -> Unit
) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(
        requestedPermissions
    )

    if (!permissionState.allPermissionsGranted) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = MaterialTheme.spacing.extraSmall,
                alignment = Alignment.CenterVertically
            ),
        ) {
            if (permissionState.shouldShowRationale) {
                SquareButton(
                    onClick = {
                        permissionState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Grant Permissions")
                }
            } else {
                SquareButton(
                    onClick = {
                        openAppSettings(context)
                    }
                ) {
                    Text("Open settings")
                }
                InlinedText(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    annotatedText = buildAnnotatedString {
                        appendInlineContent("warning_icon")
                        append(" ")
                        append("Grant permissions in app settings.")
                    },
                    annotationDictionary = mapOf("warning_icon" to { color ->
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = color,
                        )
                    })
                )
            }
        }
        return
    }

    Box(modifier = modifier) {
        content(permissionState)
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addCategory(Intent.CATEGORY_DEFAULT)
        // idk seems to break loading data
//        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
    context.startActivity(intent)
}
