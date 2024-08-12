package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Komentar
import com.android.urbannavigator.data.model.Post
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.ItemCommunityPostBinding
import com.android.urbannavigator.databinding.ItemLayoutKomentarBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KomentarAdapter(private val chooseCallback : (Komentar) -> Unit):
    RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    private var komentarList: List<Komentar> = listOf()
    private var userList: List<User> = listOf()

    fun submitList(listKomentar: List<Komentar>) {
        komentarList = listKomentar
        notifyDataSetChanged()
    }

    fun submitUserList(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        val binding = ItemLayoutKomentarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KomentarViewHolder(binding, chooseCallback)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        holder.bind(komentarList[position], userList)
    }

    override fun getItemCount(): Int = komentarList.size

    class KomentarViewHolder(private val binding: ItemLayoutKomentarBinding,
                         private val chooseCallback : (Komentar) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(komentar: Komentar, allUser: List<User>) {
            val commenterUser = allUser.first { it.uid == komentar.userId }
            binding.tvUsernameUser.text = commenterUser.username

            Glide.with(binding.root.context).load(commenterUser.profilePic)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(350).placeholder(R.drawable.img_default_profile)
                .centerCrop().error(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)

            val date = Date(komentar.waktu)

            val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
            val formattedDate = dateFormat.format(date)

            binding.tvCommentTime.text = formattedDate
            binding.tvDescUlasan.text = komentar.komen
            binding.root.setOnClickListener {
                chooseCallback.invoke(komentar)
            }
        }
    }
}

