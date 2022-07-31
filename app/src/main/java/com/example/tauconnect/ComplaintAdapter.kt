package com.example.tauconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComplaintAdapter(private val list: MutableList<ComplaintsItem>): RecyclerView.Adapter<ComplaintAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.complaint_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val complain = findViewById<TextView>(R.id.complain)
            val status = findViewById<TextView>(R.id.status)
            val date = findViewById<TextView>(R.id.date)

            complain.text = curr.complaint
            status.text = curr.status
            date.text = curr.created_at
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(item: ComplaintsItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}