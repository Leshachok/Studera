package app.studera.android.model

import app.studera.android.util.Building
import app.studera.android.util.LessonType

data class Lesson(
    val start: String,
    val end: String,
    val title: String,
    val type: LessonType,
    val lecturer: String,
    val link: String?,
    val building: Building?,
    val number: Int,
    val locationDescription: String?
)