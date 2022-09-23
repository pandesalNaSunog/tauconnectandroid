package com.example.tauconnect

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class UsersToMessageAdapter(private val list: MutableList<User>, private var to: Button, private var alert: AlertDialog): RecyclerView.Adapter<UsersToMessageAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.users_to_message_item, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply {
            val user = findViewById<Button>(R.id.user)
            val value = curr.id
            user.text = curr.name

            user.setOnClickListener {
                to.tag = value
                to.text = "To: ${curr.name}"
                alert.dismiss()
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