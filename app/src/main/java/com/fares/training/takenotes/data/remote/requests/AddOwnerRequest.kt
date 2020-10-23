package com.fares.training.takenotes.data.remote.requests

data class AddOwnerRequest(
    val owner: String,
    val noteId: String
)