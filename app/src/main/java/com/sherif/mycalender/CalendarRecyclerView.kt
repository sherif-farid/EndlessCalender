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
    private var isLoading: Boolean = false
    private var bookedDates: ArrayList<GathernReservationModel?>? = null
    private var busyList: ArrayList<String?>? = null
    private val calenderList: ArrayList<CalenderModel> = ArrayList()
    private var showWeekDays = true
    private var busyDrawableRefId = 0
    private var bookedDrawableRefId = 0
    private var singleDrawableRefId = 0
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
        singleDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_singleDrawable,
            R.drawable.single_selection
        )
        typedArray.recycle()
    }

    fun initialize(
        bookedList: ArrayList<GathernReservationModel?>?,
        busyList: ArrayList<String?>?
    ) {
        this.bookedDates = bookedList
        this.busyList = busyList
        Log.v(TAG, "initialize RV")
        initList(12)
        initAdapter()
    }

    private fun initList(months: Int = 0) {
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
                val index = bookedDates?.containGathernStart(parseDateToString(model.date))?:-1
                if (index > -1) {
                    model.shapeState = CalenderModel.shapeFlagGathernStart
                    model.clientName = bookedDates?.get(index)?.clientName?:""
                }
                if (busyList?.contains(parseDateToString(model.date)) == true) {
                    model.isBusy = true
                }
                calenderList.add(model)
                calStartDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            monthsAdded++
        }
    }

    fun appendToList(months: Int = 0) {
        var monthsAdded = 0
        val startPosition = calenderList.size - 1
        val startDate = calenderList[startPosition].date
        val calStartDate = Calendar.getInstance()
        calStartDate.time = startDate!!
        calStartDate.add(Calendar.DAY_OF_MONTH, 1)

        while (monthsAdded < months) {//calStartDate.time.before(end)
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
                val index = bookedDates?.containGathernStart(parseDateToString(model.date))?:-1
                if (index > -1) {
                    model.shapeState = CalenderModel.shapeFlagGathernStart
                    model.clientName = bookedDates?.get(index)?.clientName?:""
                }
                if (busyList?.contains(parseDateToString(model.date)) == true) {
                    model.isBusy = true
                }
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
            showWeekDays,
            busyDrawableRefId,
            bookedDrawableRefId,
            singleDrawableRefId,
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
        if (this.layoutManager == null)
            this.layoutManager = layoutManager
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
                            appendToList(12)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
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

    override fun onBookedDatesSelected() {
        onDateSelected?.onBookedDatesSelected()
    }

}


