package com.crystal.timeisgold.history

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crystal.timeisgold.R
import com.crystal.timeisgold.data.CalendarData
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val context: Context
): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private val calendarViewModel = CalendarViewModel.getCalendarViewModelInstance()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val dayOfWeekText: TextView = itemView.findViewById(R.id.dayOfWeekText)
        private val dayText: TextView = itemView.findViewById(R.id.dayText)
        private val checkRecordView: View = itemView.findViewById(R.id.checkRecordView)

        fun bind(calData: CalendarData) {

            val calendar = Calendar.getInstance()
            calendar.time = calData.date

            if (calData.isSelected) {
                dayOfWeekText.setTextColor(ContextCompat.getColor(context,R.color.highlight_color))
                dayText.setTextColor(ContextCompat.getColor(context,R.color.highlight_color))
                dayOfWeekText.typeface = Typeface.DEFAULT_BOLD
                dayText.typeface = Typeface.DEFAULT_BOLD
            } else {
                dayOfWeekText.setTextColor(ContextCompat.getColor(context,R.color.grey))
                dayText.setTextColor(ContextCompat.getColor(context,R.color.grey))
                dayOfWeekText.typeface = Typeface.DEFAULT
                dayText.typeface = Typeface.DEFAULT
            }

            if (calData.hasRecord) {
                checkRecordView.backgroundTintList = ContextCompat.getColorStateList(context, R.color.button_color)
            } else {
                checkRecordView.backgroundTintList = ContextCompat.getColorStateList(context, R.color.transparent)
            }

            val dayOfWeek =  calendar.get(Calendar.DAY_OF_WEEK)
            val day = calendar.get(Calendar.DATE)

            val dayOfWeekFormat = SimpleDateFormat("EE", Locale.getDefault())

            dayOfWeekText.text = dayOfWeekFormat.format(calData.date)
            dayText.text = day.toString()

            itemView.setOnClickListener {
                calendarViewModel.updateCurrentSelect(calData.date)
            }
        }
    }


    private val differCallback = object : DiffUtil.ItemCallback<CalendarData>() {
        override fun areItemsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
            return oldItem.date == newItem.date

        }
        override fun areContentsTheSame(oldItem: CalendarData, newItem: CalendarData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_calendar_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

}