package com.crystal.timeisgold.util

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.widget.TextView
import com.crystal.timeisgold.R

class CustomDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var titleTextView: TextView
    private lateinit var messageTextView: TextView
    private lateinit var negativeTextView: TextView
    private lateinit var positiveTextView: TextView
    private  var listener: OnClickEventListener? = null

    fun start(title: String?, message: String, positiveText: String, negativeText: String?, isCanceled: Boolean) {
        dialog.setTitle(title)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_custom)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(isCanceled)

        titleTextView = dialog.findViewById(R.id.title_text_view)
        messageTextView = dialog.findViewById(R.id.message_text_view)
        negativeTextView = dialog.findViewById(R.id.negative_text_view)
        positiveTextView = dialog.findViewById(R.id.positive_text_view)

        messageTextView.text = message
        positiveTextView.text = positiveText

        if (title != null) {
            titleTextView.text = title
        } else {
            titleTextView.visibility = View.GONE
        }

        if (negativeText != null) {
            negativeTextView.text = negativeText
        } else {
            negativeTextView.visibility = View.GONE
        }

        positiveTextView.setOnClickListener {
            listener?.onPositiveClick()
            dialog.dismiss()
        }

        negativeTextView.setOnClickListener {
            listener?.onNegativeClick()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun setOnClickListener(listener: OnClickEventListener) {
        this.listener = listener
    }


    interface OnClickEventListener {
        fun onPositiveClick()
        fun onNegativeClick()
    }
}