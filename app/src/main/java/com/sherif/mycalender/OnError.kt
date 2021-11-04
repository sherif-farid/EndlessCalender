package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 8/1/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

interface OnError {
    fun  onCrossDate()
    fun onNightsLimitReached(limit: Int)
}