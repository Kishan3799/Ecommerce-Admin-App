package com.kishan.ecommerceadminapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.databinding.ItemCategoryLayoutBinding
import com.kishan.ecommerceadminapp.model.CategoryModel

class CategoryAdapter(private val context:Context, private val list: ArrayList<CategoryModel>) :RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val binding = ItemCategoryLayoutBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return  CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.categoryItemName.text = list[position].category
        Glide.with(context).load(list[position].img).into(holder.binding.categoryItemImage)
    }
}