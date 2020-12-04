package com.emrekalkan.bitcointicker.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.emrekalkan.bitcointicker.R

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("coinImage")
    fun fetchCoinImage(view: ImageView, url: String?) {
        if (url?.isNotBlank() == true) {
            Glide.with(view).load(url).placeholder(R.drawable.ic_launcher_foreground).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("htmlString")
    fun getHtmlContainsString(textView: TextView, source: String?) {
        textView.text = if (source?.isNotBlank() == true) {
            HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            ""
        }
    }
}