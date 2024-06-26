package com.crystal.timeisgold.custom

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.crystal.timeisgold.R
import com.crystal.timeisgold.data.Record
import com.crystal.timeisgold.databinding.DialogRecordInfoFragmentBinding
import com.crystal.timeisgold.record.RecordViewModel
import com.crystal.timeisgold.util.ContextUtil
import com.crystal.timeisgold.util.CustomDialog
import com.crystal.timeisgold.util.DateUtil
import com.crystal.timeisgold.util.UIUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.internal.ViewUtils.hideKeyboard
import java.util.*
import kotlin.collections.ArrayList

enum class DialogType {
    New, Edit
}
class RecordInfoDialogFragment(
    private val type: DialogType = DialogType.New
) : DialogFragment() {

    private var _binding: DialogRecordInfoFragmentBinding? = null
    private val binding get() = _binding!!
    private var duration: Long = 0
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var memo: String
    private lateinit var recordId: String
    private var isNew = false
    private var typeList: ArrayList<String> = arrayListOf()
    private var typeIsSelected = false
    private var isEdit = true
    private var modified = false

    private val recordViewModel by lazy {
        ViewModelProvider(requireActivity())[RecordViewModel::class.java]
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    companion object {
        private const val RECORD_DURATION = "record_duration"
        private const val RECORD_START_DATE = "record_start_date"
        private const val RECORD_END_DATE = "record_end_date"
        private const val RECORD_MEMO = "record_memo"
        private const val RECORD_UID = "record_uid"
        private const val NEW_RECORD = "new_record"

        fun newInstance(
            duration: Long,
            startDate: Date,
            endDate: Date,
            memo: String
        ): DialogFragment {
            val fragment = RecordInfoDialogFragment()
            val args = Bundle()
            args.putLong(RECORD_DURATION, duration)
            args.putLong(RECORD_START_DATE, startDate.time)
            args.putLong(RECORD_END_DATE, endDate.time)
            args.putString(RECORD_MEMO, memo)
            args.putBoolean(NEW_RECORD, true)
            fragment.arguments = args

            return fragment
        }

        fun newInstance(recordUid: UUID, type: DialogType): DialogFragment {
            val fragment = RecordInfoDialogFragment(type)
            val args = Bundle()
            args.putString(RECORD_UID, recordUid.toString())
            args.putBoolean(NEW_RECORD, false)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onPause() {
        super.onPause()

        modified = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomFullDialog)

        recordId = arguments?.getString(RECORD_UID) ?: ""

        isNew = arguments?.getBoolean(NEW_RECORD) ?: false

        if (isNew) {
            duration = arguments?.getLong(RECORD_DURATION) ?: 0
            val argsStartDate = arguments?.getLong(RECORD_START_DATE) ?: 0
            val argsEndDate = arguments?.getLong(RECORD_END_DATE) ?: 0
            memo = arguments?.getString(RECORD_MEMO) ?: ""

            startDate = Date(argsStartDate)
            endDate = Date(argsEndDate)
        } else {
            recordViewModel.loadRecord(UUID.fromString(recordId))
        }

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

        binding.type = type
        if (type == DialogType.Edit)  isEdit = false
        binding.isEdit = isEdit

        dialog?.window?.setBackgroundDrawableResource(R.color.dialog_background)
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

        if (isNew) {
            updateUI()
        } else {
            recordViewModel.recordLiveDate.observe(viewLifecycleOwner) {
                it?.let {
                    duration = it.durationTime
                    memo = it.memo
                    startDate = it.startDate
                    endDate = it.endDate
                    binding.typeButton.text = it.type
                    typeIsSelected = true
                    updateUI()
                }
            }
        }


    }

    private fun setupEvents() {

        // 저장 버튼
        binding.saveButton.setOnClickListener {saveRecord() }
        binding.backButton.setOnClickListener {showDialog()}
        binding.layout.setOnClickListener {hideKeyboard() }
        binding.typeButton.setOnClickListener {showModalBottomSheet()}
        binding.editButton.setOnClickListener {
            isEdit = !isEdit
            binding.isEdit = isEdit
        }

    }

    private fun saveRecord() {
        val record = Record()

        memo = binding.memoEditText.text.toString()

        if (!typeIsSelected) {
            Toast.makeText(requireContext(), getString(R.string.choice_type), Toast.LENGTH_SHORT)
                .show()
            return
        }
        val type = binding.typeButton.text.toString()

        record.durationTime = duration
        record.startDate = startDate
        record.endDate = endDate
        record.memo = memo
        record.type = type

        if (recordId.isNotEmpty()) {
            record.id = UUID.fromString(recordId)
            recordViewModel.updateRecord(record)
        } else {
            recordViewModel.addRecord(record)
            dismiss()
        }

        Toast.makeText(requireContext(), getString(R.string.save_memo), Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {

        if (memo != binding.memoEditText.text.toString()) {
            val dialog = CustomDialog(requireContext())
            dialog.setOnClickListener(object : CustomDialog.OnClickEventListener {
                override fun onPositiveClick() {
                    dismiss()
                }

                override fun onNegativeClick() {
                }
            })
            dialog.start(
                getString(R.string.back_title),
                getString(R.string.back_message),
                getString(R.string.ok),
                getString(R.string.cancel),
                true
            )
        } else {
            dismiss()
        }
    }

    private fun updateUI() {

        binding.durationText.text = UIUtil.getDurationTime(duration)
        binding.startDateText.text = DateUtil.dateToString(startDate)
        binding.endDateText.text = DateUtil.dateToString(endDate)

        if (memo.isNotEmpty()) {
            binding.memoEditText.setText(memo)
        }
    }
    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.memoEditText.windowToken, 0)
    }

    private fun showModalBottomSheet() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_type)

        val typeEditText: EditText = dialog.findViewById(R.id.typeEditText)
        val addTypeButton: ImageButton = dialog.findViewById(R.id.addTypeButton)
        val chipGroup: ChipGroup = dialog.findViewById(R.id.chipGroup)

        typeList = ContextUtil.getTypeListPref(requireContext())

        if (typeList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.no_list), Toast.LENGTH_SHORT).show()
        } else {
            for (string in typeList) {
                val chip = Chip(requireContext()).apply {
                    text = string
                    isCloseIconVisible = true
                    setOnCloseIconClickListener {
                        chipGroup.removeView(this)
                        typeList.remove(text)
                        ContextUtil.setTypeListPref(requireContext(), typeList)
                    }
                    setOnClickListener {
                        binding.typeButton.text = text
                        typeIsSelected = true
                        dialog.dismiss()
                    }
                }
                chipGroup.addView(chip)
                chipGroup.invalidate()
            }

        }

        addTypeButton.setOnClickListener {
            val inputString = typeEditText.text.toString()

            if (inputString.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.type_edit_text_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (typeList.contains(inputString)) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.type_add_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    typeList.add(0, inputString)

                    val chip = Chip(requireContext()).apply {
                        text = inputString
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            chipGroup.removeView(this)
                        }
                        setOnClickListener {
                            binding.typeButton.text = text
                            typeIsSelected = true
                            dialog.dismiss()
                        }
                    }

                    ContextUtil.setTypeListPref(requireContext(), typeList)
                    chipGroup.addView(chip, 0)
                    chipGroup.invalidate()

                }
            }
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


}