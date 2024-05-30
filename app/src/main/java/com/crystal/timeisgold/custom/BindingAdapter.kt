package com.crystal.timeisgold.custom

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.crystal.timeisgold.R


@BindingAdapter("editImage")
fun ImageButton.setEditImage(isEdit: Boolean?) {
    isEdit?.let {
        if (it) {
            setImageResource(R.drawable.ic_eye)
        } else {
            setImageResource(R.drawable.ic_edit)
        }
    }
}

@BindingAdapter("visible")
fun View.setVisible(type: DialogType) {
    visibility = if (type == DialogType.New) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("editStatus")
fun EditText.setEditStatus(isEdit: Boolean) {

    if (isEdit) {
        isFocusableInTouchMode = true
        isFocusable = true
    } else {
        isClickable = false
        isFocusable = false
    }

//    inputType = if (isEdit) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
//    else InputType.TYPE_NULL
//    isVerticalScrollBarEnabled = true
//    setHorizontallyScrolling(false)
//    maxLines = Integer.MAX_VALUE
//    isFocusable = true
//    isFocusableInTouchMode = true
}
