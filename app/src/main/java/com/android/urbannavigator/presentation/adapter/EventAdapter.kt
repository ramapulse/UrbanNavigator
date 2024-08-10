package com.android.urbannavigator.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.urbannavigator.data.model.Event
import com.android.urbannavigator.data.model.Taman
import com.android.urbannavigator.databinding.ItemLayoutEventBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class EventAdapter(private val chooseCallback : (Event) -> Unit):
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var eventList: List<Event> = listOf()
    private var tamanList: List<Taman> = listOf()

    fun submitList(events: List<Event>) {
        eventList = events
        notifyDataSetChanged()
    }

    fun submitTamanList(tamans: List<Taman>){
        tamanList = tamans
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemLayoutEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, chooseCallback)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position], tamanList)
    }

    override fun getItemCount(): Int = eventList.size

    class EventViewHolder(private val binding: ItemLayoutEventBinding,
                          private val chooseCallback : (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, tamanList: List<Taman>) {
            binding.tvEventName.text = event.nama
            Glide.with(binding.root.context).load(event.foto)
                .diskCacheStrategy(DiskCacheStrategy.DATA).override(700).into(binding.ivEvent)
            binding.tvPeriodeEvent.text = "${event.tanggalMulai} - \n${event.tanggalSelesai}"
            binding.tvNamaTaman.text = tamanList.first { it.tamanId == event.tamanId }.nama
            binding.root.setOnClickListener {
                chooseCallback.invoke(event)
            }
        }
    }
}