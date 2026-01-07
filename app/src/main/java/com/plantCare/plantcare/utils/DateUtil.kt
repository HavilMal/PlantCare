package com.plantCare.plantcare.utils

import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId

object DateUtil{

    private val zone: ZoneId = ZoneId.systemDefault()

    fun startOfDay(date: LocalDate): Timestamp {
        return Timestamp(
            date.atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
        )
    }

    fun endOfDay(date: LocalDate): Timestamp {
        return Timestamp(
            date.plusDays(1)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli() - 1
        )
    }

    fun localDateToday() : LocalDate {
        return LocalDate.now()
    }
}