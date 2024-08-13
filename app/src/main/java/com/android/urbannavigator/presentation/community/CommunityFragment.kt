package com.android.urbannavigator.presentation.community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.urbannavigator.R
import com.android.urbannavigator.databinding.FragmentCommunityBinding
import com.android.urbannavigator.presentation.LoadingDialog
import com.android.urbannavigator.presentation.MainViewModel
import com.android.urbannavigator.presentation.adapter.EventAdapter
import com.android.urbannavigator.presentation.adapter.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingDialog = LoadingDialog(requireContext())

        val postAdapter = PostAdapter ({post ->
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            if(currentUserUid != null){
                loadingDialog.startLoadingDialog()
                val placeholderList = post.penyukaPost.toMutableList()
                if(post.penyukaPost.contains(currentUserUid)) placeholderList.remove(currentUserUid) else placeholderList.add(currentUserUid)
                val updatedValues = hashMapOf<String, Any>(
                    "penyukaPost" to placeholderList
                )

                FirebaseDatabase.getInstance().getReference("Post")
                    .child(post.postId)
                    .updateChildren(updatedValues)
                    .addOnCompleteListener { taskDatabase ->
                        if (taskDatabase.isSuccessful) {
                            loadingDialog.dismissDialog()
                        } else {
                            loadingDialog.dismissDialog()
                            makeToast(taskDatabase.exception?.message ?: "Error")
                        }
                    }
            }
        }, {
            findNavController().navigate(CommunityFragmentDirections.actionCommunityFragmentToKomentarFragment(it.postId))
        })

        binding.rvPost.apply {
            layoutManager = LinearLayoutManager(requireContext(),)
            adapter = postAdapter
            isNestedScrollingEnabled = false
        }

        binding.btnAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_postFormFragment)
        }

        mainViewModel.filteredPostList.observe(viewLifecycleOwner){ listCommunity->
            postAdapter.submitList(listCommunity)
        }

        mainViewModel.userList.observe(viewLifecycleOwner){ listUser ->
            postAdapter.submitUserList(listUser)
        }

        mainViewModel.komentarList.observe(viewLifecycleOwner){
            postAdapter.submitKomentarList(it)
        }

        mainViewModel.tamanList.observe(viewLifecycleOwner){ listTaman ->
            val placeholder = "Semua Taman"
            val namaTamanList = listOf(placeholder) + listTaman.map { it.nama }
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namaTamanList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.filterPostTaman.adapter = spinnerAdapter

            binding.filterPostTaman.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                        mainViewModel.filterPostTaman("")
                    } else {
                        val selectedTamanList = listTaman[position - 1].tamanId
                        mainViewModel.filterPostTaman(selectedTamanList)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.post_filter_array,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterPostSpinner.adapter = adapter
        }

        binding.filterPostSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                mainViewModel.filterPost(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
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