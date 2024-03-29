package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarRecyclerView : RecyclerView, OnDateSelected {
    private var position :Int= 0
        set(value) {
            field = value
            if (field < 0)field = 0
        }
    private var isLoading: Boolean = false
    private var bookedDates: ArrayList<GathernReservationModel?>? = ArrayList()
    private var busyList: ArrayList<String?>? = null
    private val calenderList: ArrayList<CalenderModel> = ArrayList()
    private var showWeekDays = true
    private var busyDrawableRefId = 0
    private var bookedDrawableRefId = 0
    private var endDrawableRefId = 0
    private var rangeDrawableRefId = 0
    private var singleDrawableRefId = 0
    private var mPrevMonth = 0
    private var onDateSelected: OnDateSelected? = null
    private val TAG = "CalendarRecyclerView"
    private var calenderAdapter: CalenderAdapter? = null


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        setAttrs(attrs)
    }

    private fun setAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarRecyclerView)
        showWeekDays = typedArray.getBoolean(R.styleable.CalendarRecyclerView_ShowWeekDays, true)
        busyDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_busyDrawable,
            R.drawable.busy_shape
        )
        bookedDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_bookedDrawable,
            R.drawable.booked_shape
        )
        //
        endDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_endDrawable,
            R.drawable.booked_shape
        )
        rangeDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_rangeDrawable,
            R.drawable.booked_shape
        )
        //
        singleDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_singleDrawable,
            R.drawable.single_selection
        )
        mPrevMonth = typedArray.getInt(R.styleable.CalendarRecyclerView_prevMonths , 0)
        typedArray.recycle()
    }

    fun initialize(
        bookedList: ArrayList<GathernReservationModel?>?,
        busyList: ArrayList<String?>? ,
        prevMonth:Int = 0
    ) {
        this.mPrevMonth = prevMonth
        if (bookedList != null) {
            for (item in bookedList){
                this.bookedDates?.add(
                    GathernReservationModel(checkInDate = item?.checkInDate ,
                    checkoutDate = getYesterday(item?.checkoutDate?:"") ,
                    clientName = item?.clientName ,
                    id = item?.id ,
                    type = item?.type)
                )
            }
        }

        this.busyList = busyList
        Log.v(TAG, "initialize RV")
        initList()
        initAdapter()
    }

    private fun initList() {
        var isRange = false
        var bookingTag:GathernReservationModel? = null
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        if (mPrevMonth >0){
            val prevDate = mPrevMonth*-1
            calendar.add(Calendar.MONTH,prevDate)
        }
        val start = calendar.time
        calenderList.clear()
        //
        val calStartDate = Calendar.getInstance()
        calStartDate.time = start

        var monthsAdded = 0
        while (monthsAdded < 12) {
            val date = calStartDate.time
            if (isCurrentMonth(date) == true){
                position =calenderList.size -1
            }
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
                val indexStart = bookedDates?.containGathernStart(parseDateToString(model.date))
                val isStart = indexStart != null
                val indexEnd = bookedDates?.containGathernEnd( parseDateToString(model.date))
                val isEnd = indexEnd != null
                if (isStart) {
                    bookingTag = indexStart
                    isRange = true
                    model.shapeState = CalenderModel.shapeFlagBooked
                    model.clientName = indexStart?.clientName?:""
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
                Log.v(TAG , "model $model")
                if (busyList?.contains(parseDateToString(model.date)) == true) {
                    model.isBusy = true
                }
                model.bookingTag = bookingTag
                calenderList.add(model)
                calStartDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            monthsAdded++
        }
    }

    fun appendToList() {
        var isRange = false
        var bookingTag:GathernReservationModel? = null
        var monthsAdded = 0
        val startPosition = calenderList.size - 1
        val startDate = calenderList[startPosition].date
        val calStartDate = Calendar.getInstance()
        calStartDate.time = startDate!!
        calStartDate.add(Calendar.DAY_OF_MONTH, 1)

        while (monthsAdded < 12) {//calStartDate.time.before(end)
            val date = calStartDate.time
            calenderList.add(CalenderModel(date, CalenderModel.titleViewType))// add month title
            calenderList.add(CalenderModel(date, CalenderModel.weekDaysViewType)) // add week days
            val currentMonth = calStartDate[Calendar.MONTH]
            //set blank days in start of month
            val blankDays = calStartDate[Calendar.DAY_OF_WEEK] - 1
//             Log.v(TAG , "blankDays $blankDays monthsAdded $monthsAdded months $months calStartDate.time ${calStartDate.time}")
            if (blankDays != 7) {
                for (i in 0 until blankDays) {
//                     Log.v(TAG , "i $i")
                    calenderList.add(CalenderModel(null, CalenderModel.dayTextViewType))
                }
            }
            // define day milliseconds because current day time may be not started
            // from 00:00:00
            val day = 86400000
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
                val indexStart = bookedDates?.containGathernStart(parseDateToString(model.date))
                val isStart = indexStart != null
                val indexEnd = bookedDates?.containGathernEnd( parseDateToString(model.date))
                val isEnd = indexEnd != null
                val isStartedBeforeMonth = bookedDates.isContainReservation(parseDateToString(model.date))
                if (isStartedBeforeMonth != null){
                    isRange = true
                }
                if (isStart) {
                    bookingTag = indexStart
                    isRange = true
                    model.shapeState = CalenderModel.shapeFlagBooked
                    model.clientName = indexStart?.clientName?:""
                    model.rangeState = CalenderModel.rangeFlagStart
                }
                if (isStart && isEnd){
                    isRange = false
                    model.rangeState = CalenderModel.rangFlagStartEnd
                }else if (isEnd){
                    isRange = false
                    model.rangeState = CalenderModel.rangeFlagEnd
                }
                if (isRange && !isStart){
                    model.rangeState = CalenderModel.rangeFlagRange
                }
                  if (busyList?.contains(parseDateToString(model.date)) == true) {
                    model.isBusy = true
                }
                model.bookingTag = bookingTag
                calenderList.add(model)
                calStartDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            monthsAdded++
        }
        calenderAdapter?.notifyItemInserted(startPosition)
        isLoading = false
    }

    private fun initAdapter() {
        calenderAdapter = CalenderAdapter(
            context,
            calenderList,
            showWeekDays
        )
        calenderAdapter?.setOnDateSelected(this)
        val layoutManager = GridLayoutManager(
            context,
            7, VERTICAL, false
        )
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType: Int = calenderList[position].viewType
                return when (viewType) {
                    CalenderModel.titleViewType, CalenderModel.weekDaysViewType -> 7
                    else -> 1
                }
            }
        }
        this.adapter = calenderAdapter
        if (this.layoutManager == null) {
            this.layoutManager = layoutManager
        }
        this.scrollToPosition(position)
        initScrollListener()
    }

    private fun initScrollListener() {
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    val linearLayoutManager: GridLayoutManager =
                        recyclerView.layoutManager as GridLayoutManager
                    if (!isLoading) {
                        val lastPos = linearLayoutManager.findLastVisibleItemPosition()
                        val lastIndexInList = calenderList.size - 1
                        if (lastPos > lastIndexInList - 100) {
                            //bottom of list!
                            isLoading = true
                            appendToList()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }



    fun setOnDateSelected(onDateSelected: OnDateSelected?) {
        this.onDateSelected = onDateSelected
    }

    fun clearAll() {
        calenderAdapter?.clearAll()
        onDateSelected?.onSelected(ArrayList(), ArrayList())
    }

    override fun onSelected(availableList: ArrayList<String>?, busyList: ArrayList<String>?) {
        onDateSelected?.onSelected(availableList, busyList)
    }

    override fun onBookedDatesSelected(model: GathernReservationModel?) {
        onDateSelected?.onBookedDatesSelected(model)
    }


}


