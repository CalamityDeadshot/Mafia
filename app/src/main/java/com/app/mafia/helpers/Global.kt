package com.app.mafia.helpers

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import kotlin.math.sqrt


class Global {
    companion object {
        const val shrinkExpandDuration: Long = 330
        var screenX = 0
        var screenY = 0
        var scaleFactor : Float = 0f
        fun init(activity: Activity) {
            calculateScaleFactor(activity)
        }

        private fun calculateScaleFactor(activity: Activity) {
            val a = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                150f/2,
                activity.resources.displayMetrics
            ).toInt()
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenY = displayMetrics.heightPixels
            screenX = displayMetrics.widthPixels


            scaleFactor = (sqrt((screenX * screenX + screenY*screenY).toDouble()) /(2*a)).toFloat() + 1
            println("$screenX : $screenY, a = $a, scaleFactor = $scaleFactor")

        }

        fun calculateNoOfColumns(
            context: Context,
            columnWidthDp: Float
        ): Int { // For example columnWidthdp=180
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
            return (screenWidthDp / columnWidthDp + 0.5).toInt()
        }
    }


}