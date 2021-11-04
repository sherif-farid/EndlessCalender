package com.sherif.mycalender

import java.util.*


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */



class CalenderModel(var date: Date?, var viewType: Int) {
     var shapeState = shapeFlagNone
        set(value) {
            if (field != shapeFlagDisabled && field !=shapeFlagUnAvailable)
            field = value
        }


    companion object {
        const val titleViewType = 1
        const val weekDaysViewType = 2
        const val dayTextViewType = 3

        //
        const val shapeFlagNone = 0
        const val shapeFlagSingleSelection = 1
        const val shapeFlagStart = 2
        const val shapeFlagRange = 3
        const val shapeFlagEnd = 4
        const val shapeFlagDisabled = 5
        const val shapeFlagUnAvailable = 6
    }


    override fun toString(): String {
        return "{shapeState :$shapeState , date :$date}"
    }
}
