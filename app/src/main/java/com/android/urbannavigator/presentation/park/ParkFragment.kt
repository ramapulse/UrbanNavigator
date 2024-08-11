package com.android.urbannavigator.presentation.park

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.urbannavigator.databinding.FragmentParkBinding
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.EventAdapter
import com.android.urbannavigator.presentation.adapter.TamanAdapter

class ParkFragment : Fragment() {

    private var _binding: FragmentParkBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tamanAdapter = TamanAdapter {taman ->
            val toDetailFragment = ParkFragmentDirections.actionParkFragmentToParkDetailFragment(taman.tamanId)
            findNavController().navigate(toDetailFragment)
        }

        binding.rvPark.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = tamanAdapter
            isNestedScrollingEnabled = false
        }

        mainViewModel.filteredTamanList.observe(viewLifecycleOwner){ listTaman ->
            tamanAdapter.submitList(listTaman)
        }

        binding.svTaman.setOnClickListener {
            binding.svTaman.isIconified= false
            binding.svTaman.requestFocus()
        }

        binding.svTaman.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { mainViewModel.searchPets(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { mainViewModel.searchPets(it) }
                return true
            }
        })

    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}