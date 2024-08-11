package com.android.urbannavigator.presentation.park

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.urbannavigator.databinding.FragmentUlasanBinding
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.UlasanAdapter
import com.google.firebase.auth.FirebaseAuth


class UlasanFragment : Fragment() {

    private var _binding: FragmentUlasanBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUlasanBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataName = UlasanFragmentArgs.fromBundle(arguments as Bundle).tamanId

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val ulasanAdapter = UlasanAdapter{

        }

        binding.rvUlasanTaman.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ulasanAdapter
            isNestedScrollingEnabled = false
        }

        mainViewModel.ulasanList.observe(viewLifecycleOwner){
            val currentUlasanList = it.filter { ulasan -> ulasan.tamanId ==  dataName}
            val score = currentUlasanList.map { ulasan -> ulasan.rating }.average().toFloat()
            val listPemberiUlasan = currentUlasanList.map { ulasan -> ulasan.userId }
            if(listPemberiUlasan.contains(FirebaseAuth.getInstance().currentUser?.uid)) binding.btnAddUlasan.visibility = View.GONE else binding.btnAddUlasan.visibility = View.VISIBLE

            binding.ratingTaman.rating = score
            binding.tvScoreAverage.text = if(currentUlasanList.isEmpty()) "0" else score.toString()
            binding.tvRatingCount.text = if(currentUlasanList.isEmpty()) "Belum ada ulasan" else "(${currentUlasanList.size} rating)"
            ulasanAdapter.submitList(currentUlasanList)
        }

        binding.btnAddUlasan.setOnClickListener {
            val toUlasanFormFragment = UlasanFragmentDirections.actionUlasanFragmentToUlasanFormFragment(dataName)
            findNavController().navigate(toUlasanFormFragment)
        }

        mainViewModel.userList.observe(viewLifecycleOwner){
            ulasanAdapter.submitUserList(it)
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