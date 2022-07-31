package com.example.tauconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnnouncementAdapter(private val list: MutableList<AnnouncementsItem>): RecyclerView.Adapter<AnnouncementAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.announcement_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val announcement = findViewById<TextView>(R.id.announcement)
            val date = findViewById<TextView>(R.id.date)
            val name = findViewById<TextView>(R.id.name)
            announcement.text = curr.description
            date.text = curr.created_at
            name.text = curr.user.name
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(item: AnnouncementsItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}