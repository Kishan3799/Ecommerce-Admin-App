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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.kishan.ecommerceadminapp.R
import com.kishan.ecommerceadminapp.databinding.FragmentSliderBinding
import java.nio.file.FileStore
import java.util.UUID


class SliderFragment : Fragment() {
    private lateinit var binding: FragmentSliderBinding
    private var imageUrl:Uri? = null
    private lateinit var dialog: Dialog

    // this variable is launching gallery
    private var launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        //checking result code equal to activity
        if(it.resultCode == Activity.RESULT_OK){
            imageUrl = it.data!!.data
            binding.sliderImageView.setImageURI(imageUrl)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSliderBinding.inflate(layoutInflater)

        //this code is progress bar create by Dialog
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.setCancelable(false)

        binding.apply {

            sliderImageView.setOnClickListener {
//                opening Gallary
                val intent = Intent("android.intent.action.GET_CONTENT")
                intent.type = "image/*"
                launchGalleryActivity.launch(intent)
            }

            sliderUploadBtn.setOnClickListener {
                if (imageUrl != null){
                    uploadImage(imageUrl!!)
                }else {
                    Toast.makeText(requireContext(), "Please Select Image" , Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    private fun uploadImage(uri: Uri) {
        dialog.show()

        val fileName = UUID.randomUUID().toString()+".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("slider/$fileName")
        refStorage.putFile(uri)
            .addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { image->
                    storeData(image.toString())
                }
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong with storage", Toast.LENGTH_SHORT).show()
            }

    }

    private fun storeData(image: String) {
        val db = Firebase.firestore

        val data = hashMapOf<String, Any>(
            "img" to image
        )

        db.collection("slider").document("item").set(data)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(requireContext(), "Slider Update", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(requireContext(), "Something is Wrong", Toast.LENGTH_SHORT).show()
            }
    }

}