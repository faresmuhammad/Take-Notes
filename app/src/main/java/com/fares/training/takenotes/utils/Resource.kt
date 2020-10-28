package com.fares.training.takenotes.utils

sealed class Resource<out T> {

    open val data:T? = null
    open val message:String = ""

    data class Success<T>(override val data: T?) : Resource<T>()

    data class Error<T>(override val message: String, override val data: T?) : Resource<T>()

    data class Loading<T>(override val data: T?) : Resource<T>()
}