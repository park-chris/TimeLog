package com.crystal.android.timeisgold.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.data.CalendarData
import com.crystal.android.timeisgold.databinding.FragmentHistoryBinding
import com.crystal.android.timeisgold.util.DateUtil
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HistoryFragment"

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val calendarViewModel = CalendarViewModel.getCalendarViewModelInstance()
    private var adapter: CalendarAdapter? = null
    private var startLastClickedTime: Long = 0L
    private var endLastClickedTime: Long = 0L
    private var currentDate: Date = Date()
    private var currentDay: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)

        setValues()

        Log.d(TAG, "onCreateView")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValues() {

        initCalendar()

        calendarViewModel.currentDate.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "currentDate: $currentDate")
                currentDate = it
                updateCalendar(currentDate)
                updateUI(currentDate)
            }
        }

        calendarViewModel.currentDay.observe(viewLifecycleOwner) {
            it?.let {
                Log.d(TAG, "update Day : $it")
                currentDay = it
                updateSelectedCalendar(it)
            }
        }
    }

    private fun setupEvents() {

        binding.todayButton.setOnClickListener {
            Toast.makeText(requireActivity(), "onClicekd", Toast.LENGTH_SHORT).show()
        }

    }

    private fun initCalendar() {

        adapter = CalendarAdapter(requireContext())
        binding.calendarRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.calendarRecyclerView.adapter = adapter

        binding.calendarRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val startView = recyclerView.getChildAt(0)
                val endView = recyclerView.getChildAt(binding.calendarRecyclerView.childCount - 1)
                val startDetector = startView.left // 첫번째 아이템 시작 x
                val endDetector =
                    endView.right - (binding.calendarRecyclerView.width + recyclerView.scrollX) // 마지막 아이템 끝 x

                if (startDetector == 0 && newState == 0) {

                    val interval = System.currentTimeMillis() - startLastClickedTime

                    if (interval < 1000) {
                        updateDate(currentDate, previous = true, next = false)
                    }

                    startLastClickedTime = System.currentTimeMillis()
                }

                if (endDetector == 0 && newState == 0) {

                    val interval = System.currentTimeMillis() - endLastClickedTime
                    if (interval < 1000) {
                        updateDate(currentDate, previous = false, next = true)
                    }

                    endLastClickedTime = System.currentTimeMillis()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

        })

        val calendar = Calendar.getInstance()

        calendarViewModel.updateCurrentDate(calendar.time)

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dates = mutableListOf<CalendarData>()

        for (i in 1 until lastDay + 1) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val cal = CalendarData(calendar.time, false)
            dates.add(cal)
        }

        adapter!!.differ.submitList(dates)
        calendarViewModel.updateCurrentDay(Date())


/*        binding.calendarRecyclerView.scrollToPosition(today - 2)

        val smoothScroller = CenterSmoothScroller(binding.calendarRecyclerView.context)

        smoothScroller.targetPosition = today

        binding.calendarRecyclerView.layoutManager?.startSmoothScroll(smoothScroller)*/
    }

    private fun updateDate(date: Date, previous: Boolean, next: Boolean) {

        val calendar = Calendar.getInstance()
        calendar.time = date

        if (previous) {
            calendar.add(Calendar.MONTH, -1)
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DATE, lastDay)
        }
        if (next) {
            calendar.add(Calendar.MONTH, 1)
            calendar.set(Calendar.DATE, 1)
        }

        calendarViewModel.updateCurrentDate(calendar.time)
    }


    private fun updateCalendar(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<CalendarData>()


        for (i in 1 until lastDay + 1) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val calData = CalendarData(calendar.time, false)
            list.add(calData)
        }
        Log.d(TAG, "month: ${calendar.get(Calendar.MONTH)} lastDay: ${lastDay} caL: ${calendar}")

        adapter!!.differ.submitList(list)

        Log.d(TAG, "calendar: ${adapter!!.differ.currentList.size}")

        // calendarViewModel.updateCurrentDay(date)
    }

    private fun updateUI(date: Date) {
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthStringFormat = SimpleDateFormat("MMM", Locale.getDefault())
        binding.monthText.text = monthStringFormat.format(date)
        binding.yearText.text = yearFormat.format(date)
    }

    private fun updateSelectedCalendar(date: Date) {
        adapter ?: return
  /*      val calendar = Calendar.getInstance()
        calendar.time = date
*/
        Log.d(TAG, "리스트 사이즈: ${adapter!!.differ.currentList.size}")

        val list = adapter!!.differ.currentList.mapIndexed { index, calData ->
            val newItem = calData.copy(
                isSelected = DateUtil.differDates(calData.date, date)
            )
            newItem
        }

        adapter!!.differ.submitList(list)

    }

    private fun scrollToCenter() {

    }
}