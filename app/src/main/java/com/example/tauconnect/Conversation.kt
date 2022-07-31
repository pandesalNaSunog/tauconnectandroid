package com.example.tauconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

class Conversation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)
        val messageEdit = findViewById<EditText>(R.id.messageEdit)
        val send = findViewById<Button>(R.id.send)
        val userId = intent.getStringExtra("user_id")
        val userName = intent.getStringExtra("name")
        val progress = LoadingScreen(this)
        val alerts = Alerts(this)
        val db = UserDatabase(this)
        val token = db.getToken()
        val name = findViewById<TextView>(R.id.name)
        val conversationRecycler = findViewById<RecyclerView>(R.id.conversationRecycler)
        val conversationAdapter = MessageAdapter(mutableListOf())
        conversationRecycler.adapter = conversationAdapter
        conversationRecycler.layoutManager = LinearLayoutManager(this)
        val emptyMessages = findViewById<TextView>(R.id.noMessages)
        emptyMessages.isVisible = false

        send.setOnClickListener {
            if(messageEdit.text.toString().isEmpty()){
                messageEdit.error = "Please fill out this field."
            }else{
                val jsonObject = JSONObject()
                jsonObject.put("message", messageEdit.text.toString())
                jsonObject.put("receiver_id", userId)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val sendMessage = try{ RetrofitInstance.retro.sendMessage("Bearer $token", request) }
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
                        emptyMessages.isVisible = false
                        messageEdit.text.clear()
                        conversationAdapter.add(sendMessage)
                        conversationRecycler.scrollToPosition(conversationAdapter.itemCount - 1)
                    }
                }
            }
        }




        name.text = userName
        progress.showLoadingScreen("Loading...")

        val jsonObject = JSONObject()
        jsonObject.put("user_id", userId)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.IO).launch {
            val messages = try{ RetrofitInstance.retro.getConversation("Bearer $token", request) }
            catch(e: SocketTimeoutException){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.timeout()
                }
                return@launch
            }catch(e: Exception){
                withContext(Dispatchers.Main){
                    progress.dismiss()
                    alerts.error(e.toString())
                }
                return@launch
            }

            withContext(Dispatchers.Main){
                progress.dismiss()
                if(messages.size == 0){
                    emptyMessages.isVisible = true
                }else{
                    emptyMessages.isVisible = false
                    for(i in messages.indices){
                        conversationAdapter.add(messages[i])
                    }
                    conversationRecycler.scrollToPosition(messages.size - 1)
                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}