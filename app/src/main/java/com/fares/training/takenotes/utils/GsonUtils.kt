package com.fares.training.takenotes.utils

import com.google.gson.InstanceCreator
import java.lang.reflect.Type

class ByteArrayInstanceCreator(val size: Int) : InstanceCreator<ByteArray> {
    override fun createInstance(type: Type?): ByteArray = ByteArray(size)
}