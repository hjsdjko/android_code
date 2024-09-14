package com.design.appproject.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.design.appproject.R
import com.design.appproject.ext.load
import com.youth.banner.adapter.BannerAdapter

class DetailBannerAdapter(list: List<String>):BannerAdapter<String, RecyclerView.ViewHolder>(list) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object:RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.banner_image_layout_detail, parent, false)){}
    }

    override fun onBindView(holder: RecyclerView.ViewHolder, data: String, position: Int, size: Int) {
        holder.itemView.findViewById<ImageView>(R.id.banner_iv).load(holder.itemView.context,data, needPrefix = !data.startsWith("http"))
    }
}