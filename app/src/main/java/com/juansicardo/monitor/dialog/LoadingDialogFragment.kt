package com.juansicardo.monitor.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.juansicardo.monitor.R

class LoadingDialogFragment(private val owner: AppCompatActivity) : DialogFragment() {

    init {
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).apply {
                this.setView(requireActivity().layoutInflater.inflate(R.layout.dialog_loading, null))
                this.setCancelable(false)
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun show() {
        show(owner.supportFragmentManager, "loading")
    }
}