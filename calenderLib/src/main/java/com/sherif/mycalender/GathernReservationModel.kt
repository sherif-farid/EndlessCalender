package com.sherif.mycalender
data class GathernReservationModel(
    val id:String?= null,
    val type:Int? = null,
    val checkInDate: String?= null,
    var checkoutDate: String?= null,
    val clientName: String?= null,
    internal var rangeState: RangeState? = null
){
    enum class RangeState{
        Start , Range , End , StartEnd
    }
}