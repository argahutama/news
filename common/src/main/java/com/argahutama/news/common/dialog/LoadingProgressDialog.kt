package com.argahutama.news.common.dialog

import android.app.Dialog
import com.argahutama.news.common.R
import com.argahutama.news.common.base.BaseDialogFragment
import com.argahutama.news.common.databinding.DialogLoadingBinding

class LoadingProgressDialog : BaseDialogFragment<Nothing>() {
    override val binding by lazy { DialogLoadingBinding.inflate(layoutInflater) }

    override fun setup() {}
    override fun setupDialogStyle(dialog: Dialog) {
        isCancelable = false
    }

    override fun loadArguments() {
        setStyle(STYLE_NORMAL, R.style.DefaultDialog)
    }

    companion object {
        fun newInstance() = LoadingProgressDialog()
    }
}