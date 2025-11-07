package com.plantCare.plantcare.utils

import java.util.UUID

object RandomUtil {
    fun genUUID(): UUID {
        return UUID.randomUUID()
    }

    fun genUUIDString(): String {
        return genUUID().toString()
    }
}