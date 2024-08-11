package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.urbannavigator.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(imageUrl: ArrayList<String>) :
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

    var sliderList: ArrayList<String> = imageUrl

    override fun getCount(): Int {
        return sliderList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapter.SliderViewHolder {
        val inflate: View =
            LayoutInflater.from(parent!!.context).inflate(R.layout.banner_slider_item, null)
        return SliderViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapter.SliderViewHolder?, position: Int) {

        if (viewHolder != null) {
            Glide.with(viewHolder.itemView).load(sliderList.get(position)).override(800).skipMemoryCache(true).diskCacheStrategy(
                DiskCacheStrategy.DATA)
                .centerCrop()
                .into(viewHolder.imageView)
        }
    }

    class SliderViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {

        var imageView: ImageView = itemView!!.findViewById(R.id.myimage)
    }
}