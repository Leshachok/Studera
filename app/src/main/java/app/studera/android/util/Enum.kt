package app.studera.android.util

import com.google.android.gms.maps.model.LatLng

enum class LessonType {
    LECTURE, LAB, PRACTICE
}

enum class Building {
    MAIN {
        override val location: LatLng = LatLng(46.4601194,30.7507463)
        },
    ADMINISTRATION{
        override val location: LatLng = LatLng(46.4601194,30.7507463)
    },
    ICS{
        override val location: LatLng = LatLng(46.4593594,30.7521201)
    },
    IEE{
        override val location: LatLng = LatLng(46.4589792,30.7504963)
    },
    RGF{
        override val location: LatLng = LatLng(46.4601194,30.7507463)
    };

    abstract val location: LatLng
}