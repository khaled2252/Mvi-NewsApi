package com.khaled.newsmvi.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?) {
    Picasso.get()
            .load(imageUrl)
            .fit()
            .into(imageView, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    imageView.setImageResource(com.khaled.newsmvi.R.drawable.no_image)
                }
            })
}