package jafar.stories.utils

import android.Manifest
import com.google.android.gms.maps.model.LatLng

object Constanta {
    /* API Request time */
    const val READ_TIME_OUT = 25.toLong()
    const val WRITE_TIME_OUT = 300.toLong()
    const val CONNECT_TIME_OUT = 60.toLong()

    /* Detail Content */
    const val EXTRA_DATA = "extra data"

    /* Coordinate */
    enum class Coordinate { IsLocationPicked, Lat, Lon }

    /* Add Story Activity */
    val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    const val REQUEST_CODE_PERMISSION = 10
    const val EXTRA_COORDINATE = "Extra coordinate"

    /* Maps Fragment */
    val indonesiaLocation = LatLng(-3.283087, 117.595434)

    /* Default value */
    const val defaultValue = "Not set"

    /*Preference DataStore Constanta*/
    enum class Preferences { IsLogin, Token }
}
