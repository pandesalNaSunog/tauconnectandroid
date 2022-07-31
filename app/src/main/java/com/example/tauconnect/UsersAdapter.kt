package com.example.tauconnect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class UsersAdapter(private val list: MutableList<User>): RecyclerView.Adapter<UsersAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.users_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val name = findViewById<TextView>(R.id.name)
            val user = findViewById<CardView>(R.id.user)
            name.text = curr.name

            user.setOnClickListener{
                val intent = Intent(context, Conversation::class.java)
                intent.putExtra("user_id", curr.id.toString())
                intent.putExtra("name", curr.name)
                startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun add(item: User){
        list.add(item)
        notifyItemInserted(list.size - 1)
    }
}