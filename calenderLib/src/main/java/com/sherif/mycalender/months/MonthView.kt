package com.sherif.mycalender.months

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
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

class MonthView : RecyclerView {
    private val tag = "MonthsRvTag"
    private var bookedDates: ArrayList<GathernReservationModel?>?= ArrayList()
    private val calenderList: ArrayList<MonthModel> = ArrayList()
    private var calenderAdapter: MonthsAdapter? = null
    constructor(context: Context) : super(context) {
//        initialize(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        initialize(null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
//      initialize(null)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    fun initialize(
        bookedList: ArrayList<GathernReservationModel?>?
    ) {
        bookedList?.forEach {
            this.bookedDates?.add(GathernReservationModel(checkInDate = it?.checkInDate ,
            checkoutDate = getYesterday(it?.checkoutDate?:"") ,
            clientName = it?.clientName))
        }
        initList(1)
        initAdapter()
    }
    private fun initList(months: Int = 0) {
        var isRange = false
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val start = calendar.time
        calenderList.clear()
        val calStartDate = Calendar.getInstance()
        calStartDate.time = start

        val currentMonth = calStartDate[Calendar.MONTH]
        //set blank days in start of month
        val blankDays = calStartDate[Calendar.DAY_OF_WEEK] - 1
        if (blankDays != 7) {
            for (i in 0 until blankDays) {
                calenderList.add(MonthModel(null))
            }
        }

        while (calStartDate[Calendar.MONTH] == currentMonth) {
            val model = MonthModel(calStartDate.time)
            val indexStart = bookedDates?.containGathernStart(parseDateToString(model.date))?:-1
            val isStart = indexStart > -1
            val indexEnd = bookedDates?.containGathernEnd(parseDateToString(model.date) )?:-1
            val isEnd = indexEnd > -1
            logs(tag , "isStart $isStart isEnd $isEnd date ${model.date}")
            if (isStart) {
                isRange = true
                model.shapeState = MonthModel.shapeFlagBooked
                model.rangeState = MonthModel.rangeFlagStart
            }
            if (isStart && isEnd){
                isRange = false
                model.rangeState = MonthModel.rangFlagStartEnd
            }else if (isEnd){
                isRange = false
                model.rangeState = MonthModel.rangeFlagEnd
            }
            if (isRange&& !isStart){
                model.rangeState = MonthModel.rangeFlagRange
            }
            calenderList.add(model)
            calStartDate.add(Calendar.DAY_OF_MONTH, 1)
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