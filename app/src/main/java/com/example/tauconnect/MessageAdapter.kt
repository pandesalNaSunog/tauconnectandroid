package com.example.tauconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val list: MutableList<MessagesItem>): RecyclerView.Adapter<MessageAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return if(viewType == 1){
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.message_bubble_sender, parent, false))
        }else{
            Holder(LayoutInflater.from(parent.context).inflate(R.layout.message_bubble_receiver, parent, false))
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val message = findViewById<TextView>(R.id.message)

            message.text = curr.message
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {

        val curr = list[position]
        return if(curr.mine){
            1
        }else{
            2
        }
    }

    fun add(item: MessagesItem){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}