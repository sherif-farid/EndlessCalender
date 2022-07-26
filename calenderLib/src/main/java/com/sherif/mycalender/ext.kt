package com.sherif.mycalender

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun ArrayList<GathernReservationModel?>?.containGathernStart(date: String?): Int {
    val mList = this
    if (mList != null) {
        for (item in mList) {
            if (item?.checkInDate == date) {
                return mList.indexOf(item)
            }
        }
    }
    return -1
}
fun ArrayList<GathernReservationModel?>?.containGathernEnd(date: String? ): Int {
    // roll back 1 day for checkout only
    val mList = this
    if (mList != null) {
        for (item in mList) {
            if (item?.checkoutDate == date) {
                return mList.indexOf(item)
            }
        }
    }
    return -1
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
fun parseStringToDate(date: String?): Date? {
    if (date == null) return null
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return try {
        sdf.parse(date)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
fun getYesterday(date:String):String{
    val oneDay = 86400000
    val checkoutTime :Long= parseStringToDate(date)?.time?:0
    val yesDate = Date(checkoutTime - oneDay)
    return parseDateToString(yesDate)?:""
}
