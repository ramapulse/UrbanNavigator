package com.android.urbannavigator.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.android.urbannavigator.R
import com.android.urbannavigator.databinding.FragmentHomeBinding
import com.android.urbannavigator.presentation.MainActivity
import com.android.urbannavigator.presentation.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.tvUsernameUser.text = user.username ?:""
            Glide.with(requireContext()).load(user.profilePic).centerCrop().skipMemoryCache(true)
                .override(100,100).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)
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