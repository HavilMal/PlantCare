package com.plantCare.plantcare.utils

import java.util.UUID
import kotlin.random.Random

object RandomUtil {
    fun genUUID(): UUID {
        return UUID.randomUUID()
    }

    fun genUUIDString(): String {
        return genUUID().toString()
    }

    fun genBool(): Boolean {
        return Random.nextBoolean()
    }
}