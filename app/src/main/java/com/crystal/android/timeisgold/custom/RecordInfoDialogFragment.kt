package com.crystal.android.timeisgold.custom

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.databinding.DialogRecordInfoFragmentBinding
import com.crystal.android.timeisgold.record.RecordViewModel
import com.crystal.android.timeisgold.util.CustomDialog
import com.crystal.android.timeisgold.util.UIUtil
import java.time.Duration
import java.util.*


class RecordInfoDialogFragment: DialogFragment() {

    private var _binding: DialogRecordInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private  var duration: Long = 0
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var memo: String

    private val recordViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RecordViewModel::class.java)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        private const val RECORD_DURATION = "record_duration"
        private const val RECORD_START_DATE = "record_start_date"
        private const val RECORD_END_DATE = "record_end_date"
        private const val RECORD_MEMO = "record_memo"

        fun newInstance(duration: Long, startDate: Date, endDate:Date, memo: String): DialogFragment {
            val fragment = RecordInfoDialogFragment()
            val args = Bundle()
            args.putLong(RECORD_DURATION, duration)
            args.putLong(RECORD_START_DATE, startDate.time)
            args.putLong(RECORD_END_DATE, endDate.time)
            args.putString(RECORD_MEMO, memo)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomFullDialog)

        duration = arguments?.getLong(RECORD_DURATION) ?: 0
        val argsStartDate = arguments?.getLong(RECORD_START_DATE) ?: 0
        val argsEndDate = arguments?.getLong(RECORD_END_DATE) ?: 0
        memo = arguments?.getString(RECORD_MEMO) ?: ""


        startDate =  Date(argsStartDate)
        endDate =  Date(argsEndDate)
    }

    override fun onStart() {
        super.onStart()

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogRecordInfoFragmentBinding.inflate(inflater, container, false)

        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setValues()
        setupEvents()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValues() {

        updateUI()

    }

    private fun setupEvents() {

        // 저장 버튼
        binding.saveButton.setOnClickListener {
/*        recordViewModel.addRecord(record)
        record.date = date!!
        record.durationTime = second
        recordViewModel.saveRecord(record)*/
        }

        binding.backButton.setOnClickListener {
            showDialog()
        }

        binding.layout.setOnClickListener {
            hideKeyboard()
        }

        binding.typeButton.setOnClickListener {

        }

    }

    private fun showDialog() {
        val dialog = CustomDialog(requireContext())
        dialog.setOnClickListener(object : CustomDialog.OnClickEventListener {
            override fun onPositiveClick() {
                dismiss()
            }

            override fun onNegativeClick() {
            }
        })
        dialog.start("뒤로가기", "확인을 클릭하시면 저장되지 않은채 뒤로 가집니다.", getString(R.string.ok), getString(R.string.cancel), true)
    }

    private fun updateUI() {

        binding.durationText.text = UIUtil.getDurationTime(duration)
        binding.startDateText.text = startDate.toString()
        binding.endDateText.text = endDate.toString()

        val breakTime = ((endDate.time - startDate.time ) / 1000) - duration

        if (breakTime > 0) {
            binding.breakText.text = UIUtil.getDurationTime(breakTime)
        } else {
            binding.breakText.text = getString(R.string.timer_notification_content, 0, 0, 0)
        }

        if (memo.isNotEmpty()) { binding.memoEditText.setText(memo) }
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.memoEditText.windowToken, 0)
    }

}