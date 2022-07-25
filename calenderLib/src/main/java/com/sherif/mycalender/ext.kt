package com.sherif.mycalender

import android.util.Log

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
fun ArrayList<GathernReservationModel?>?.containGathernEnd(date: String?): Int {
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