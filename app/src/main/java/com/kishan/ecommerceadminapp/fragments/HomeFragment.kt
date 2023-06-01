package com.kishan.ecommerceadminapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.activity.AllOrderActivity
import com.kishan.ecommerceadminapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        //category Button
        binding.categoryBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }

        //product Button
        binding.productBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_productFragment)
        }

        //Slider Button
        binding.sliderBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sliderFragment)
        }

        //AllOrder Button
        binding.allOrderBtn.setOnClickListener {
            val openOrderActivity = Intent(requireContext(), AllOrderActivity::class.java)
            startActivity(openOrderActivity)
        }

        return binding.root
    }


}