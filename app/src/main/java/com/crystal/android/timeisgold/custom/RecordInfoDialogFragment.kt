package com.crystal.android.timeisgold.custom

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.databinding.DialogRecordInfoFragmentBinding
import com.crystal.android.timeisgold.record.RecordViewModel
import java.sql.Date


class RecordInfoDialogFragment: DialogFragment() {

    private var _binding: DialogRecordInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private  var duration: Long = 0
    private lateinit var date: Date

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
        private const val RECORD_DATE = "record_date"

        fun newInstance(duration: Long, date: Date): DialogFragment {
            val fragment = RecordInfoDialogFragment()
            val args = Bundle()
            args.putLong(RECORD_DURATION, duration)
            args.putLong(RECORD_DATE, date.time)

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomFullDialog)

        duration = arguments?.getLong(RECORD_DURATION) ?: 0
        val argsDate = arguments?.getLong(RECORD_DATE) ?: 0
        date = Date(argsDate)
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



    }

    private fun setupEvents() {

    }


}