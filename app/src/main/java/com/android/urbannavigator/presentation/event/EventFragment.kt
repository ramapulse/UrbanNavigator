package com.android.urbannavigator.presentation.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.urbannavigator.databinding.FragmentEventBinding
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.EventAdapter
import com.android.urbannavigator.presentation.adapter.TamanAdapter


class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventAdapter = EventAdapter {event ->
            makeToast("event terpilih ${event.nama}")
        }

        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireContext(),)
            adapter = eventAdapter
            isNestedScrollingEnabled = false
        }

        mainViewModel.eventList.observe(viewLifecycleOwner){ listEvent->
            eventAdapter.submitList(listEvent)
        }

        mainViewModel.tamanList.observe(viewLifecycleOwner){ listTaman ->
            eventAdapter.submitTamanList(listTaman)
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