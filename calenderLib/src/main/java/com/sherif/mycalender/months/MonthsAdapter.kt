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
import com.sherif.mycalender.logs
import com.sherif.mycalender.parseDateToString
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

    private val TAG = "MonthsAdapterTAG"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
         val dayBind: MonthViewBinding = MonthViewBinding.inflate(inflater, parent, false)
        dayBind.root.post {
            val width = dayBind.baseView.width
            logs(TAG , "before height ${dayBind.root.height} width ${dayBind.root.width}")
            val lp = dayBind.root.layoutParams
            lp.apply {
                this.height = width
            }
            dayBind.root.layoutParams = lp
            dayBind.root.requestLayout()
            logs(TAG , "after height ${dayBind.root.height} width ${dayBind.root.width}")
        }
         return DaysViewHolder(dayBind)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = arrayList[position]
        logs(TAG , "model $model position $position")
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
        }else {
            holder.binding.baseView.background = null
            holder.binding.rootFrame.background = null
            holder.binding.bcView.background = null
            return
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
        val dateSt = parseDateToString(date)
        val today = parseDateToString(Date())
        return dateSt == today
    }

}