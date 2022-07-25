package com.sherif.mycalender.months

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sherif.mycalender.R
import com.sherif.mycalender.databinding.MonthViewBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/*
 * Created by Sherif farid
 * Date: 7/24/2022.
 * email: sherffareed39@gmail.com.
 * phone: 00201007538470
 */

class MonthsAdapter(
    private val context: Context,
    private val arrayList: ArrayList<MonthModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CalenderAdapterTAG"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
         val dayBind: MonthViewBinding = MonthViewBinding.inflate(inflater, parent, false)
        dayBind.root.post {
            val height = dayBind.root.height
            val width = dayBind.root.width
            Log.v(TAG , "height $height")
            Log.v(TAG , "width $width")
            val lp = dayBind.root.layoutParams
//            if (height > width){
//                lp.apply {
//                    this.height = width
//                }
//            }else if (width > height){
//                lp.apply {
//                    this.width = height
//                }
//            }
//            dayBind.root.layoutParams = lp
        }
         return DaysViewHolder(dayBind)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        when (holder) {
            is DaysViewHolder -> {
                initItemDays(holder, model)
            }
        }
    }

    private fun initItemDays(holder: DaysViewHolder, model: MonthModel?) {
        if (model?.date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = model.date!!
        }
        var baseViewBc: Drawable? = null
        var rootFrameBc: Drawable? = null
        when(model?.rangeState){
            MonthModel.rangFlagStartEnd ->{
                rootFrameBc =  /*ResourcesCompat.getDrawable(
                    context.resources, R.drawable.month_start_end, null
                )*/null
            }
            MonthModel.rangeFlagStart ->{
                rootFrameBc =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.month_start_shape, null
                )
            }
            MonthModel.rangeFlagEnd ->{
                baseViewBc = ResourcesCompat.getDrawable(
                    context.resources, R.drawable.month_end_shape, null
                )
            }
            MonthModel.rangeFlagRange ->{
                rootFrameBc =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.month_range_shape, null
                )
            }
        }
        when (model?.shapeState) {
            MonthModel.shapeFlagBooked -> {
                baseViewBc = ResourcesCompat.getDrawable(
                    context.resources, R.drawable.month_booked_shape, null
                )
            }
            MonthModel.shapeFlagNone -> {
                 if (isToday(model.date)) {
                     baseViewBc =   ResourcesCompat.getDrawable(
                        context.resources, R.drawable.today, null
                    )
                }
            }
        }

        holder.binding.baseView.background = baseViewBc
        holder.binding.rootFrame.background = rootFrameBc
    }

    inner class DaysViewHolder(val binding: MonthViewBinding) :
        RecyclerView.ViewHolder(binding.root)

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