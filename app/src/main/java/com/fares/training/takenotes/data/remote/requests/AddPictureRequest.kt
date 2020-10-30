package com.fares.training.takenotes.data.remote.requests

import java.net.URI

data class AddPictureRequest(
    val noteId: String,
    val picture: ByteArray
)