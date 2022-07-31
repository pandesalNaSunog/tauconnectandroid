package com.example.tauconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(private val list: MutableList<CommentX>): RecyclerView.Adapter<CommentAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]

        holder.itemView.apply {
            val name = findViewById<TextView>(R.id.name)
            val comment = findViewById<TextView>(R.id.comment)

            name.text = curr.name
            comment.text = curr.comment
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(comment: CommentX){
        list.add(comment)
        notifyItemInserted(list.size - 1)
    }
}