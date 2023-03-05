package app.studera.android.data

import app.studera.android.model.Lesson
import app.studera.android.util.Building
import app.studera.android.util.LessonType
import app.studera.android.util.zonedDateTime
import java.time.LocalDate

class EducationRepository {

    companion object {
        val I: EducationRepository = EducationRepository()
    }

    private var lessons: List<Lesson>

    fun getLessons(date: LocalDate): List<Lesson>{
        return lessons.filter { lesson ->
            val eventTime = lesson.start.zonedDateTime()
            date.dayOfWeek == eventTime.dayOfWeek
        }.sortedBy { it.number }
    }

    fun getBuildings(): List<Building>{
        return listOf(Building.ICS, Building.IEE, Building.RGF, Building.ADMINISTRATION, Building.MAIN)
    }

    init {
        lessons = listOf(
            Lesson(
                start = "2023-02-27T08:00:01.004Z",
                end = "2023-03-05T09:50:01.004Z",
                title = "Сучасні технології програмування на перфокартах",
                type = LessonType.PRACTICE,
                lecturer = "О. О. Арсірій",
                link = "https://us06web.zoom.us/j/84008858525?pwd=SHVkWVdEOG5RbUZrblhnam14Z0FKdz09",
                building = null,
                locationDescription = null,
                number = 1
            ),
            Lesson(
                start = "2023-02-27T11:40:01.004Z",
                end = "2023-03-05T13:15:01.004Z",
                title = "Основи ООП",
                type = LessonType.LAB,
                lecturer = "О. О. Олапак",
                link = "https://us06web.zoom.us/j/84008858525?pwd=SHVkWVdEOG5RbUZrblhnam14Z0FKdz09",
                building = null,
                locationDescription = null,
                number = 3
            ),
            Lesson(
                start = "2023-02-28T09:50:01.004Z",
                end = "2023-03-05T11:25:01.004Z",
                title = "Англійська мова",
                type = LessonType.LECTURE,
                lecturer = "М. К. Лобачьов",
                link = null,
                building = Building.IEE,
                locationDescription = null,
                number = 2
            ),
            Lesson(
                start = "2023-03-01T10:05:01.004Z",
                end = "2023-03-05T11:40:01.004Z",
                title = "Основи Кібербезпеки",
                type = LessonType.PRACTICE,
                lecturer = "П.О. Тесленко",
                link = null,
                building = Building.IEE,
                locationDescription = null,
                number = 3
            ),
            Lesson(
                start = "2023-03-02T10:05:01.004Z",
                end = "2023-03-05T11:40:01.004Z",
                title = "Історія України",
                type = LessonType.PRACTICE,
                lecturer = "О. О. Арсірій",
                link = null,
                building = Building.ICS,
                locationDescription = null,
                number = 2
            ),
            Lesson(
                start = "2023-03-03T10:05:01.004Z",
                end = "2023-03-05T11:40:01.004Z",
                title = "Сучасні технології програмування на перфокартах",
                type = LessonType.PRACTICE,
                lecturer = "О. О. Арсірій",
                link = null,
                building = Building.MAIN,
                locationDescription = null,
                number = 2
            ),
            Lesson(
                start = "2023-03-04T10:05:01.004Z",
                end = "2023-03-05T11:40:01.004Z",
                title = "Менеджмент проектів",
                type = LessonType.LECTURE,
                lecturer = "М.І. Лобачов",
                link = null,
                building = Building.IEE,
                locationDescription = null,
                number = 2
            ),
            Lesson(
                start = "2023-03-05T08:00:01.004Z",
                end = "2023-03-05T09:50:01.004Z",
                title = "Сучасні технології програмування на перфокартах",
                type = LessonType.LECTURE,
                lecturer = "О. О. Арсірій",
                link = null,
                building = Building.IEE,
                locationDescription = null,
                number = 1
            ),
            Lesson(
                start = "2023-03-05T10:05:01.004Z",
                end = "2023-03-05T11:40:01.004Z",
                title = "Сучасні технології програмування на перфокартах",
                type = LessonType.PRACTICE,
                lecturer = "О. О. Арсірій",
                link = null,
                building = Building.ICS,
                locationDescription = null,
                number = 2
            ),
            Lesson(
                start = "2023-03-05T11:55:01.004Z",
                end = "2023-03-05T13:40:01.004Z",
                title = "Сучасні технології програмування на перфокартах",
                type = LessonType.LAB,
                lecturer = "П. О. Тесленко",
                link = null,
                building = Building.ICS,
                locationDescription = null,
                number = 3
            ),
        )
    }
}