package com.emrekalkan.bitcointicker.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.emrekalkan.bitcointicker.BR
import com.emrekalkan.bitcointicker.ui.MainActivity
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<ViewModel : BaseViewModel, DataBinding : ViewDataBinding> : Fragment() {

    lateinit var binding: DataBinding

    abstract val viewModel: ViewModel

    @LayoutRes
    abstract fun getLayoutRes(): Int
    abstract fun init(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, getLayoutRes(), container, false)
        binding.setVariable(BR.viewModel, viewModel)
        init(savedInstanceState)

        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    fun updateLoadingDialog(isLoading: Boolean) {
        (requireActivity() as MainActivity).updateLoadingDialog(isLoading)
    }

    fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        (requireActivity() as MainActivity).showSnackBar(message, duration)
    }
}