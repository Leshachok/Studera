package app.studera.android.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

fun DayOfWeek.displayText(uppercase: Boolean = false, style: TextStyle = TextStyle.SHORT): String {
    val locale = Locale.forLanguageTag("uk")
    return getDisplayName(style, locale).let { value ->
        if (uppercase) value.uppercase(locale) else value
    }
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL_STANDALONE
    val locale = Locale.forLanguageTag("uk")
    return getDisplayName(style, locale).replaceFirstChar { c -> c.uppercase() }
}

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)


fun Int.formatTime(): String{
    return if (this < 10) "0$this" else "$this"
}

val Int.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()


fun String.zonedDateTime(): ZonedDateTime{
    val zoneId = Calendar.getInstance().timeZone.toZoneId()

    val formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneId.of("Europe/London"))

    val date = ZonedDateTime.parse(this, formatter)
    return date.withZoneSameInstant(zoneId)
}

fun ZonedDateTime.localHM(): String{
    return "${this.hour}:${this.minute.formatTime()}"
}