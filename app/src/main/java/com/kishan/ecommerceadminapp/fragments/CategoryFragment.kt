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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.adapter.CategoryAdapter
import com.kishan.ecommerceadminapp.databinding.FragmentCategoryBinding
import com.kishan.ecommerceadminapp.model.CategoryModel
import java.util.*
import kotlin.collections.ArrayList


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var imageUrl: Uri? = null
    private lateinit var dialog: Dialog

    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data!!.data
            binding.categoryImageView.setImageURI(imageUrl)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        getCategoryData()

        binding.apply {
            categoryImageView.setOnClickListener {
                //opening Gallary
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }
            categoryAddBtn.setOnClickListener {
               validateData(categoryETName.text.toString())
            }
        }


        return binding.root
    }

    private fun getCategoryData() {
        val categoryList = ArrayList<CategoryModel>()
        Firebase.firestore.collection("categories")
            .get().addOnSuccessListener {
                categoryList.clear()
                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    categoryList.add(data!!)
                }
                binding.categoryRecyclerView.adapter = CategoryAdapter(requireContext(),categoryList)
            }
    }

    private fun validateData(category: String) {
        if(category.isEmpty()){
            Toast.makeText(requireContext(), "Please enter category name", Toast.LENGTH_SHORT).show()
        }
        else if(imageUrl == null){
            Toast.makeText(requireContext(), "Please select category image", Toast.LENGTH_SHORT).show()
        }
        else {
            uploadImage(category)
        }
    }

    private fun uploadImage(category: String) {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("category/$fileName")
        refStorage.putFile(imageUrl!!)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image->
                    storeData(category,image.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong with storage", Toast.LENGTH_SHORT).show()
            }

    }

    private fun storeData(category: String, url: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "category" to category,
            "img" to url
        )

        db.collection("categories").add(data)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.categoryImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.image_preview, null))
                binding.categoryETName.text = null
                getCategoryData()
                Toast.makeText(requireContext(), "Category Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong", Toast.LENGTH_SHORT).show()
            }
    }

}

