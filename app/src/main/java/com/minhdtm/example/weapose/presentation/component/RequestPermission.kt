package com.minhdtm.example.weapose.presentation.component

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun RequestPermissionLocation(
    onPermissionGranted: () -> Unit,
    onPermissionNotGranted: () -> Unit,
) {
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGrantedFineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            if (isGrantedFineLocation) {
                onPermissionGranted.invoke()
            } else {
                onPermissionNotGranted.invoke()
            }
        }

    LaunchedEffect(key1 = true) {
        val isGrantedFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isGrantedCoarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isAndroidS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        if (isAndroidS) {
            if (isGrantedCoarseLocation && isGrantedFineLocation) {
                onPermissionGranted.invoke()
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        } else {
            if (isGrantedFineLocation) {
                onPermissionNotGranted.invoke()
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    onPermissionFineGranted: () -> Unit,
    onPermissionCoarseGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    ) { permissions ->
        if (permissions.all { it.value }) {
            onPermissionFineGranted.invoke()
        } else if (permissions.getValue(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            onPermissionCoarseGranted.invoke()
        } else {
            onPermissionDenied.invoke()
        }
    }

    LaunchedEffect(key1 = true) {
        when {
            locationPermissionState.allPermissionsGranted -> {
                onPermissionFineGranted.invoke()
            }
            locationPermissionState.shouldShowRationale -> {
                onPermissionDenied.invoke()
            }
            else -> {
                locationPermissionState.launchMultiplePermissionRequest()
            }
        }
    }
}
