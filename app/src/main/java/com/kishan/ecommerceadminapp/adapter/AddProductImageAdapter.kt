package com.kishan.ecommerceadminapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kishan.ecommerceadminapp.databinding.ProductImageItemBinding
import com.kishan.ecommerceadminapp.model.AddProductModel

class AddProductImageAdapter(private val list: ArrayList<Uri>) :RecyclerView.Adapter<AddProductImageAdapter.AddProductImageViewHolder>() {

    inner class AddProductImageViewHolder(val binding:ProductImageItemBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddProductImageViewHolder {
        val binding = ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddProductImageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AddProductImageViewHolder, position: Int) {
        holder.binding.imageView.setImageURI(list[position])
    }

}