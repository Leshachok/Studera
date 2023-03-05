package app.studera.android.util

import com.google.android.gms.maps.model.LatLng

enum class LessonType {
    LECTURE, LAB, PRACTICE
}

enum class Building {
    MAIN {
        override val location: LatLng = LatLng(46.4601194,30.7507463)
        override val title: String = "Головний корпус"
        },
    ADMINISTRATION{
        override val location: LatLng = LatLng(46.4610468,30.7508504)
        override val title: String = "Адміністрація"
    },
    ICS{
        override val location: LatLng = LatLng(46.4593594,30.7521201)
        override val title: String = "Корпус ІКС"
    },
    IEE{
        override val location: LatLng = LatLng(46.4589792,30.7504963)
        override val title: String = "Корпус ІЕЕ"
    },
    RGF{
        override val location: LatLng = LatLng(46.4608667,30.7527171)
        override val title: String = "Корпус РГФ"
    };

    abstract val location: LatLng
    abstract val title: String
}