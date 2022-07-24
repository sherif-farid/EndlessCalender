package com.sherif.mycalender.months

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sherif.mycalender.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
 * Created by Sherif farid
 * Date: 7/24/2022.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

class MonthsRv : RecyclerView {
    private var bookedDates: ArrayList<GathernReservationModel?>?= null
    private val calenderList: ArrayList<CalenderModel> = ArrayList()
    private var calenderAdapter: MonthsAdapter? = null
    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {

    }


    fun initialize(
        bookedList: ArrayList<GathernReservationModel?>?
    ) {
        this.bookedDates = bookedList
        initList(1)
        initAdapter()
    }
    private fun initList(months: Int = 0) {
        var isRange = false
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val start = calendar.time
        calenderList.clear()
        //
        val calStartDate = Calendar.getInstance()
        calStartDate.time = start

        var monthsAdded = 0
        while (monthsAdded < months) {
            val date = calStartDate.time
            calenderList.add(CalenderModel(date, CalenderModel.titleViewType))// add month title
            calenderList.add(CalenderModel(date, CalenderModel.weekDaysViewType)) // add week days
            val currentMonth = calStartDate[Calendar.MONTH]
            //set blank days in start of month
            val blankDays = calStartDate[Calendar.DAY_OF_WEEK] - 1
//            Log.v(TAG , "blankDays $blankDays monthsAdded $monthsAdded months $months calStartDate.time ${calStartDate.time}")
            if (blankDays != 7) {
                for (i in 0 until blankDays) {
//                    Log.v(TAG , "i $i")
                    calenderList.add(CalenderModel(null, CalenderModel.dayTextViewType))
                }
            }
            // define day milliseconds because current day time may be not started
            // from 00:00:00
            val day = 86400000
//            var isInRange = false
            while (calStartDate[Calendar.MONTH] == currentMonth) {
                val model = CalenderModel(calStartDate.time, CalenderModel.dayTextViewType)
                val t1 = model.date?.time ?: 0
                val t2 = Date().time
                val diff = t2 - t1
                if (diff >= day //to disable yesterday
                // || model.date?.after(end) == true // to disable after end date
                ) {
                    model.shapeState = CalenderModel.shapeFlagDisabled
                }
                val indexStart = bookedDates?.containGathernStart(parseDateToString(model.date))?:-1
                val isStart = indexStart > -1
                val indexEnd = bookedDates?.containGathernEnd(parseDateToString(model.date))?:-1
                val isEnd = indexEnd > -1
                if (isStart) {
                    isRange = true
                    model.shapeState = CalenderModel.shapeFlagBooked
                    model.clientName = bookedDates?.get(indexStart)?.clientName?:""
                    model.rangeState = CalenderModel.rangeFlagStart
                }
                if (isStart && isEnd){
                    isRange = false
                    model.rangeState = CalenderModel.rangFlagStartEnd
                }else if (isEnd){
                    isRange = false
                    model.rangeState = CalenderModel.rangeFlagEnd
                }
                if (isRange&& !isStart){
                    model.rangeState = CalenderModel.rangeFlagRange
                }
                calenderList.add(model)
                calStartDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            monthsAdded++
        }
    }
    private fun initAdapter() {
        calenderAdapter = MonthsAdapter(
            context,
            calenderList
        )
        val layoutManager = GridLayoutManager(
            context,
            7, VERTICAL, false
        )
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        this.adapter = calenderAdapter
        if (this.layoutManager == null)
            this.layoutManager = layoutManager
    }

    private fun parseDateToString(date: Date?): String? {
        if (date == null) return null
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            sdf.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}