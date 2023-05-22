package com.crystal.android.timeisgold.monitoring

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.data.Record
import com.crystal.android.timeisgold.databinding.TypeChartListItemBinding
import com.crystal.android.timeisgold.util.DateUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TypeChartAdapter(
    private val context: Context,
    var typeList: List<String>,
    var records: List<Record>
    ): RecyclerView.Adapter<TypeChartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: TypeChartListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(type: String) {

            binding.typeTextView.text = type

            val dailyList = mutableListOf<Date>()
            val xLabels = mutableListOf<String>()

            for (i in 0 until 7) {
                val cal = Calendar.getInstance()

                cal.get(Calendar.YEAR)
                cal.get(Calendar.MONTH)
                cal.add(Calendar.DATE, i - 7)
                val labelPattern = SimpleDateFormat("MM/dd", Locale.getDefault())
                xLabels.add(labelPattern.format(cal.time))
                dailyList.add(cal.time)
            }


            val entries = ArrayList<Entry>()

            for (i in dailyList.indices) {

                var sum = 0L

                for (j in records.indices) {
                    val record: Record = records[j]
                    if (type == record.type && DateUtil.differDates(dailyList[i], record.startDate)) {
                        sum += record.durationTime
                    }
                }

                sum /= 3600

                if (sum > 0) {
                    entries.add(Entry(i.toFloat(), sum.toFloat()))
                } else {
                    entries.add(Entry(i.toFloat(), 0f))
                }
            }

            val dataSet = LineDataSet(entries, "")

            dataSet.valueFormatter = MyValueFormatter()
            dataSet.setDrawFilled(true)
            dataSet.fillColor = ContextCompat.getColor(context, R.color.green)
            dataSet.setCircleColor(ContextCompat.getColor(context, R.color.green))
            dataSet.color = ContextCompat.getColor(context, R.color.green)


//            x 축 값
            binding.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)

            binding.lineChart.getTransformer(YAxis.AxisDependency.LEFT)
            binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            
            binding.lineChart.axisLeft.setLabelCount(7, true)
//            y축 값
            binding.lineChart.axisLeft.axisMaximum = 24f
            binding.lineChart.axisLeft.axisMinimum = 0f
            binding.lineChart.axisLeft.granularity = 1f

            binding.lineChart.axisRight.setDrawLabels(false)
            binding.lineChart.axisRight.setDrawAxisLine(false)
            binding.lineChart.axisRight.setDrawGridLines(false)
            binding.lineChart.description.text = context.getString(R
                .string.month_day)
            binding.lineChart.description.textColor = Color.GRAY
            binding.lineChart.axisLeft.textColor = Color.GRAY
            binding.lineChart.xAxis.textColor = Color.GRAY

            val data = LineData(dataSet)

            binding.lineChart.data = data
            binding.lineChart.invalidate()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TypeChartListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = typeList[position]
        holder.bind(type)
    }

    override fun getItemCount(): Int = typeList.size

}