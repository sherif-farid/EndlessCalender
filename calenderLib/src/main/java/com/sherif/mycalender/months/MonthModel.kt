package com.sherif.mycalender.months

import java.util.*

class MonthModel(var date: Date? ) {
    var rangeState = 0
    var shapeState = shapeFlagNone
        set(value) {
            if (field != shapeFlagDisabled && field !=shapeFlagBooked)
                field = value
        }


    companion object {
        const val rangeFlagStart = 20
        const val rangeFlagEnd = 30
        const val rangeFlagRange = 40
        const val rangFlagStartEnd = 50

        const val shapeFlagNone = 0
        const val shapeFlagSingleSelection = 1
        const val shapeFlagDisabled = 5// like past days
        // cross dash on the view removed and replaced with client name
        const val shapeFlagBooked = 6
    }


    override fun toString(): String {
        return "shapeState :$shapeState , date :$date , rangeState: $rangeState"
    }
}