package com.crystal.timeisgold.history

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crystal.timeisgold.R
import com.crystal.timeisgold.custom.RecordInfoDialogFragment
import com.crystal.timeisgold.data.CalendarData
import com.crystal.timeisgold.data.Record
import com.crystal.timeisgold.databinding.FragmentHistoryBinding
import com.crystal.timeisgold.record.RecordViewModel
import com.crystal.timeisgold.util.CustomDialog
import com.crystal.timeisgold.util.DateUtil
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "HistoryFragment"

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var adapter: CalendarAdapter? = null
    private lateinit var recordAdapter: RecordAdapter
    private var startLastClickedTime: Long = 0L
    private var endLastClickedTime: Long = 0L
    private var currentCalendarDate: Date = Date()
    private var currentSelectDay: Date = Date()
    private var scrollPosition = 0
    private var calendarValidRecords = emptyList<Date>()

    private val calendarViewModel = CalendarViewModel.getCalendarViewModelInstance()
    private val recordViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)

        setValues()

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

        initRecords()
        initCalendar()

        calendarViewModel.currentCalendarDate.observe(viewLifecycleOwner) {
            it?.let {
                currentCalendarDate = it
                updateCalendar(currentCalendarDate)
                updateUI(currentCalendarDate)
            }
        }

        calendarViewModel.currentSelectDay.observe(viewLifecycleOwner) {
            it?.let {
                currentSelectDay = it
                updateSelectedCalendar(it)
                recordViewModel.loadRecords(it)
            }
        }

        recordViewModel.selectedRecordsLiveData.observe(viewLifecycleOwner) {
            updateRecord(it)
            if (it.isEmpty()) {
                binding.infoTextView.visibility = View.VISIBLE
            } else {
                binding.infoTextView.visibility = View.GONE
            }
        }
    }

    private fun setupEvents() {

        binding.todayButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            scrollPosition = calendar.get(Calendar.DAY_OF_MONTH)
            calendarViewModel.updateCurrentSelect(calendar.time)
            calendarViewModel.updateCurrentCalendar(calendar.time)
        }

        binding.prevButton.setOnClickListener {
            updateDate(currentCalendarDate, previous = true, next = false)
        }

        binding.nextButton.setOnClickListener {
            updateDate(currentCalendarDate, previous = false, next = true)
        }

    }

    private fun initCalendar() {

        adapter = CalendarAdapter(requireContext())
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.calendarRecyclerView.layoutManager = layoutManager
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
                        updateDate(currentCalendarDate, previous = true, next = false)
                    }

                    startLastClickedTime = System.currentTimeMillis()
                }

                if (endDetector == 0 && newState == 0) {

                    val interval = System.currentTimeMillis() - endLastClickedTime
                    if (interval < 1000) {
                        updateDate(currentCalendarDate, previous = false, next = true)
                    }

                    endLastClickedTime = System.currentTimeMillis()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

        })

        val calendar = Calendar.getInstance()

        scrollPosition = calendar.get(Calendar.DAY_OF_MONTH)

        calendarViewModel.updateCurrentCalendar(calendar.time)

        updateCalendarUI(calendar)



    }

    private fun updateCalendarUI(calendar: Calendar) {

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dates = mutableListOf<CalendarData>()

        CoroutineScope(Dispatchers.Main).launch {

            calendarValidRecords = getMonthRecordDates(calendar.time)

            for (i in 1 until lastDay + 1) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val cal = CalendarData(calendar.time, isSelected = false, hasRecord = checkHasRecord(calendar.time))
                dates.add(cal)
            }

            adapter!!.differ.submitList(dates)

            calendarViewModel.updateCurrentSelect(Date())

        }

    }

    private fun checkHasRecord(date: Date): Boolean {
        val filterDate = calendarValidRecords.filter { d ->
            DateUtil.differDates(d, date)
        }

        return filterDate.isNotEmpty()
    }

    private suspend fun getMonthRecordDates(date: Date) = withContext(Dispatchers.IO) {
        recordViewModel.getCheckRecordsSum(date)
    }

    private fun initRecords() {
        recordAdapter = RecordAdapter(requireContext())
        recordAdapter.setOnItemClickListener(object : RecordAdapter.SetOnItemClickListener {
            override fun onSelectMenu(record: Record) {
                showBottomDialog(record)
            }

            override fun onClickItem(id: UUID) {
                val dialog = RecordInfoDialogFragment.newInstance(id)
                dialog.show(requireActivity().supportFragmentManager, "RecordInfoDialogFragment")
            }
        })
        binding.recordRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recordRecyclerView.adapter = recordAdapter
    }

    private fun updateDate(date: Date, previous: Boolean, next: Boolean) {

        val calendar = Calendar.getInstance()
        calendar.time = date

        if (previous) {
            calendar.add(Calendar.MONTH, -1)
            val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DATE, lastDay)
            scrollPosition = lastDay - 1
        }
        if (next) {
            calendar.add(Calendar.MONTH, 1)
            calendar.set(Calendar.DATE, 1)
            scrollPosition = 0
        }

        calendarViewModel.updateCurrentCalendar(calendar.time)
    }

    private fun updateCalendar(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<CalendarData>()

        for (i in 1 until lastDay + 1) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val calData = if (DateUtil.differDates(currentSelectDay, calendar.time)) {
                CalendarData(calendar.time, true)
            } else {
                CalendarData(calendar.time, false)
            }
            list.add(calData)
        }



        adapter!!.differ.submitList(list)

        updateCalendarUI(calendar)

        Handler(Looper.getMainLooper()).postDelayed( { scrollToCenter(scrollPosition) }, 100)

    }

    private fun updateUI(date: Date) {
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthStringFormat = SimpleDateFormat("M", Locale.getDefault())
        binding.monthText.text = monthStringFormat.format(date)
        binding.yearText.text = yearFormat.format(date)
    }

    private fun updateSelectedCalendar(date: Date) {
        adapter ?: return
        val list = adapter!!.differ.currentList.mapIndexed { _, calData ->
            val newItem = calData.copy(
                isSelected = DateUtil.differDates(calData.date, date),
                hasRecord = checkHasRecord(calData.date)
            )
            newItem
        }
        adapter!!.differ.submitList(list)

    }

    private fun updateRecord(list: List<Record>) {
        val recordList = mutableListOf<Record>()
        recordList.addAll(list)
        recordAdapter.differ.submitList(recordList)

    }

    private fun showBottomDialog(record: Record) {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_record_menu)

        val deleteText: TextView = dialog.findViewById(R.id.deleteText)
        val cancelText: TextView = dialog.findViewById(R.id.cancelText)

        cancelText.setOnClickListener {
            dialog.dismiss()
        }

        deleteText.setOnClickListener {
            showDeleteDialog(dialog, record)
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

    }

    private fun showDeleteDialog(bottomSheet: Dialog, record: Record) {
        val dialog = CustomDialog(requireContext())
        dialog.setOnClickListener(object : CustomDialog.OnClickEventListener{
            override fun onPositiveClick() {
                deleteRecord(record)
                Toast.makeText(requireContext(), getString(R.string.delete_record_complete), Toast.LENGTH_SHORT).show()
                bottomSheet.dismiss()
            }

            override fun onNegativeClick() {
            }
        })
        dialog.start(getString(R.string.delete_record_title), getString(R.string.delete_record_message), getString(R.string.delete), getString(R.string.cancel), true)
    }

    private fun deleteRecord(record: Record) {
        recordViewModel.deleteRecord(record)
    }

    private fun scrollToCenter(position: Int) {

        val layoutManager = binding.calendarRecyclerView.layoutManager as? LinearLayoutManager
        layoutManager ?: return
        val offset = layoutManager.width / 2 - layoutManager.width / layoutManager.itemCount / 2

        layoutManager.scrollToPositionWithOffset(position, offset)

    }
}