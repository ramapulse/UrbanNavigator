package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.data.model.Ulasan
import com.android.urbannavigator.databinding.ItemLayoutTamanBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class TamanAdapter(private val chooseCallback : (Taman) -> Unit):
    RecyclerView.Adapter<TamanAdapter.TamanViewHolder>() {

    private var tamanList: List<Taman> = listOf()

    private var ulasanList: List<Ulasan> = listOf()

    fun submitList(tamans: List<Taman>) {
        tamanList = tamans
        notifyDataSetChanged()
    }

    fun submitUlasan(listUlasan: List<Ulasan>){
        ulasanList = listUlasan
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TamanViewHolder {
        val binding = ItemLayoutTamanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TamanViewHolder(binding, chooseCallback)
    }

    override fun onBindViewHolder(holder: TamanViewHolder, position: Int) {
        holder.bind(tamanList[position], ulasanList)
    }

    override fun getItemCount(): Int = tamanList.size

    class TamanViewHolder(private val binding: ItemLayoutTamanBinding,
                          private val chooseCallback : (Taman) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taman: Taman, ulasanList: List<Ulasan>) {

            val currentUlasanList = ulasanList.filter { ulasan -> ulasan.tamanId ==  taman.tamanId}
            val score = currentUlasanList.map { ulasan -> ulasan.rating }.average().toFloat()

            binding.ratingTaman.rating = score
            binding.tvRatingCount.text = "(${currentUlasanList.size})"

            binding.tvNamaTaman.text = taman.nama
            Glide.with(binding.root.context).load(taman.listGambar.get(0))
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(500).into(binding.ivTaman)
            binding.tvDescTaman.text = taman.deskripsi
            binding.root.setOnClickListener {
                chooseCallback.invoke(taman)
            }
        }
    }
}