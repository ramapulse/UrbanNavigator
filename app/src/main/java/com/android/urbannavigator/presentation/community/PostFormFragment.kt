package com.android.urbannavigator.presentation.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Post
import com.android.urbannavigator.databinding.FragmentPostFormBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class PostFormFragment : Fragment() {

    private var _binding: FragmentPostFormBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var localPhotoPath: Uri? = null
    private var selectedTamanList: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostFormBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.currentUser.observe(viewLifecycleOwner){
            Glide.with(binding.root.context).load(it.profilePic)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(350).placeholder(R.drawable.img_default_profile)
                .centerCrop().error(R.drawable.img_default_profile)
                .into(binding.ivProfilePic)
        }

        binding.uploadedPhotoIV.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .crop(1f,1f)
                .createIntent { intent ->
                    startForPostProfile.launch(intent)
                }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mainViewModel.tamanList.observe(viewLifecycleOwner){ tamanList ->
            val placeholder = "Taman tidak dilist"
            val namaTamanList = listOf(placeholder) + tamanList.map { it.nama }
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namaTamanList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.tamanSpinner.adapter = spinnerAdapter

            binding.tamanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                    } else {
                        selectedTamanList = tamanList[position - 1].tamanId
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        binding.btnPost.setOnClickListener {
            binding.deskripsiET.error = null
            when {
                binding.deskripsiET.editText?.text.isNullOrEmpty() -> {
                    binding.deskripsiET.error = getString(R.string.error_field_kosong)
                    binding.deskripsiET.requestFocus()
                }
                localPhotoPath == null ->{
                    makeToast("Upload foto terlebih dahulu")
                }
                else -> {
                    postCurrentForm()
                }
            }
        }
    }

    private fun postCurrentForm(){
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.startLoadingDialog()
        val postPhoto = localPhotoPath!!
        val postDesc = binding.deskripsiET.editText?.text.toString()
        val postTamanId = selectedTamanList

        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference("Post")
        val newPostRef = postRef.push()
        val postId = newPostRef.key ?: ""
        val postTimeStamp = System.currentTimeMillis()


        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference("post/$postId")
        storageRef.putFile(postPhoto)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    makeToast("Foto terupload")
                    val postPhotoLink = uri.toString()
                    val post = Post(
                        postId,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        postTamanId,
                        listOf(),
                        postPhotoLink,
                        postDesc,
                        postTimeStamp
                    )

                    newPostRef.setValue(post).addOnCompleteListener { task ->
                        loadingDialog.dismissDialog()
                        if (task.isSuccessful) {
                            makeToast("Berhasil Post")
                            findNavController().popBackStack()
                        } else {
                            makeToast("Gagal posting, coba lagi kembali")
                        }
                    }
                }.addOnFailureListener { e ->
                    loadingDialog.dismissDialog()
                    makeToast(e.message.toString())
                }
            }
            .addOnFailureListener { e ->
                loadingDialog.dismissDialog()
                makeToast(e.message.toString())
            }
    }

    private val startForPostProfile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                Glide.with(requireContext()).load(fileUri).skipMemoryCache(true).centerCrop().into(binding.uploadedPhotoIV)
                localPhotoPath = fileUri

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                makeToast(ImagePicker.getError(data))
            } else {

            }
        }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}