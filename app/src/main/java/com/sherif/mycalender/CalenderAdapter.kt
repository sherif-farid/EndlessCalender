package com.sherif.mycalender


/*
 * Created by Sherif farid
 * Date: 7/28/2021.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sherif.mycalender.databinding.DayItemBinding
import com.sherif.mycalender.databinding.MonthTitleItemBinding
import com.sherif.mycalender.databinding.WeekDaysItemBinding
import java.text.SimpleDateFormat
import java.util.*


class CalenderAdapter(
    private val context: Context,
    private val arrayList: ArrayList<CalenderModel>,
    private val showWeekDays: Boolean,
    private val startDrawableRefId: Int,
    private val endDrawableRefId: Int,
    private val rangeDrawableRefId: Int,
    private val singleDrawableRefId: Int ,
     var nightsLimit:Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CalenderAdapterTAG"
    private var fromDate: CalenderModel? = null
    private var toDate: CalenderModel? = null
    private var onDateSelected: OnDateSelected? = null
    private var onError:OnError? = null

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

            /*CalenderModel.dayTextViewType*/ else -> {
                val dayBind: DayItemBinding = DayItemBinding.inflate(inflater, parent, false)
//                val params = dayBind.day.layoutParams
//                params.height = width/7
//                dayBind.day.layoutParams = params
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
                initItemDays(holder, model)
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

    private fun initItemDays(holder: DaysViewHolder, model: CalenderModel?) {
//        Log.v(TAG , "model?.shapeState : ${model?.shapeState} date :${model?.date}")
        var day = 0
        if (model?.date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = model.date!!
            day = calendar[Calendar.DAY_OF_MONTH]
        }
        holder.binding.day.text = if (day == 0) "" else day.toString()
        var dayTextDrawable: Drawable? = null
        var frameDrawable: Drawable? = null
        var textColor = ResourcesCompat.getColor(context.resources, R.color.textBlackColor, null)
        holder.binding.disableLine.visibility = View.GONE
        when (model?.shapeState) {
            CalenderModel.shapeFlagDisabled -> {
                dayTextDrawable = null
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mydarkgray, null
                )
            }
            CalenderModel.shapeFlagUnAvailable -> {
                dayTextDrawable = null
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.grayText, null
                )
                holder.binding.disableLine.visibility = View.VISIBLE
            }
            CalenderModel.shapeFlagNone -> {
                dayTextDrawable = null
            }
            CalenderModel.shapeFlagSingleSelection -> {
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mywhite, null
                )
                dayTextDrawable = ResourcesCompat.getDrawable(
                    context.resources, singleDrawableRefId, null
                )
                frameDrawable = null
            }
            CalenderModel.shapeFlagStart -> {
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mywhite, null
                )
                dayTextDrawable = ResourcesCompat.getDrawable(
                    context.resources, singleDrawableRefId, null
                )
                frameDrawable = ResourcesCompat.getDrawable(
                    context.resources, startDrawableRefId, null
                )
            }
            CalenderModel.shapeFlagRange -> {
                dayTextDrawable = null
                frameDrawable = ResourcesCompat.getDrawable(
                    context.resources, rangeDrawableRefId, null
                )
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.colorPrimary, null
                )
            }
            CalenderModel.shapeFlagEnd -> {
                textColor = ResourcesCompat.getColor(
                    context.resources,
                    R.color.mywhite, null
                )
                dayTextDrawable = ResourcesCompat.getDrawable(
                    context.resources, singleDrawableRefId, null
                )
                frameDrawable = ResourcesCompat.getDrawable(
                    context.resources, endDrawableRefId, null
                )
            }
        }
        holder.binding.day.background = dayTextDrawable
        holder.binding.dayFrame.background = frameDrawable
        holder.binding.day.setTextColor(textColor)
    }

    fun setOnDateSelected(onDateSelected: OnDateSelected?) {
        this.onDateSelected = onDateSelected
    }
    fun setOnError(onError:OnError?){
        this.onError = onError
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
            val clickedModel = arrayList[pos]
            if (clickedModel.date == null) return
            if (clickedModel.shapeState == CalenderModel.shapeFlagDisabled) return
            if (clickedModel.shapeState == CalenderModel.shapeFlagUnAvailable) return
            //
            if (fromDate != null &&
                toDate == null &&
                clickedModel.date?.after(fromDate?.date) == true &&
                clickedModel.date?.equals(fromDate?.date) == false
            ) {
                val startPos = arrayList.indexOf(fromDate)
                arrayList[startPos].shapeState = CalenderModel.shapeFlagStart
                clickedModel.shapeState = CalenderModel.shapeFlagEnd
                toDate = clickedModel
                if (getNightsCount() > nightsLimit){
                    onError?.onNightsLimitReached(nightsLimit)
                    resetClickedAsStart(clickedModel)
                }else {
                    setRange(startPos, pos)
                }
            } else {
                resetClickedAsStart(clickedModel)
            }
            notifyDataSetChanged()
            onDateSelected?.onSelected(
                parseDate(fromDate?.date),
                parseDate(toDate?.date?:getTomorrow()),
                getNightsCount()
            )
        }

        init {
            binding.day.setOnClickListener(this)
        }
    }
    private fun getTomorrow():Date?{
        val today = Calendar.getInstance()
        today.time = fromDate?.date?:return null
        today.add(Calendar.DATE ,1)
        return today.time
    }

    private fun resetClickedAsStart(clickedModel: CalenderModel) {
        clearAllSelections("288")
        fromDate = clickedModel
        clickedModel.shapeState = CalenderModel.shapeFlagSingleSelection
        toDate = null
    }
    private fun clearAllSelections(line:String) {
//        Log.v(TAG , "line $line")
        for (model in arrayList) {
            model.shapeState = CalenderModel.shapeFlagNone
        }
    }
     fun clearAll(){
        clearAllSelections("300")
        fromDate = null
        toDate = null
        notifyDataSetChanged()
    }
    private fun setRange(start: Int, end: Int) {

        for (model in arrayList) {
            if (arrayList.indexOf(model) > start && arrayList.indexOf(model) < end) {
                if (model.shapeState == CalenderModel.shapeFlagUnAvailable) {
                    // clear all selection and you can inform user here
//                    Log.e(TAG, "first cross dates ${model.date}")
                    onError?.onCrossDate()
                    clearAllSelections("313")
                    val newModel = arrayList[end]
                    newModel.shapeState = CalenderModel.shapeFlagSingleSelection
                    fromDate = newModel
                    toDate = null
                    return
                }
                model.shapeState = CalenderModel.shapeFlagRange
            }
        }
    }

    private fun getNightsCount(): Int {
        val from: Long = fromDate?.date?.time ?: 0
        val to: Long = toDate?.date?.time ?: 0
        var nights: Int = ((to - from) / 86400000).toInt()
        if (nights < 1) nights = 1
        return nights
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