package com.fares.training.takenotes.utils

object Constants {

    object Network {

        val IGNORE_AUTH_URLS = listOf("/register", "/login")

        const val BASE_URL = "https://192.168.1.3:8002"
    }

    object Database {
        const val DATABASE_NAME = "notes_db"
    }

    object Preferences {
        const val ENCRYPTED_SHARED_PREF_NAME = "encrypted_pref"
        const val KEY_LOGGED_EMAIL = "KEY_LOGGED_EMAIL"
        const val KEY_LOGGED_PASSWORD = "KEY_LOGGED_PASSWORD"
        const val NO_EMAIL = "NO_EMAIL"
        const val NO_PASSWORD = "NO_PASSWORD"
        const val KEY_PERMISSION_FIRST_ASKED = "KEY_PERMISSION_FIRST_ASKED"
    }

    object Permission {
        const val READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val EXTERNAL_STORAGE_REQUEST_CODE = 400
    }

    object Note {
        const val DEFAULT_NOTE_COLOR = "FFA500"

    }

    object Dialog {
        const val COLOR_FRAGMENT_TAG = "AddEditNoteFragment"
        const val ADD_OWNER_DIALOG_TAG = "ADD_OWNER_DIALOG_TAG"
    }

    object Intents {
        const val PICK_IMAGE_EXTRA = "PICK_IMAGE_EXTRA"
    }
}