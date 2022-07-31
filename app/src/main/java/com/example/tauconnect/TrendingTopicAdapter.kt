package com.example.tauconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrendingTopicAdapter(private val list: MutableList<TrendingTopicItem>): RecyclerView.Adapter<TrendingTopicAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.trending_topic_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val topic = findViewById<TextView>(R.id.topic)
            val number = position + 1
            topic.text = "$number. ${curr.description}"
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(item: TrendingTopicItem){
        list.add(item)
        notifyDataSetChanged()
    }
}