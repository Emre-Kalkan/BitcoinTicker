package com.emrekalkan.bitcointicker.core

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.emrekalkan.bitcointicker.BR

abstract class BaseActivity<ViewModel : BaseViewModel, DataBinding : ViewDataBinding> : AppCompatActivity() {

    abstract val viewModel: ViewModel

    val binding: DataBinding by lazy {
        DataBindingUtil.setContentView(this, getLayoutRes())
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.setVariable(BR.viewModel, viewModel)
    }
}