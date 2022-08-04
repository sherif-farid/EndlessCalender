package com.sherif.mycalender

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun ArrayList<GathernReservationModel?>?.containGathernStart(date: String?): GathernReservationModel? {
    val mList = this
    if (mList != null) {
        for (item in mList) {
            if (item?.checkInDate == date) {
                return item
            }
        }
    }
    return null
}
fun ArrayList<GathernReservationModel?>?.isContainReservation(date: String?): GathernReservationModel? {
   if (date == null)return null
    val mList = this
    if (mList != null) {
        for (item in mList) {
            if (item != null) {
                when {
//                    item.checkInDate == date && item.checkoutDate == date -> {
//                        item.rangeState = GathernReservationModel.RangeState.StartEnd
//                        return item
//                    }
//                    item.checkInDate == date -> {
//                        item.rangeState = GathernReservationModel.RangeState.Start
//                        return item
//                    }
//                    item.checkoutDate == date -> {
//                        item.rangeState = GathernReservationModel.RangeState.End
//                        return item
//                    }

                    date.toApiDateFormat()  > item.checkInDate.toApiDateFormat() &&
                            date.toApiDateFormat() <   item.checkoutDate.toApiDateFormat()  -> {
                        item.rangeState = GathernReservationModel.RangeState.Range
                        return item
                    }
                }
            }
        }
    }
    return null
}
fun ArrayList<GathernReservationModel?>?.containGathernEnd(date: String? ): GathernReservationModel? {
    // roll back 1 day for checkout only
    val mList = this
    if (mList != null) {
        for (item in mList) {
            if (item?.checkoutDate == date) {
                return item
            }
        }
    }
    return null
}
fun logs(tag:String , value:String){
    if (com.sherif.mycalender.BuildConfig.DEBUG){
        Log.v(tag , value)
    }
}
fun parseDateToString(date: Date?): String? {
    if (date == null) return null
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return try {
        sdf.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun String?.toApiDateFormat(): Date {
    val date = this ?:return Date()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return try {
        sdf.parse(date)
    } catch (e: Exception) {
        e.printStackTrace()
        Date()
    }
}
fun getYesterday(date:String):String{
    val oneDay = 86400000
    val checkoutTime :Long= date.toApiDateFormat().time
    val yesDate = Date(checkoutTime - oneDay)
    return parseDateToString(yesDate)?:""
}
fun getToday():String?{
    return parseDateToString(Date())
}
fun isToday(date: Date?):Boolean {
    val dateSt = parseDateToString(date)
    return dateSt == getToday()
}
fun isCurrentMonth(date: Date?):Boolean? {
    if (date == null) return null
    val sdf = SimpleDateFormat("yyyy-MM", Locale.ENGLISH)
    try {
       val currentMonth =  sdf.format(Date())
       val comparableMonth = sdf.format(date)
       return currentMonth == comparableMonth
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

