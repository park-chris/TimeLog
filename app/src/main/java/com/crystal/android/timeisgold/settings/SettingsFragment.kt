package com.crystal.android.timeisgold.settings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.crystal.android.timeisgold.BuildConfig
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.databinding.FragmentSettingsBinding
import com.crystal.android.timeisgold.util.ContextUtil
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater,  container, false)

        binding.currentVersionTextView.text = getAppVersion()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.typeTextView.setOnClickListener {
            showModalBottomSheet()
        }

        binding.askTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            val address = arrayOf(getString(R.string.email_email))
            intent.putExtra(Intent.EXTRA_EMAIL, address)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_content))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }



    private fun showModalBottomSheet() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheet_type)

        val typeEditText: EditText = dialog.findViewById(R.id.typeEditText)
        val addTypeButton: ImageButton = dialog.findViewById(R.id.addTypeButton)
        val chipGroup: ChipGroup = dialog.findViewById(R.id.chipGroup)

        val typeList = ContextUtil.getTypeListPref(requireContext())

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