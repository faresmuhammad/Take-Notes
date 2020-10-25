package com.fares.training.takenotes.util

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.fares.training.takenotes.util.Constants.Permission.EXTERNAL_STORAGE_REQUEST_CODE
import com.fares.training.takenotes.util.Constants.Permission.READ_EXTERNAL_STORAGE
import com.fares.training.takenotes.util.Constants.Preferences.KEY_PERMISSION_FIRST_ASKED
import javax.inject.Inject

@Inject
lateinit var pref: SharedPreferences

fun Fragment.checkExternalStoragePermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
    }
    return false

}

fun Fragment.requestExternalStoragePermission() {
    requireActivity().requestPermissions(
        arrayOf(READ_EXTERNAL_STORAGE),
        EXTERNAL_STORAGE_REQUEST_CODE
    )

}

fun Fragment.handleRequestPermissionsCases(explanation: () -> Unit) {
    val requestPermissionRationale =
        requireActivity().shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)

    if (requestPermissionRationale) {
        explanation()
        requestExternalStoragePermission()
    } else {
        if (pref.getBoolean(KEY_PERMISSION_FIRST_ASKED, true)) {
            requestExternalStoragePermission()
            pref.edit {
                putBoolean(KEY_PERMISSION_FIRST_ASKED, false)
            }
        } else {
            goToPermissionsSettings()
        }
    }
}

fun Fragment.goToPermissionsSettings() {
    val uri = Uri.fromParts("package", requireActivity().packageName, null)
    val permissionSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = uri
        addCategory(Intent.CATEGORY_DEFAULT)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    requireActivity().startActivity(permissionSettings)
}


fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}