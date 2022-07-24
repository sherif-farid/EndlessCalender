package com.sherif.mycalender

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