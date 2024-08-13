package com.android.urbannavigator.presentation.park

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.databinding.FragmentParkDetailBinding
import com.android.urbannavigator.presentation.MainActivity
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.EventAdapter
import com.android.urbannavigator.presentation.adapter.SliderAdapter
import com.android.urbannavigator.presentation.event.EventFragmentDirections
import com.smarteist.autoimageslider.SliderView


class ParkDetailFragment : Fragment() {

    private var _binding: FragmentParkDetailBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataName = ParkDetailFragmentArgs.fromBundle(arguments as Bundle).tamanId

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLihatUlasan.setOnClickListener {
            val toUlasanFragment = ParkDetailFragmentDirections.actionParkDetailFragmentToUlasanFragment(dataName)
            findNavController().navigate(toUlasanFragment)
        }

        binding.btnReportForm.setOnClickListener {
            val toLaporan = ParkDetailFragmentDirections.actionParkDetailFragmentToFormKerusakanFragment(dataName)
            findNavController().navigate(toLaporan)
        }

        val eventAdapter = EventAdapter{ event ->
            findNavController().navigate(
                ParkDetailFragmentDirections.actionParkDetailFragmentToEventDetailFragment(event))
        }
        binding.rvEventPark.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
            isNestedScrollingEnabled = false
        }
        mainViewModel.tamanList.observe(viewLifecycleOwner){
            val currentTaman = it.first { taman -> taman.tamanId == dataName }

            eventAdapter.submitTamanList(it)

            val sliderAdapter = SliderAdapter( ArrayList(currentTaman.listGambar))
            val sliderView = binding.imageSlider
            sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
            sliderView.setSliderAdapter(sliderAdapter)
            sliderView.scrollTimeInSec = 3
            sliderView.isAutoCycle = true
            sliderView.startAutoCycle()

            initView(currentTaman)
        }

        mainViewModel.ulasanList.observe(viewLifecycleOwner){
            val currentUlasanList = it.filter { ulasan -> ulasan.tamanId ==  dataName}
            val score = currentUlasanList.map { ulasan -> ulasan.rating }.average().toFloat()

            binding.ratingTaman.rating = score
            binding.tvRatingCount.text = if(currentUlasanList.isEmpty()) "Belum ada ulasan" else "(${currentUlasanList.size} rating)"
        }

        binding.btnSeeEvent.setOnClickListener {
            (activity as MainActivity).switchMenu(R.id.eventFragment)
        }


        mainViewModel.eventList.observe(viewLifecycleOwner){
            val currentEvent = it.first{ event -> event.tamanId == dataName}
            eventAdapter.submitList(listOf(currentEvent))
        }
    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initView(currentTaman: Taman){
        binding.apply {
            tvParkName.text = currentTaman.nama
            tvAlamatTaman.text = currentTaman.alamat
            tvHariBuka.text = "${currentTaman.hariBuka} - ${currentTaman.sampaiHariBuka}"
            tvJamBuka.text = "${currentTaman.jamBuka} - ${currentTaman.jamTutup}"
            tvDeskripsiTaman.text = currentTaman.deskripsi
            btnSeeOnGmaps.setOnClickListener {
                val geoUri = "http://maps.google.com/maps?q=loc:${currentTaman.lat},${currentTaman.lon} (${currentTaman.nama})"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
                requireContext().startActivity(intent)
            }
        }
    }
}