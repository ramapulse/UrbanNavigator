package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.data.model.Ulasan
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.ItemLayoutTamanBinding
import com.android.urbannavigator.databinding.LayoutItemUlasanBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class UlasanAdapter(private val chooseCallback : (Ulasan) -> Unit):
    RecyclerView.Adapter<UlasanAdapter.UlasanViewHolder>() {

    private var ulasanList: List<Ulasan> = listOf()

    private var userList: List<User> = listOf()

    fun submitList(ulasans: List<Ulasan>) {
        ulasanList = ulasans
        notifyDataSetChanged()
    }

    fun submitUserList(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UlasanViewHolder {
        val binding = LayoutItemUlasanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UlasanViewHolder(binding, chooseCallback)
    }

    override fun onBindViewHolder(holder: UlasanViewHolder, position: Int) {
        holder.bind(ulasanList[position], userList)
    }

    override fun getItemCount(): Int = ulasanList.size

    class UlasanViewHolder(private val binding: LayoutItemUlasanBinding,
                          private val chooseCallback : (Ulasan) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ulasan: Ulasan, users: List<User>) {
            val currentUser = users.first {  it.uid == ulasan.userId }
            binding.tvUsernameUser.text = currentUser.username
            binding.tvLocationUser.text = currentUser.location
            Glide.with(binding.root.context).load(currentUser.profilePic)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(350).placeholder(R.drawable.img_default_profile)
                .centerCrop().error(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)

            binding.tvJudulUlasan.text = ulasan.judul
            binding.tvDescUlasan.text = ulasan.komen
            binding.ratingTaman.rating = ulasan.rating
            binding.root.setOnClickListener {
                chooseCallback.invoke(ulasan)
            }
        }
    }
}