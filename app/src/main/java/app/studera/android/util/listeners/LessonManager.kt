package app.studera.android.util.listeners

import app.studera.android.model.Lesson

interface LessonManager {

    fun onLessonClicked(lesson: Lesson)
}