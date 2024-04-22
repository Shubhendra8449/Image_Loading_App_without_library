package com.imageloadingapp.presentation.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imageloadingapp.R
import com.imageloadingapp.databinding.ItemLayoutBinding
import com.imageloadingapp.domain.model.ImageModelItem
import com.imageloadingapp.utils.ImageLoader
import javax.inject.Inject


class ImageLoadAdapter @Inject constructor() :
    RecyclerView.Adapter<ImageLoadAdapter.ViewHolder>() {

    private var imageList = ArrayList<ImageModelItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun setImage(imageModelItem: ArrayList<ImageModelItem>){
        imageList=imageModelItem
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            ImageLoader.with(binding.imageView.context).load(binding.imageView, url,
                R.drawable.image_placeholder
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position].coverageURL)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}
