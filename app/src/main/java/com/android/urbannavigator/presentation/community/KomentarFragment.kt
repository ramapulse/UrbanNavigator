package com.android.urbannavigator.presentation.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Komentar
import com.android.urbannavigator.databinding.FragmentCommunityBinding
import com.android.urbannavigator.databinding.FragmentKomentarBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.KomentarAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class KomentarFragment : Fragment() {

    private var _binding: FragmentKomentarBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKomentarBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingDialog = LoadingDialog(requireContext())

        val postId = KomentarFragmentArgs.fromBundle(arguments as Bundle).postId

        val komentarAdapter = KomentarAdapter{

        }

        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(requireContext(),)
            adapter = komentarAdapter
            isNestedScrollingEnabled = true
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        mainViewModel.komentarList.observe(viewLifecycleOwner){
            val allKomentar = it.filter { komen -> komen.postId == postId }
            komentarAdapter.submitList(allKomentar)
        }

        mainViewModel.userList.observe(viewLifecycleOwner){
            komentarAdapter.submitUserList(it)
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner){
            Glide.with(binding.root.context).load(it.profilePic)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(350).placeholder(R.drawable.img_default_profile)
                .centerCrop().error(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)

            binding.btnSendComment.setOnClickListener {
                binding.commentEt.error = null
                when{
                    binding.commentEt.editText?.text.isNullOrEmpty() -> {
                        binding.commentEt.error = getString(R.string.error_field_kosong)
                        binding.commentEt.requestFocus()
                    }
                    else -> {
                        val komentar = binding.commentEt.editText?.text.toString()

                        val database = FirebaseDatabase.getInstance()
                        val komentarRef = database.getReference("Komentar")
                        val newKomentarRef = komentarRef.push()
                        val komentarId = newKomentarRef.key ?: ""
                        val komentarTime = System.currentTimeMillis()
                        val komentarData = Komentar(
                            komentarId, komentarTime, komentar, FirebaseAuth.getInstance().currentUser?.uid ?:"",
                            postId
                        )

                        newKomentarRef.setValue(komentarData).addOnCompleteListener { task ->
                            loadingDialog.dismissDialog()
                            if (task.isSuccessful) {
                                makeToast("Berhasil memberi komentar")
                                binding.commentEt.editText?.setText("")
                            } else {
                                makeToast("Gagal komentar, coba lagi kembali")
                            }
                        }
                    }
                }
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