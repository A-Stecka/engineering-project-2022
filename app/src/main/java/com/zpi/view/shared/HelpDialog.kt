package com.zpi.view.shared

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zpi.R

class HelpDialog(private val context: Context, private val layoutInflater: LayoutInflater) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
        val layoutTextView: TextView = dialogLayout.findViewById(R.id.textView)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        val titleTextView: TextView = dialogTitle.findViewById(R.id.textView)
        var currentDialog = 0

        layoutTextView.text = context.getString(R.string.help_dialog_1)
        titleTextView.text = context.getString(R.string.help_dialog_1_label)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(context.getString(R.string.next)) { _, _ -> }
            setNegativeButton(context.getString(R.string.previous)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val nextButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val previousButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            previousButton.visibility = View.GONE

            nextButton.setOnClickListener {
                when (currentDialog) {
                    0 -> {
                        previousButton.visibility = View.VISIBLE
                        layoutTextView.text = context.getString(R.string.help_dialog_2)
                        titleTextView.text = context.getString(R.string.help_dialog_2_label)
                    }
                    1 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_3)
                        titleTextView.text = context.getString(R.string.help_dialog_3_label)
                    }
                    2 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_4)
                        titleTextView.text = context.getString(R.string.help_dialog_4_label)
                    }
                    3 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_5)
                        titleTextView.text = context.getString(R.string.help_dialog_5_label)
                    }
                    4 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_6)
                        titleTextView.text = context.getString(R.string.help_dialog_6_label)
                        nextButton.text = context.getText(R.string.close)
                    }
                    else -> dialog.dismiss()
                }
                currentDialog++
            }

            previousButton.setOnClickListener {
                when (currentDialog) {
                    1 -> {
                        previousButton.visibility = View.GONE
                        layoutTextView.text = context.getString(R.string.help_dialog_1)
                        titleTextView.text = context.getString(R.string.help_dialog_1_label)
                    }
                    2 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_2)
                        titleTextView.text = context.getString(R.string.help_dialog_2_label)
                    }
                    3 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_3)
                        titleTextView.text = context.getString(R.string.help_dialog_3_label)
                    }
                    4 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_4)
                        titleTextView.text = context.getString(R.string.help_dialog_4_label)
                    }
                    5 -> {
                        layoutTextView.text = context.getString(R.string.help_dialog_5)
                        titleTextView.text = context.getString(R.string.help_dialog_5_label)
                        nextButton.text = context.getText(R.string.next)
                    }
                }
                currentDialog--
            }
        }

        dialog.show()
    }

}