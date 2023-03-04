package app.studera.android.util

import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

fun ukDaysOfWeek(): List<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.forLanguageTag("uk")).firstDayOfWeek
    val pivot = 7 - firstDayOfWeek.ordinal
    val daysOfWeek = DayOfWeek.values()
    return (daysOfWeek.takeLast(pivot) + daysOfWeek.dropLast(pivot))
}