package com.crystal.android.timeisgold.history

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crystal.android.timeisgold.R
import com.crystal.android.timeisgold.data.CalendarData
import com.crystal.android.timeisgold.data.Record
import com.crystal.android.timeisgold.databinding.RecordListItemBinding
import com.crystal.android.timeisgold.util.DateUtil
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter(
    private val context: Context,
    private val list: List<Record>
): RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RecordListItemBinding ): RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Record) {
            itemView.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RecordListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}