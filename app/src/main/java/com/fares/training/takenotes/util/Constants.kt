package com.fares.training.takenotes.util

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
    }

    object Note {
        const val DEFAULT_NOTE_COLOR = "FFA500"

    }

    object Dialog {
        const val COLOR_FRAGMENT_TAG = "AddEditNoteFragment"
        const val ADD_OWNER_DIALOG_TAG = "ADD_OWNER_DIALOG_TAG"
    }
}