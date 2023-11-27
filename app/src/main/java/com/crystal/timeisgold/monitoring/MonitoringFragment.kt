package com.crystal.timeisgold.monitoring

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.crystal.timeisgold.R
import com.crystal.timeisgold.data.Record
import com.crystal.timeisgold.databinding.FragmentMonitoringBinding
import com.crystal.timeisgold.record.RecordViewModel
import com.crystal.timeisgold.util.ContextUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MonitoringFragment"

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TypeChartAdapter

    private val recordViewModel: RecordViewModel by lazy {
        ViewModelProvider(this).get(RecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_monitoring, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setValues()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValues() {

        val typeList = ContextUtil.getTypeListPref(requireContext())
        var records = emptyList<Record>()

        setPieChart()
        initRecyclerView()

        CoroutineScope(Dispatchers.Main).launch {

            records = getDailyRecords()

            updateTypeChart(typeList, records)
        }
    }

    private fun updateTypeChart(typeList: List<String>, records: List<Record>) {
        adapter = TypeChartAdapter(requireContext(), typeList, records)
        binding.typeChartRecyclerView.adapter = adapter
    }

    private suspend fun getDailyRecords(): List<Record> {
        val list: List<Record> = withContext(Dispatchers.IO) {
            recordViewModel.getDailyRecords()
        }
        return list
    }

    private fun initRecyclerView() {
        binding.typeChartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setPieChart() {
        val itemList = ContextUtil.getTypeListPref(requireContext())
        val entries = ArrayList<PieEntry>()

        binding.pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        binding.pieChart.minAngleForSlices = 20f

        CoroutineScope(Dispatchers.Main).launch {

            var otherTime = 86400
            for (i in 0 until itemList.size) {
                val itemTime = recordViewModel.getTime(itemList[i])
                if (itemTime > 100) {
                    otherTime -= itemTime
                    entries.add(PieEntry(itemTime.toFloat(), itemList[i]))
                }

                if (i == itemList.size - 1) {
                    if (entries.isEmpty()) {
                        entries.add(PieEntry(86400f, getString(R.string.no_time)))
                    } else {
                        entries.add(PieEntry(otherTime.toFloat(), getString(R.string.no_time)))
                    }
                }
            }

            val colorsItems = arrayListOf<Int>(
                Color.parseColor("#22695C"),
                Color.parseColor("#0b986a"),
                Color.parseColor("#4dc493"),
                Color.parseColor("#82dcb9"),
            )
            for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
            for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)

            val pieDataSet = PieDataSet(entries, "")
            pieDataSet.apply {
                colors = colorsItems
                valueTextSize = 14f
                valueTextColor = getColor(R.color.grey)
                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                valueLineColor = getColor(R.color.grey)
                valueLinePart1OffsetPercentage = 70f
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.2f
            }

            pieDataSet.valueFormatter = MyValueFormatter()

            val pieData = PieData(pieDataSet)
            binding.pieChart.apply {
                data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                centerText = "Today"
                setEntryLabelColor(getColor(context, R.color.grey))
                animateY(1400, Easing.EaseInOutQuad)
                animate()
            }

        }

    }
}