package com.crystal.android.timeisgoldre.timer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.crystal.android.timeisgoldre.R
import com.crystal.android.timeisgoldre.databinding.FragmentTimerBinding

private const val TAG = "TimerFragment"

class TimerFragment: Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        Log.d(TAG, "onCreatView")
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

    }

    private fun setupEvents() {

        binding.textView.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "onClicked",
                Toast.LENGTH_SHORT
            ).show()

            Log.d(TAG, "timerfragment onClicked")
        }


    }

}