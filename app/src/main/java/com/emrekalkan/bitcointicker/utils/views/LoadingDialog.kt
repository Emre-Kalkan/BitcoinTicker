package com.emrekalkan.bitcointicker.utils.views

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.emrekalkan.bitcointicker.R

class LoadingDialog : DialogFragment() {

    companion object {
        const val TAG = "LoadingDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_loading)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}