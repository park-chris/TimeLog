package com.crystal.android.timeisgold.timer

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.databinding.FragmentTimerBinding
import com.crystal.android.timeisgold.util.CustomDialog
import com.crystal.android.timeisgold.util.ServiceUtil
import com.crystal.android.timeisgold.util.UIUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.StringBuilder
import java.sql.Time

private const val TAG = "TimerFragment"


class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var timerAnimation: ObjectAnimator
    private var isPlaying = false
    private var second: Long = 0

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                TimerService.ACTION_CLOSE -> {
                    reset()
                }
                TimerService.ACTION_SAVE -> {
                }
                TimerService.ACTION_UPDATE -> {
                    second = intent.getLongExtra(TimerService.TIMER_VALUE, 0)
                    updateUI()
                }
                TimerService.ACTION_PAUSE -> {
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("playing", isPlaying)
        outState.putLong(TimerService.TIMER_VALUE, second)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver()

        second = savedInstanceState?.getLong(TimerService.TIMER_VALUE) ?: 0
        isPlaying = savedInstanceState?.getBoolean("isPlaying", false) ?: false

    }

    override fun onDestroy() {
        super.onDestroy()

        requireActivity().applicationContext.unregisterReceiver(receiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        setValues()

        if (second != 0L) {
            binding.timerText.text = UIUtil.getDurationTime(second)
        }
        if (isPlaying) {
            binding.operatorButton.setImageResource(R.drawable.ic_pause)
            timerAnimation.start()
        }

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

        timerAnimation = ObjectAnimator.ofFloat(binding.timerProgress, "rotation", 0f, 360f)
        timerAnimation.duration = 1000
        timerAnimation.interpolator = LinearInterpolator()
        timerAnimation.repeatCount = Animation.INFINITE

        binding.operatorButton.setOnClickListener {
            if (isPlaying) {
                pause()
            } else {
                start()
            }
        }

        binding.resetButton.setOnClickListener {
            showDialog()
        }

    }

    private fun setupEvents() {


    }

    private fun reset() {
        val intent = Intent(requireContext().applicationContext, TimerService::class.java)
        timerAnimation.cancel()
        requireActivity().stopService(intent)
        binding.operatorButton.setImageResource(R.drawable.ic_play)
        binding.timerText.text = "00:00:00"
        second = 0
        isPlaying = false
    }

    private fun start() {

        if (ServiceUtil.isServiceRunning(requireContext(), TimerService::class.java)) {
            val intent = Intent(TimerService.ACTION_START)
            requireActivity().sendBroadcast(intent)
        } else {
            val intent = Intent(requireContext().applicationContext, TimerService::class.java)
            requireActivity().startForegroundService(intent)
        }
            binding.operatorButton.setImageResource(R.drawable.ic_pause)
            timerAnimation.start()
            isPlaying = true
    }

    private fun pause() {
        val serviceToMainIntent = Intent(TimerService.ACTION_PAUSE)
        requireActivity().sendBroadcast(serviceToMainIntent)
        timerAnimation.pause()
        binding.operatorButton.setImageResource(R.drawable.ic_play)

        isPlaying = false
    }

    private fun showDialog() {
        val dialog = CustomDialog(requireContext())
        dialog.setOnClickListener(object : CustomDialog.OnClickEventListener {
            override fun onPositiveClick() {
                reset()
            }

            override fun onNegativeClick() {
            }
        })

        dialog.start(getString(R.string.timer_reset_title), getString(R.string.timer_reset_message), getString(R.string.ok),  getString(R.string.cancel), true)

    }

    private fun updateUI() {

        val fragment = try {
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager!!.fragments[0]
        } catch (e: NullPointerException) {
            null
        }
        if (fragment is TimerFragment) {
            binding.timerText.text = UIUtil.getDurationTime(second)
        }
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(TimerService.ACTION_CLOSE)
        intentFilter.addAction(TimerService.ACTION_SAVE)
        intentFilter.addAction(TimerService.ACTION_UPDATE)
        intentFilter.addAction(TimerService.ACTION_PAUSE)
        intentFilter.addAction(TimerService.ACTION_START)
        requireActivity().applicationContext.registerReceiver(receiver, intentFilter)
    }
}