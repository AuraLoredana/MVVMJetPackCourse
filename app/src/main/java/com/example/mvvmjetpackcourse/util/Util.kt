package com.example.mvvmjetpackcourse.util

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mvvmjetpackcourse.R

val PERMISSION_SEND_SMS_REQUEST_CODE = 1234
fun getProgressDrawable(context: Context?): CircularProgressDrawable? {
    return context?.let {
        CircularProgressDrawable(it).apply {
            strokeWidth = 10f
            centerRadius = 50f
        }
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions.placeholderOf(progressDrawable).error(R.mipmap.ic_launcher)
    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    getProgressDrawable(view.context)?.let { view.loadImage(url, it) }
}