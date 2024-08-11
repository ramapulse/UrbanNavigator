package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.urbannavigator.R
import com.android.urbannavigator.data.model.Post
import com.android.urbannavigator.data.model.User
import com.android.urbannavigator.databinding.ItemCommunityPostBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val chooseCallback : (Post) -> Unit):
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList: List<Post> = listOf()
    private var userList: List<User> = listOf()

    fun submitList(posts: List<Post>) {
        postList = posts
        notifyDataSetChanged()
    }

    fun submitUserList(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, chooseCallback)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position], userList)
    }

    override fun getItemCount(): Int = postList.size

    class PostViewHolder(private val binding: ItemCommunityPostBinding,
                          private val chooseCallback : (Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, allUser: List<User>) {
            val posterUser = allUser.first { it.uid == post.userId }
            binding.tvUsernameUser.text = posterUser.username
            binding.tvLocationUser.text = posterUser.location

            Glide.with(binding.root.context).load(post.urlFoto)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(700).centerCrop().into(binding.ivPost)
            binding.tvDescPost.text = post.deskripsi

            Glide.with(binding.root.context).load(posterUser.profilePic)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(350).placeholder(R.drawable.img_default_profile)
                .centerCrop().error(R.drawable.img_default_profile)
                .into(binding.ivUserProfile)

            val date = Date(post.waktu)

            val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("id", "ID"))
            val formattedDate = dateFormat.format(date)

            binding.tvPostTime.text = formattedDate

            if(post.penyukaPost.isNotEmpty()){
                if(post.penyukaPost.size > 3){
                    val takeThreeUser = post.penyukaPost.take(3)
                    val allUserName = allUser.filter { takeThreeUser.contains(it.uid) }
                    binding.tvLikeCount.text = allUserName.joinToString(separator = ", ") { it.username } + " dan ${post.penyukaPost.size - 3} orang lainnya."
                }else{
                    val allUserName = allUser.filter { post.penyukaPost.contains(it.uid) }
                    binding.tvLikeCount.text = allUserName.joinToString(separator = ", ") { it.username }
                }
            }else{
                binding.tvLikeCount.text = "Belum ada like"
            }

            if(post.penyukaPost.contains(FirebaseAuth.getInstance().currentUser?.uid)){
                binding.ivFavorite.setImageResource(R.drawable.ic_fav)
            }else{
                binding.ivFavorite.setImageResource(R.drawable.ic_fav_border)
            }
            binding.ivFavorite.setOnClickListener {
                chooseCallback.invoke(post)
            }
        }
    }
}