package com.kishan.ecommerceadminapp.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kishan.ecommerceadminapp.databinding.AllOrderLayoutBinding
import com.kishan.ecommerceadminapp.model.AllOrderModel

class AllOrderAdapter(val list:ArrayList<AllOrderModel>, val context: Context) : RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {

    inner class AllOrderViewHolder(val binding:AllOrderLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
            return AllOrderViewHolder(
                AllOrderLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
            )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        val data = list[position]
        holder.binding.productNameTv.text = data.name
        holder.binding.productPriceTv.text = data.sellingprice
        holder.binding.cancelBtn.setOnClickListener {
//            holder.binding.proceedBtn.text = "Canceled"
            holder.binding.proceedBtn.visibility = GONE
            updateStatus("Canceled", data.orderId!!)
        }
        when(data.status){
            "Ordered" -> {
                holder.binding.proceedBtn.text = "Dispatched"
                holder.binding.proceedBtn.setOnClickListener {
                    updateStatus("Dispatched", data.orderId!!)
                }
            }
            "Dispatched" -> {
                holder.binding.proceedBtn.text = "Delivered"
                holder.binding.proceedBtn.setOnClickListener {
                    updateStatus("Delivered", data.orderId!!)
                }
            }
            "Delivered" -> {
                holder.binding.cancelBtn.visibility = GONE
                holder.binding.proceedBtn.text = "Already Delivered"
                holder.binding.proceedBtn.isEnabled = false
            }
            "Canceled" -> {
                holder.binding.proceedBtn.visibility = GONE
                holder.binding.cancelBtn.isEnabled = false
            }
        }
    }

    private fun updateStatus(str:String, doc:String){
        val data = hashMapOf<String, Any>()
        data["status"] = str
        Firebase.firestore.collection("allOrders")
            .document(doc).update(data).addOnSuccessListener {
                Toast.makeText(context, "Status Update", Toast.LENGTH_SHORT).show()
            }
    }
}