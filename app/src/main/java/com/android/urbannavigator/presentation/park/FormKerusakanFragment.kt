package com.android.urbannavigator.presentation.park

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.FormKerusakan
import com.android.urbannavigator.data.model.Post
import com.android.urbannavigator.data.model.Ulasan
import com.android.urbannavigator.databinding.FragmentFormKerusakanBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FormKerusakanFragment : Fragment() {

    private var _binding: FragmentFormKerusakanBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var kerusakanPhotoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormKerusakanBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataName = FormKerusakanFragmentArgs.fromBundle(arguments as Bundle).tamanId

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mainViewModel.tamanList.observe(viewLifecycleOwner){
            val currentTaman = it.first { taman -> taman.tamanId == dataName }

            Glide.with(binding.root.context).load(currentTaman.listGambar.get(0))
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(800).into(binding.ivParkImage)
            binding.tvNamaTaman.text = currentTaman.nama
        }

        initPhotoPicker()

        binding.btnSubmitLaporan.setOnClickListener {
            binding.etJudul.error = null
            binding.etDeskripsi.error = null
            when {
                binding.etJudul.editText?.text.isNullOrEmpty() -> {
                    binding.etJudul.error = getString(R.string.error_field_kosong)
                    binding.etJudul.requestFocus()
                }
                kerusakanPhotoUri == null ->{
                    binding.etKerusakanPhoto.error = getString(R.string.error_field_kosong)
                    binding.etKerusakanPhoto.requestFocus()
                }
                binding.etDeskripsi.editText?.text.isNullOrEmpty() -> {
                    binding.etDeskripsi.error = getString(R.string.error_field_kosong)
                    binding.etDeskripsi.requestFocus()
                }
                else -> {
                    postCurrentLaporan(dataName)
                }
            }
        }

    }

    private fun postCurrentLaporan(dataName: String) {
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.startLoadingDialog()
        val fotoKerusakan = kerusakanPhotoUri
        val judulKerusakan = binding.etJudul.editText?.text.toString()
        val deskripsiKerusakan = binding.etDeskripsi.editText?.text.toString()

        val database = FirebaseDatabase.getInstance()
        val kerusakanRef = database.getReference("LaporanKerusakan")
        val newKerusakanRef = kerusakanRef.push()
        val kerusakanId = newKerusakanRef.key ?: ""

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference("laporan/$kerusakanId")
        storageRef.putFile(fotoKerusakan!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    makeToast("Foto laporan terupload")
                    val laporanPhotoLink = uri.toString()
                    val laporan = FormKerusakan(
                        kerusakanId,
                        dataName,
                        FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        judulKerusakan,
                        laporanPhotoLink,
                        deskripsiKerusakan,
                        System.currentTimeMillis()
                    )

                    newKerusakanRef.setValue(laporan).addOnCompleteListener { task ->
                        loadingDialog.dismissDialog()
                        if (task.isSuccessful) {
                            makeToast("Berhasil mengirim laporan kerusakan")
                            findNavController().popBackStack()
                        } else {
                            makeToast("Gagal mengirim, coba lagi kembali")
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

    private fun initPhotoPicker() {
        binding.btnUpload.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .crop(1f,1f)
                .createIntent { intent ->
                    startForKerusakanImageResult.launch(intent)
                }
        }

        binding.btnUploadImageUploaded.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .crop(1f,1f)
                .createIntent { intent ->
                    startForKerusakanImageResult.launch(intent)
                }
        }
    }

    private val startForKerusakanImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                binding.layoutImageNotUploaded.visibility = View.GONE
                binding.layoutImageUploaded.visibility = View.VISIBLE

                binding.ivPhotoUploaded.setImageURI(fileUri)
                kerusakanPhotoUri = fileUri
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