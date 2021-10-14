package com.argahutama.news.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.argahutama.news.common.R
import com.argahutama.news.common.navigation.NavigationDirection

abstract class BaseDialogFragment<SuccessState> : DialogFragment() {
    abstract val binding: ViewBinding

    open val viewModel: BaseViewModel<SuccessState>? = null

    open val dialogStyle = R.style.DefaultDialog

    abstract fun setup()
    abstract fun loadArguments()

    open fun render(state: SuccessState) {}
    open fun setupDialogStyle(dialog: Dialog) {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setupDialogStyle(dialog)
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, dialogStyle)
        loadArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.viewState?.observe(this, {
            if (it is BaseViewState.Success) it.data?.let { state -> render(state) }
        })
        setup()
    }

    fun getBaseActivity(): BaseActivity<*>? = if (requireActivity() is BaseActivity<*>) {
        requireActivity() as BaseActivity<*>
    } else null

    override fun dismiss() {
        if (isStateSaved) {
            dismissAllowingStateLoss()
        } else {
            super.dismiss()
        }
    }

    private fun getBaseApp() = getBaseActivity()?.getBaseApp()

    fun navigateTo(direction: NavigationDirection) =
        getBaseApp()?.navigateTo(requireContext(), direction)
    fun navigateTo(direction: NavigationDirection, requestCode: Int) =
        getBaseApp()?.navigateTo(requireActivity(), direction, requestCode)
}