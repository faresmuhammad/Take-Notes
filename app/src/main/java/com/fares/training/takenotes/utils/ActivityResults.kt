package com.fares.training.takenotes.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class PickAnImageContract : ActivityResultContract<Void, Uri>() {
    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        val image = intent?.data

        return if (image != null && resultCode == Activity.RESULT_OK) image
        else null
    }
}

class PickMultipleImagesContract : ActivityResultContract<Void, List<Uri>>() {
    override fun createIntent(context: Context, input: Void?): Intent =
        Intent(Intent.ACTION_GET_CONTENT).apply {
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            type = "image/*"
        }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val imagesSelected = mutableListOf<Uri>()
        val clipData = intent?.clipData

        clipData?.let { clip ->
            if (resultCode == Activity.RESULT_OK) {
                for (i in 0 until clip.itemCount) {
                    val imageUri = clip.getItemAt(i).uri
                    imagesSelected.add(imageUri)
                }
            }
        }
        if (clipData == null && resultCode == Activity.RESULT_OK) {
            val imageUri = intent?.data
            imagesSelected.add(imageUri!!)
        }


        return imagesSelected
    }
}