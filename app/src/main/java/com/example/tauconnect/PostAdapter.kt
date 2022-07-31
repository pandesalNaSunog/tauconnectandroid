package com.example.tauconnect

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class PostAdapter(private val list: MutableList<PostsItem>): RecyclerView.Adapter<PostAdapter.Holder>() {
    class Holder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val curr = list[position]
        holder.itemView.apply{
            val viewAllComments = findViewById<TextView>(R.id.viewAllComments)
            val writeComment = findViewById<EditText>(R.id.writeComment)
            val postComment = findViewById<Button>(R.id.postComment)
            val name = findViewById<TextView>(R.id.name)
            val date = findViewById<TextView>(R.id.date)
            val description = findViewById<TextView>(R.id.description)
            val profilePicture = findViewById<CircleImageView>(R.id.circleImageView)
            val commentRecycler = findViewById<RecyclerView>(R.id.commentRecycler)
            val commentAdapter = CommentAdapter(mutableListOf())
            commentRecycler.adapter = commentAdapter
            commentRecycler.layoutManager = LinearLayoutManager(context)
            val path = curr.profile_picture
            val db = UserDatabase(context)
            val alerts = Alerts(context)
            val token = db.getToken()
            description.text = curr.description
            name.text = curr.name
            date.text = curr.date

            for(i in curr.comments.indices){
                Log.e("postadapter", curr.comments[i].toString())
                commentAdapter.add(curr.comments[i])
            }

            viewAllComments.setOnClickListener {
                val commentsSheet = BottomSheetDialog(context)
                val commentsSheetView = LayoutInflater.from(context).inflate(R.layout.all_comments, null)
                commentsSheet.setContentView(commentsSheetView)
                commentsSheet.show()

                val commentsRecycler = commentsSheetView.findViewById<RecyclerView>(R.id.allCommentsRecycler)
                val commentsAdapter = CommentAdapter(mutableListOf())
                commentsRecycler.adapter = commentsAdapter
                commentsRecycler.layoutManager = LinearLayoutManager(context)

                val jsonObject = JSONObject()
                jsonObject.put("post_id", curr.post_id)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val comments = try{ RetrofitInstance.retro.getPostComments("Bearer $token", request) }
                    catch(e: SocketTimeoutException){
                        withContext(Dispatchers.Main){
                            alerts.timeout()
                        }
                        return@launch
                    }catch(e: Exception){
                        withContext(Dispatchers.Main){
                            alerts.error(e.toString())
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main){
                        for(i in comments.indices){
                            commentsAdapter.add(comments[i])
                        }
                    }
                }
            }


            Glide.with(context).load("http://tauconnect.online/$path").error(R.drawable.ic_baseline_account_circle_24).into(profilePicture)


            postComment.setOnClickListener{
                if(writeComment.text.toString().isEmpty()){
                    writeComment.error = "Please fill out this field."
                }else{

                    val jsonObject = JSONObject()
                    jsonObject.put("post_id", curr.post_id)
                    jsonObject.put("comment", writeComment.text.toString())
                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    CoroutineScope(Dispatchers.IO).launch {
                        val commentResponse = try{ RetrofitInstance.retro.postComment("Bearer $token",request)}
                        catch(e: SocketTimeoutException){
                            withContext(Dispatchers.Main){
                                alerts.timeout()
                            }
                            return@launch
                        }catch(e: Exception){
                            withContext(Dispatchers.Main){
                                alerts.error(e.toString())
                            }
                            return@launch
                        }

                        withContext(Dispatchers.Main){
                            writeComment.setText("")
                            commentAdapter.add(commentResponse)
                        }
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun add(item: PostsItem){
        list.add(0,item)
        notifyDataSetChanged()
    }
}