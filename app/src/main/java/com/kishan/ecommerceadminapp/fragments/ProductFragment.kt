package com.kishan.ecommerceadminapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.adapter.ProductDetailAdapter
import com.kishan.ecommerceadminapp.databinding.FragmentProductBinding
import com.kishan.ecommerceadminapp.model.AddProductModel
import com.kishan.ecommerceadminapp.model.ProductDetailModel


class ProductFragment : Fragment() {
    private lateinit var binding: FragmentProductBinding
    private lateinit var productList : ArrayList<ProductDetailModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductBinding.inflate(layoutInflater)

        binding.productFabBtn.setOnClickListener {
             Navigation.findNavController(it).navigate(R.id.action_productFragment_to_addProductFragment)
        }

        productList = ArrayList()

        getProductData()

        return binding.root
    }

    private fun getProductData(){
        Firebase.firestore.collection("products")
            .get().addOnSuccessListener {
                productList.clear()
                for (doc in it.documents){
                    val data = doc.toObject(ProductDetailModel::class.java)
                    productList.add(data!!)
                }
                binding.productRecyclerView.adapter = ProductDetailAdapter(requireContext(), productList)
                binding.progressBar2.visibility = View.GONE
            }
    }

}