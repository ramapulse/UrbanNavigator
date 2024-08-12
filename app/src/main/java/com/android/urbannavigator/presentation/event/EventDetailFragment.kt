package com.android.urbannavigator.presentation.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Event
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.databinding.FragmentEventDetailBinding
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.park.ParkDetailFragmentArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentEvent = EventDetailFragmentArgs.fromBundle(arguments as Bundle).currentEvent


        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvEventName.text = currentEvent.nama
        binding.tvHariBuka.text = "${currentEvent.tanggalMulai} - ${currentEvent.tanggalSelesai}"
        binding.tvJamBuka.text = "${currentEvent.jamMulai} - ${currentEvent.jamSelesai}"
        binding.tvDeskripsiTaman.text = currentEvent.deskripsi

        Glide.with(requireContext()).load(currentEvent.foto).centerCrop().skipMemoryCache(true)
            .override(800).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(binding.ivEventImage)

        mainViewModel.tamanList.observe(viewLifecycleOwner){
            val currentTaman = it.first{ taman -> taman.tamanId == currentEvent.tamanId }
            binding.tvNamaTaman.text = currentTaman.nama
            binding.tvNamaTaman.setOnClickListener {
                findNavController().navigate(EventDetailFragmentDirections.actionEventDetailFragmentToParkDetailFragment(currentEvent.tamanId))
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