package com.android.urbannavigator.presentation.park

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Ulasan
import com.android.urbannavigator.databinding.FragmentUlasanBinding
import com.android.urbannavigator.databinding.FragmentUlasanFormBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class UlasanFormFragment : Fragment() {

    private var _binding: FragmentUlasanFormBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private var currentRating = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUlasanFormBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataName = UlasanFormFragmentArgs.fromBundle(arguments as Bundle).tamanId

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ratingTaman.setOnRatingBarChangeListener { _, rating, _ ->
            currentRating = rating
            binding.tvRating.text = rating.toString()
        }

        binding.btnSubmitUlasan.setOnClickListener {
            binding.deskripsiUlasanET.error = null
            binding.judulUlasanEt.error = null
            when {
                binding.judulUlasanEt.editText?.text.isNullOrEmpty() -> {
                    binding.judulUlasanEt.error = getString(R.string.error_field_kosong)
                    binding.judulUlasanEt.requestFocus()
                }
                binding.deskripsiUlasanET.editText?.text.isNullOrEmpty() -> {
                    binding.deskripsiUlasanET.error = getString(R.string.error_field_kosong)
                    binding.deskripsiUlasanET.requestFocus()
                }
                else -> {
                    postCurrentUlasan(dataName)
                }
            }
        }

    }

    private fun postCurrentUlasan(tamanId: String) {
        val loadingDialog = LoadingDialog(requireContext())
        loadingDialog.startLoadingDialog()
        val ulasanRating = currentRating
        val judulUlasan = binding.judulUlasanEt.editText?.text.toString()
        val deskripsiUlasan = binding.deskripsiUlasanET.editText?.text.toString()

        val database = FirebaseDatabase.getInstance()
        val ulasanRef = database.getReference("Ulasan")
        val newUlasanRef = ulasanRef.push()
        val ulasanId = newUlasanRef.key ?: ""

        val ulasan = Ulasan(
            ulasanId,
            System.currentTimeMillis(),
            ulasanRating,
            judulUlasan,
            deskripsiUlasan,
            FirebaseAuth.getInstance().currentUser?.uid?:"",
            tamanId
        )

        newUlasanRef.setValue(ulasan).addOnCompleteListener { task ->
            loadingDialog.dismissDialog()
            if (task.isSuccessful) {
                makeToast("Berhasil Memberi Ulasan")
                findNavController().popBackStack()
            } else {
                makeToast("Gagal memberi ulasan, coba lagi kembali")
            }
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