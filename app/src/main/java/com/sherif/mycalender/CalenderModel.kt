package com.sherif.mycalender

import java.util.*


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */



class CalenderModel(var date: Date?, var viewType: Int ,var isBusy:Boolean = false) {
     var shapeState = shapeFlagNone
        set(value) {
            if (field != shapeFlagDisabled && field !=shapeFlagBooked)
            field = value
        }


    companion object {
        const val titleViewType = 1
        const val weekDaysViewType = 2
        const val dayTextViewType = 3

        //
        const val shapeFlagNone = 0
        const val shapeFlagSingleSelection = 1
        const val shapeFlagDisabled = 5// like past days
        const val shapeFlagBooked = 6 // cross dash on the view
    }


    override fun toString(): String {
        return "{shapeState :$shapeState , date :$date}"
    }
}
