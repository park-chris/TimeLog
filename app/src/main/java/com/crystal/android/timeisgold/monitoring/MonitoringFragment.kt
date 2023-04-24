package com.crystal.android.timeisgold.monitoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.databinding.FragmentMonitoringBinding
import com.crystal.android.timeisgold.record.RecordViewModel
import com.crystal.android.timeisgold.util.ContextUtil

class MonitoringFragment: Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

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
        setupEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValues() {

        val itemList = ContextUtil.getTypeListPref(requireContext())
        recordViewModel.recordDailyLiveData.observe(viewLifecycleOwner) {
            it?.let {
              //   updateUI(itemList, it)
            }
        }

    }

    private fun setupEvents() {

    }


}