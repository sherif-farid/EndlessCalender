package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*



class CalendarRecyclerView : RecyclerView, OnDateSelected, OnError {
    private var selectedTo: String = ""
    private var selectedFrom: String=""
    private var disabledDates: ArrayList<String?>? = null
    private val arrayList: ArrayList<CalenderModel> = ArrayList<CalenderModel>()
    private var start: Date? = null
    private var end: Date? = null
    private var startDate: String = ""
    private var endDate: String = ""
    private var showWeekDays = true
    private var startDrawableRefId = 0
    private var endDrawableRefId = 0
    private var rangeDrawableRefId = 0
    private var singleDrawableRefId = 0
    private var onDateSelected: OnDateSelected? = null
    private var onError: OnError? = null
    private var enableRange = false
    private val TAG = "CalendarRecyclerView"
    private var calenderAdapter :CalenderAdapter? = null
    var nightsLimit:Int = 30
        set(value) {
            calenderAdapter?.nightsLimit = value
            field = value
        }


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
        startDate = typedArray.getString(R.styleable.CalendarRecyclerView_StartDate)?:""
        endDate = typedArray.getString(R.styleable.CalendarRecyclerView_EndDate)?:""
        enableRange = typedArray.getBoolean(R.styleable.CalendarRecyclerView_EnableRange, false)
        showWeekDays = typedArray.getBoolean(R.styleable.CalendarRecyclerView_ShowWeekDays, true)
        startDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_startDrawable,
            R.drawable.start_range
        )
        endDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_endDrawable,
            R.drawable.end_range
        )
        rangeDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_rangeDrawable,
            R.drawable.range
        )
        singleDrawableRefId = typedArray.getResourceId(
            R.styleable.CalendarRecyclerView_singleDrawable,
            R.drawable.single_selection
        )
        typedArray.recycle()
    }

     fun initialize(disabledList: ArrayList<String?>? , selectedFrom:String , selectedTo:String) {
        this.disabledDates = disabledList
        this.selectedFrom = selectedFrom
        this.selectedTo = selectedTo
//        Log.v(TAG, "initialize RV")
        initStartEndDates()
        initList()
        initAdapter()
    }

    private fun initStartEndDates() {
        if (startDate.length == 10) {
            start = parseStringToDate(startDate)
        } else {
            val calendar = Calendar.getInstance()
//            calendar[Calendar.MONTH] = 0 // jen
            calendar.set(Calendar.DAY_OF_MONTH ,1)
            start = calendar.time

            calendar.add(Calendar.DATE , 180)
//            Log.v(TAG, "start + 180  ${calendar.time}")
        }
        if (endDate.length == 10) {
            end = parseStringToDate(endDate)
        } else {
            val calendar = Calendar.getInstance()
            // 1000*60*60*24*179 = 15552000000 // next 180 days
            val today = calendar.timeInMillis
            val next180Days :Long= 15465600000 +today
//            Log.v(TAG ,"next180Days $next180Days today $today")
            calendar.timeInMillis = next180Days
            end = calendar.time
        }
    }

    private fun initList() {
//        Log.v(TAG, "start $start end $end")
        if (start == null || end == null)return
        arrayList.clear()
        //
        val calStartDate = Calendar.getInstance()
        calStartDate.time = start!!
        //
        val calEndDate = Calendar.getInstance()
        calEndDate.time = end!!

        //
        if (calEndDate.time.before(start)){
//            Log.e(TAG, "end date is before start date")
            return
        }
        //
        while (calStartDate.time.before(end)) {
            val date = calStartDate.time
            arrayList.add(CalenderModel(date, CalenderModel.titleViewType))
            arrayList.add(CalenderModel(date, CalenderModel.weekDaysViewType))
            val currentMonth = calStartDate[Calendar.MONTH]
            //set blank days in start of month
            val blankDays = calStartDate[Calendar.DAY_OF_WEEK]
            if (blankDays != 7) for (i in 1 until blankDays) {
                arrayList.add(CalenderModel(null, CalenderModel.dayTextViewType))
            }
            // define day milliseconds because current day time may be not started
            // from 00:00:00
            val day = 86400000
            var isInRange = false
            while (calStartDate[Calendar.MONTH] == currentMonth) {
                val model = CalenderModel(calStartDate.time, CalenderModel.dayTextViewType)
                val t1 = model.date?.time?:0
                val t2 = Date().time
                val diff =t2 - t1
//                Log.v(TAG , "diff $diff t1 $t1 t2 $t2")
                if (diff >=day || model.date?.after(end) == true){
//                    Log.v(TAG , "before model.date ${model.date} TODAY :${Date()}")
                    model.shapeState = CalenderModel.shapeFlagDisabled
                }
                if (disabledDates?.contains(parseDateToString(model.date)) == true){
                     model.shapeState = CalenderModel.shapeFlagUnAvailable
                }
                if (isInRange){
                    model.shapeState = CalenderModel.shapeFlagRange
                }
                if (parseDateToString(model.date) == selectedFrom){
//                    Log.v(TAG ,"selectedFrom reached")
                    model.shapeState = CalenderModel.shapeFlagStart
                    isInRange = true
                }
                if (parseDateToString(model.date) == selectedTo){
                    model.shapeState = CalenderModel.shapeFlagEnd
//                    Log.v(TAG ,"selectedTo reached")
                    isInRange =false
                }
                arrayList.add(model)
                calStartDate.add(Calendar.DAY_OF_MONTH, 1)
//                Log.v(TAG, "shapeState ${model.shapeState} date ${model.date}")
            }
        }
//        Log.v(TAG, "arrayList $arrayList")
    }

    private fun initAdapter() {
        calenderAdapter = CalenderAdapter(
            context,
            arrayList,
            showWeekDays,
            startDrawableRefId,
            endDrawableRefId,
            rangeDrawableRefId,
            singleDrawableRefId,
            nightsLimit
        )
        calenderAdapter?.setOnDateSelected(this)
        calenderAdapter?.setOnError(this)
        val layoutManager = GridLayoutManager(
            context,
            7, VERTICAL, false
        )
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType: Int = arrayList[position].viewType
                return when (viewType) {
                    CalenderModel.titleViewType, CalenderModel.weekDaysViewType -> 7
                    else -> 1
                }
            }
        }
        this.adapter = calenderAdapter
        this.layoutManager = layoutManager
    }



    private fun parseStringToDate(date: String): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            sdf.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }
    private fun parseDateToString(date: Date?): String? {
        if (date == null )return null
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return try {
            sdf.format(date)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    fun setOnDateSelected(onDateSelected: OnDateSelected?) {
        this.onDateSelected = onDateSelected
    }

    override fun onSelected(from: String?, to: String? , nights:Int) {
        onDateSelected?.onSelected(from, to ,nights )
    }
     fun clearAll(){
        calenderAdapter?.clearAll()
    }

    fun setOnError(onError: OnError) {
       this.onError = onError
    }

    override fun onCrossDate() {
        onError?.onCrossDate()
    }

    override fun onNightsLimitReached(limit: Int) {
      onError?.onNightsLimitReached(limit)
    }

}