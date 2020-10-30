package com.fares.training.takenotes.data.remote.responses

import java.net.URI

data class PictureResponse(
    val isSuccessful: Boolean,
    val picture: ByteArray?
)