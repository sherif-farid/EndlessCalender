package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sherif.mycalender.databinding.DayItemBinding
import com.sherif.mycalender.databinding.MonthTitleItemBinding
import com.sherif.mycalender.databinding.WeekDaysItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalenderAdapter(
    private val context: Context,
    private val arrayList: ArrayList<CalenderModel>,
    private val showWeekDays: Boolean,
    private val busyDrawableRefId: Int = R.drawable.busy_shape,
    private val bookedDrawableRefId: Int = R.drawable.booked_shape,
    private val singleDrawableRefId: Int = R.drawable.single_selection,
    private val endDrawableRefId : Int=R.drawable.end_shape,
    private val rangeDrawableRefId: Int=R.drawable.range_shape,
    private val startDrawableRefId:Int = R.drawable.start_shape ,
    private val startEndDrawableRefId:Int = R.drawable.start_end,
    private val todayDrawableRefId:Int = R.drawable.today
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CalenderAdapterTAG"
    private var onDateSelected: OnDateSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CalenderModel.titleViewType -> {
                val binding: MonthTitleItemBinding =
                    MonthTitleItemBinding.inflate(inflater, parent, false)
                TitleViewHolder(binding)
            }
            CalenderModel.weekDaysViewType -> {
                val binding2: WeekDaysItemBinding =
                    WeekDaysItemBinding.inflate(inflater, parent, false)
                WeekDaysViewHolder(binding2)
            }

          else -> {
                val dayBind: DayItemBinding = DayItemBinding.inflate(inflater, parent, false)
                DaysViewHolder(dayBind)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = arrayList[position]
        return model.viewType
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder) {
            is TitleViewHolder -> {
                initItemTitle(holder, model)
            }
            is WeekDaysViewHolder -> {
                initItemWeekDays(holder, model)
            }
            is DaysViewHolder -> {
                initItemDays(holder, model )
            }
        }
    }

    private fun initItemTitle(holder: TitleViewHolder, model: CalenderModel?) {
        try {
            val month = getMonth(model?.date)
            val year = getYear(model?.date)
            val monthArray = context.resources.getStringArray(R.array.months)
            val monthIndex = month.toInt()
            val title = monthArray[monthIndex - 1].toString() + " " + year
            holder.binding.monthTitle.text = title
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMonth(date: Date?): String {
        val sdf = SimpleDateFormat("MM", Locale.ENGLISH)
        return if (date != null) sdf.format(date) else ""
    }

    private fun getYear(date: Date?): String {
        val sdf = SimpleDateFormat("yyyy", Locale.ENGLISH)
        return if (date != null) sdf.format(date) else ""
    }

    private fun initItemWeekDays(holder: WeekDaysViewHolder, model: CalenderModel?) {
        holder.binding.weekDays1.visibility = if (showWeekDays) View.VISIBLE else View.GONE
        val array = context.resources.getStringArray(R.array.week_days)
        if (array.size != 7) return
        holder.binding.sun.text = array[0]
        holder.binding.mon.text = array[1]
        holder.binding.tu.text = array[2]
        holder.binding.wed.text = array[3]
        holder.binding.thu.text = array[4]
        holder.binding.fri.text = array[5]
        holder.binding.sat.text = array[6]
    }
    private fun setEndMargin(v:View ,margin:Int , rootView:View){
        rootView.post {
            val glp = rootView.layoutParams as GridLayoutManager.LayoutParams
            val index = glp.spanIndex
            var mMargin = margin
            if (index %6 == 0)mMargin = 0// last item in span row is 6
            val mlp = v.layoutParams as ViewGroup.MarginLayoutParams
            mlp.marginEnd = mMargin
            v.layoutParams = mlp
        }
    }
    private fun initItemDays(holder: DaysViewHolder, model: CalenderModel? ) {
        var day = 0
        if (model?.date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = model.date!!
            day = calendar[Calendar.DAY_OF_MONTH]
        }
        holder.binding.day.text = when {
                day == 0 -> ""
                model?.clientName?.isNotEmpty() == true -> model.clientName
                else -> day.toString()
            }
        var dayTextDrawable: Drawable? = null
        var bcViewDrawable: Drawable? = null
        var textColor = ResourcesCompat.getColor(context.resources, R.color.textBlackColor, null)
        holder.binding.disableLine.visibility = View.GONE
        holder.binding.dayFrame.alpha = 1f
        holder.binding.day.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
        setEndMargin(holder.binding.dayFrame , -42 , rootView = holder.binding.root)
        when(model?.rangeState){
            CalenderModel.rangFlagStartEnd ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, startEndDrawableRefId, null
                )
            }
            CalenderModel.rangeFlagStart ->{
                setEndMargin(holder.binding.dayFrame , 0 , rootView = holder.binding.root)
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, startDrawableRefId, null
                )
            }
            CalenderModel.rangeFlagEnd ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, endDrawableRefId, null
                )
            }
            CalenderModel.rangeFlagRange ->{
                setEndMargin(holder.binding.dayFrame , 0 , rootView = holder.binding.root)
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, rangeDrawableRefId, null
                )
            }
        }
        when (model?.shapeState) {
            CalenderModel.shapeFlagDisabled -> {
                holder.binding.dayFrame.alpha = 0.2f
                if (model.clientName.isNotEmpty()){
                    dayTextDrawable = ResourcesCompat.getDrawable(
                        context.resources, bookedDrawableRefId, null
                    )
                    textColor = ResourcesCompat.getColor(
                        context.resources,
                        R.color.mywhite, null
                    )
                    holder.binding.day.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f)
                }
            }
            CalenderModel.shapeFlagBooked -> {
                dayTextDrawable = ResourcesCompat.getDrawable(
                    context.resources, bookedDrawableRefId, null
                )
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mywhite, null
                )
                holder.binding.day.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f)
                //                holder.binding.disableLine.visibility = View.VISIBLE
            }
            CalenderModel.shapeFlagNone -> {
                dayTextDrawable = if (isToday(model.date)) {
                    ResourcesCompat.getDrawable(
                        context.resources, todayDrawableRefId, null
                    )
                }else {
                    null
                }
            }
            CalenderModel.shapeFlagSingleSelection -> {
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mywhite, null
                )
                dayTextDrawable = ResourcesCompat.getDrawable(
                    context.resources, singleDrawableRefId, null
                )
            }
        }

        if (model?.isBusy == true && model.shapeState != CalenderModel.shapeFlagSingleSelection){
            dayTextDrawable= ResourcesCompat.getDrawable(
                context.resources, busyDrawableRefId, null
            )
            holder.binding.disableLine.visibility = View.VISIBLE
        }
        holder.binding.day.background = dayTextDrawable
        holder.binding.bcView.background = bcViewDrawable
        holder.binding.day.setTextColor(textColor)
    }

    fun setOnDateSelected(onDateSelected: OnDateSelected?) {
        this.onDateSelected = onDateSelected
    }


    class TitleViewHolder internal constructor(binding: MonthTitleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding: MonthTitleItemBinding

        init {
            this.binding = binding
        }
    }

    class WeekDaysViewHolder internal constructor(binding: WeekDaysItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val binding: WeekDaysItemBinding

        init {
            this.binding = binding
        }
    }

    inner class DaysViewHolder(val binding: DayItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View) {
            val pos = layoutPosition
            val glp = binding.root.layoutParams as GridLayoutManager.LayoutParams
            val index = glp.spanIndex
            val clickedModel = arrayList[pos]
            logs(TAG , "indexClicked $index date ${clickedModel.date}")

            if (clickedModel.rangeState == CalenderModel.rangeFlagRange ||
                clickedModel.rangeState == CalenderModel.rangeFlagEnd) {
                onDateSelected?.onBookedDatesSelected()
                return
            }
            if (clickedModel.date == null) return
            if (clickedModel.shapeState == CalenderModel.shapeFlagDisabled) return
            if (clickedModel.shapeState == CalenderModel.shapeFlagBooked ) {
                onDateSelected?.onBookedDatesSelected()
                return
            }

            if (clickedModel.shapeState == CalenderModel.shapeFlagSingleSelection) {
                clickedModel.shapeState =
                    CalenderModel.shapeFlagNone// should hold initial states
            } else {
                clickedModel.shapeState = CalenderModel.shapeFlagSingleSelection
            }
            notifyItemChanged(pos)
            val availableList: ArrayList<String> = ArrayList()
            val busyList: ArrayList<String> = ArrayList()
            arrayList.forEach {
                // free days selected to be busy
                if (it.shapeState == CalenderModel.shapeFlagSingleSelection && !it.isBusy) {
                    availableList.add(parseDate(it.date) ?: "")
                }
                // busy days selected to remove busy and make it available
                if (it.isBusy && it.shapeState == CalenderModel.shapeFlagSingleSelection){
                    busyList.add(parseDate(it.date) ?: "")
                }
            }
            onDateSelected?.onSelected(availableList ,busyList )
        }

        init {
            binding.day.setOnClickListener(this)
        }
    }




    fun clearAll() {
        for (model in arrayList) {
            model.shapeState = CalenderModel.shapeFlagNone
        }
        notifyDataSetChanged()
    }
    private fun isToday(date: Date?):Boolean {
        val dateSt = parseDate(date)
        val today = parseDate(Date())
        return dateSt == today
    }
    private fun parseDate(date: Date?): String? {
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