package com.example.m19_location.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m19_location.data.SinglePhoto
import com.example.m19_location.databinding.MyViewGroupBinding
import java.text.SimpleDateFormat
import java.util.Locale


class PhotosAdapter(private val values: List<SinglePhoto?>) :

    RecyclerView.Adapter<PhotosViewHolder>() {

    companion object{
        private const val FILENAME_FORMAT = "yyyy-MM-dd"

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = MyViewGroupBinding.inflate(LayoutInflater.from(parent.context))
        return PhotosViewHolder(binding)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val items = values[position]

        holder.binding.topText.text =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val context = holder.binding.imageView.context

        Glide.with(context).load(items?.savedUri.toString()).into(holder.binding.imageView)

    }
}

class PhotosViewHolder(val binding: MyViewGroupBinding) : RecyclerView.ViewHolder(binding.root)