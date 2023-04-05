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
    private val context: Context
) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    interface SetOnItemClickListener {
        fun onSelectMenu(record: Record)
        fun onClickItem(id: UUID)
    }

    private var listener: SetOnItemClickListener? = null

    fun setOnItemClickListener(listener: SetOnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: RecordListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Record) {
            binding.titleText.text = record.type
            if (record.memo.isEmpty()) {
                binding.contentText.text = context.getString(R.string.no_memo)
            } else {
                binding.contentText.text = record.memo
            }
            binding.startTimeText.text = DateUtil.dateToTimeString(record.startDate)
            binding.durationTimeText.text = DateUtil.longToDurationTime(record.durationTime)

            binding.menuButton.setOnClickListener {
                listener?.onSelectMenu(record)
            }
            itemView.setOnClickListener {
                listener?.onClickItem(record.id)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = RecordListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

}