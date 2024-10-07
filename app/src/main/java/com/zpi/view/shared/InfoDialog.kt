package com.zpi.view.shared

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zpi.R

class InfoDialog(private val context: Context, private val layoutInflater: LayoutInflater) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)

        dialogTitle.findViewById<TextView>(R.id.textView).text = context.getString(R.string.ai_analysis_info_label)
        dialogLayout.findViewById<TextView>(R.id.textView).text = context.getString(R.string.ai_analysis_info)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(context.getString(R.string.close)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val closeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}