package com.crystal.timeisgold.history

import android.app.DatePickerDialog
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
import com.crystal.timeisgold.custom.DialogType
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
//    private var currentCalendarDate: Date = Date()
//    private var currentSelectDay: Date = Date()
    private var scrollPosition = 0
//    private var calendarValidRecords = emptyList<Date>()

    private var calendarDate: Date = Date()
    private var selectedDate: Date = Date()
    private var recordDates = emptyList<Date>()
    private var calendarDates = emptyList<CalendarData>()

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
                Log.e(TAG, "currentCalendarDate $it")
                calendarDate = it
                selectedDate = it
                updateCalendar(it)
//                updateSelectedCalendar(it)
                updateUI(it)
            }
        }

        calendarViewModel.selectDay.observe(viewLifecycleOwner) {
            it?.let {
                Log.e(TAG, "currentSelectDay $it")

                selectedDate = it
//                updateSelectedCalendar(it)
                updateCalendar(it)
                updateUI(it)
                recordViewModel.loadRecords(it)
            }
        }

        recordViewModel.selectedRecordsLiveData.observe(viewLifecycleOwner) {
            Log.e("TestLog", "updateRecord list $it")
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
            selectedDate = calendar.time
            scrollPosition = calendar.get(Calendar.DAY_OF_MONTH)


//            calendarViewModel.updateCurrentSelect(calendar.time)
            calendarViewModel.updateCurrentCalendar(calendar.time)
        }

        binding.prevButton.setOnClickListener {
            updateDate(calendarDate, previous = true, next = false)
        }

        binding.nextButton.setOnClickListener {
            updateDate(calendarDate, previous = false, next = true)
        }

        binding.yearMonthLayout.setOnClickListener {
            showDatePicker { date ->
                calendarViewModel.updateCurrentSelect(date)
//                calendarViewModel.updateCurrentCalendar(date)

                scrollPosition = selectedDate.date
                Log.e(TAG, "scrollToposition $scrollPosition")
                Handler(Looper.getMainLooper()).postDelayed( { scrollToCenter(scrollPosition) }, 100)

                recordViewModel.loadRecords(date)
            }
        }

    }
    private fun showDatePicker(onDateSelected: (date: Date) -> Unit) {
        val calendar = Calendar.getInstance().apply {
            time = selectedDate
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                onDateSelected(selectedCalendar.time)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    private fun initCalendar() {

        adapter = CalendarAdapter(requireContext()) {date ->
            calendarViewModel.updateCurrentSelect(date)
        }
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
                        updateDate(calendarDate, previous = true, next = false)
                    }

                    startLastClickedTime = System.currentTimeMillis()
                }

                if (endDetector == 0 && newState == 0) {

                    val interval = System.currentTimeMillis() - endLastClickedTime
                    if (interval < 1000) {
                        updateDate(calendarDate, previous = false, next = true)
                    }

                    endLastClickedTime = System.currentTimeMillis()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }

        })

        val calendar = Calendar.getInstance()

        selectedDate = calendar.time
        calendarViewModel.updateCurrentCalendar(calendar.time)
    }

    private fun checkHasRecord(date: Date): Boolean {
        val filterDate = recordDates.filter { d ->
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
                val dialog = RecordInfoDialogFragment.newInstance(id, DialogType.Edit)
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
            val calData = if (DateUtil.differDates(selectedDate, calendar.time)) {
                CalendarData(calendar.time, true)
            } else {
                CalendarData(calendar.time, false)
            }
            list.add(calData)
        }

        adapter!!.differ.submitList(list)

        val dates = mutableListOf<CalendarData>()

        CoroutineScope(Dispatchers.Main).launch {

            recordDates = getMonthRecordDates(calendar.time)

            for (i in 1 until lastDay + 1) {
                calendar.set(Calendar.DAY_OF_MONTH, i)
                val calData = if (DateUtil.differDates(selectedDate, calendar.time)) {
                    CalendarData(calendar.time, true, checkHasRecord(calendar.time))
                } else {
                    CalendarData(calendar.time, false)
                }
                dates.add(calData)
            }

            adapter!!.differ.submitList(dates)
        }

        scrollPosition = date.date

        Log.e(TAG," updateCalender scollToPosition $scrollPosition")
        Handler(Looper.getMainLooper()).postDelayed( { scrollToCenter(scrollPosition) }, 200)

    }

    private fun updateUI(date: Date) {
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthStringFormat = SimpleDateFormat("M", Locale.getDefault())
        binding.monthText.text = monthStringFormat.format(date)
        binding.yearText.text = yearFormat.format(date)
    }

    private fun updateSelectedCalendar(date: Date) {
        adapter ?: return

        val calendar = Calendar.getInstance()
        calendar.time = date

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val list = mutableListOf<CalendarData>()

        for (i in 1 until lastDay + 1) {
            calendar.set(Calendar.DAY_OF_MONTH, i)
            val calData = if (DateUtil.differDates(selectedDate, calendar.time)) {
                CalendarData(calendar.time, true, checkHasRecord(calendar.time))
            } else {
                CalendarData(calendar.time, false)
            }
            list.add(calData)
        }

        adapter!!.differ.submitList(list)

        scrollPosition = selectedDate.date
        Log.e(TAG, "scrollToposition $scrollPosition")
        Handler(Looper.getMainLooper()).postDelayed( { scrollToCenter(scrollPosition) }, 100)

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