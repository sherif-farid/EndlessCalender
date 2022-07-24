package com.sherif.mycalender.months

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sherif.mycalender.CalenderModel
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
    private val arrayList: ArrayList<CalenderModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CalenderAdapterTAG"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
         val dayBind: MonthViewBinding = MonthViewBinding.inflate(inflater, parent, false)
        dayBind.root.post {
            Log.v(TAG , "height ${dayBind.root.height}")
            Log.v(TAG , "width ${dayBind.root.width}")
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

    private fun initItemDays(holder: DaysViewHolder, model: CalenderModel?) {
        if (model?.date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = model.date!!
        }
        var frameDrawable: Drawable? = null
        var bcViewDrawable: Drawable? = ResourcesCompat.getDrawable(
            context.resources, R.drawable.start_end, null
        )
        when(model?.rangeState){
            CalenderModel.rangFlagStartEnd ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.start_end, null
                )
            }
            CalenderModel.rangeFlagStart ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.start_shape, null
                )
            }
            CalenderModel.rangeFlagEnd ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.end_shape, null
                )
            }
            CalenderModel.rangeFlagRange ->{
                bcViewDrawable =  ResourcesCompat.getDrawable(
                    context.resources, R.drawable.range_shape, null
                )
            }
        }
        when (model?.shapeState) {
            CalenderModel.shapeFlagDisabled -> {
                if (model.clientName.isNotEmpty()){
                    frameDrawable = ResourcesCompat.getDrawable(
                        context.resources, R.drawable.booked_shape, null
                    )
                }
            }
            CalenderModel.shapeFlagBooked -> {
                frameDrawable = ResourcesCompat.getDrawable(
                    context.resources, R.drawable.booked_shape, null
                )
            }
            CalenderModel.shapeFlagNone -> {
                frameDrawable = if (isToday(model.date)) {
                    ResourcesCompat.getDrawable(
                        context.resources, R.drawable.today, null
                    )
                }else {
                    null
                }
            }
            CalenderModel.shapeFlagSingleSelection -> {
                frameDrawable = ResourcesCompat.getDrawable(
                    context.resources, R.drawable.single_selection, null
                )
            }
        }


        holder.binding.dayFrame.background = frameDrawable
        holder.binding.bcView.background = bcViewDrawable
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