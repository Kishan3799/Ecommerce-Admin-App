package com.kishan.ecommerceadminapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kishan.ecommerceadminapp.databinding.ProductItemBinding
import com.kishan.ecommerceadminapp.model.ProductDetailModel

class ProductDetailAdapter(private val context:Context,private val list: ArrayList<ProductDetailModel>):RecyclerView.Adapter<ProductDetailAdapter.ProductDetailViewHolder>() {

    inner class ProductDetailViewHolder(val binding:ProductItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductDetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
         return list.size
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val data = list[position]

        holder.binding.productName.text = data.productName
        holder.binding.productSellingPrice.text = data.productSp
        holder.binding.productMarketSellingPrice.text = data.productMrp
        Glide.with(context).load(data.productCoverImg).into(holder.binding.productImage)

    }
}