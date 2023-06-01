package com.kishan.ecommerceadminapp.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.ktx.firestore

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.adapter.AddProductImageAdapter
import com.kishan.ecommerceadminapp.databinding.FragmentAddProductBinding
import com.kishan.ecommerceadminapp.model.AddProductModel
import com.kishan.ecommerceadminapp.model.CategoryModel
import java.util.*
import kotlin.collections.ArrayList


class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var list: ArrayList<Uri>
    private lateinit var listImages : ArrayList<String>
    private var coverImage : Uri? = null
    private lateinit var adapter: AddProductImageAdapter
    private lateinit var dialog : Dialog
    private lateinit var categoryList : ArrayList<String>
    private var coverImgUrl : String? = ""

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            coverImage = it.data!!.data
            binding.productCoverImage.setImageURI(coverImage)
            binding.productCoverImage.visibility = View.VISIBLE
        }
    }

    private var launchProductActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            val imageUrl = it.data!!.data
            list.add(imageUrl!!)
            adapter.notifyDataSetChanged()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddProductBinding.inflate(layoutInflater)

        list = ArrayList()
        listImages = ArrayList()

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)



        binding.selectCoverImage.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.productImageBtn.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchProductActivity.launch(intent)
        }

        setProductCategory()

        adapter = AddProductImageAdapter(list)
        binding.productImageRecyclerView.adapter = adapter

        binding.submitProductBtn.setOnClickListener {
            validateData()
        }
        return binding.root
    }

    private fun validateData() {
        if(binding.productNameEt.text.toString().isEmpty()) {
            binding.productNameEt.requestFocus()
            binding.productNameEt.error = "Empty"
        }else if (binding.productSPEt.text.toString().isEmpty()){
            binding.productSPEt.requestFocus()
            binding.productSPEt.error = "Empty"
        }else if (coverImage == null) {
            Toast.makeText(requireContext(),"Please Select cover image", Toast.LENGTH_SHORT).show()
        }else if (list.size < 1){
            Toast.makeText(requireContext(),"Please Select product image", Toast.LENGTH_SHORT).show()
        }else {
            uploadImage()
        }
    }

    private fun uploadImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(coverImage!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image->
                    coverImgUrl = image.toString()

                    uploadProductImage()
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private var i = 0
    private fun uploadProductImage() {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("products/$fileName")
        refStorage.putFile(list[i])
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image->
                    listImages.add(image!!.toString())
                    if (list.size == listImages.size){
                        storeData()
                    }else {
                        i += 1
                        uploadProductImage()
                    }
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong with storage", Toast.LENGTH_SHORT).show()
            }
    }

    private fun storeData() {
        val db = Firebase.firestore.collection("products")
        val key = db.document().id

        val data = AddProductModel(
            binding.productNameEt.text.toString(),
            binding.productDescriptionEt.text.toString(),
            coverImgUrl.toString(),
            categoryList[binding.productCategoryDropdown.selectedItemPosition],
            key,
            binding.productMRPEt.text.toString(),
            binding.productSPEt.text.toString(),
            listImages
        )

        db.document(key).set(data).addOnSuccessListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Product Added", Toast.LENGTH_SHORT).show()
            binding.productNameEt.text = null
        }.addOnFailureListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Product Not Added", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setProductCategory(){
        categoryList = ArrayList()
        Firebase.firestore.collection("categories").get().addOnSuccessListener {
            categoryList.clear()
            for (doc in it.documents) {
                val data = doc.toObject(CategoryModel::class.java)
                categoryList.add(data!!.category!!)
            }
            categoryList.add(0, "Select Category")

            val arrayAdapter =  ArrayAdapter(requireContext(), R.layout.dropdown_item_category_layout, categoryList)
            binding.productCategoryDropdown.adapter = arrayAdapter
        }
    }

}