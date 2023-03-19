package com.crystal.android.timeisgold.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.crystal.android.timeisgold.R
import com.google.android.material.internal.ViewUtils.hideKeyboard

class TypeAdapter(
    private val context: Context
): RecyclerView.Adapter<TypeAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, type: String)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val typeNameTextView: TextView = itemView.findViewById(R.id.typeNameText)

        fun bind(string: String) {
            typeNameTextView.text = string

            itemView.setOnClickListener {
                listener?.onItemClick(itemView, string)
            }
        }

    }

    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.type_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = differ.currentList[position]
        holder.bind(string)
    }

    override fun getItemCount() = differ.currentList.size

}