package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

interface OnDateSelected {
    fun onSelected(availableList:ArrayList<String>? , busyList:ArrayList<String>?)
    fun onBookedDatesSelected()
}