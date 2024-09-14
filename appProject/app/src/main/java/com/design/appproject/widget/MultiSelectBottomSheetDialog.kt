package com.design.appproject.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.design.appproject.R
import com.google.android.material.bottomsheet.BottomSheetDialog

/**底部多选弹窗*/
class MultiSelectBottomSheetDialog(
    context: Context,
    title: String,
    private val items: List<String>,
    private val onItemsSelected: (List<String>) -> Unit
) {

    private val dialogView: View =
        LayoutInflater.from(context).inflate(R.layout.dialog_multi_select, null)
    private val alertDialog: BottomSheetDialog = BottomSheetDialog(context)
    private val selectedItems = mutableSetOf<String>()

    init {
        alertDialog.setContentView(dialogView)
        dialogView.findViewById<TextView>(R.id.dialog_title).text = title
        dialogView.findViewById<LinearLayout>(R.id.dialog_list_tv).apply {
            items.forEach { item ->
                val checkBox = CheckBox(context, null).apply {
                    text = item
                    setOnClickListener { view ->
                        val isChecked = (view as CheckBox).isChecked
                        if (isChecked) {
                            selectedItems.add(item)
                        } else {
                            selectedItems.remove(item)
                        }
                    }
                }
                addView(checkBox)
            }
        }
        dialogView.findViewById<View>(R.id.btn_done).setOnClickListener {
            onItemsSelected(selectedItems.toList())
            alertDialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun show() {
        alertDialog.show()
    }
}
