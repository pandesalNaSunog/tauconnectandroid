package com.example.tauconnect

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Messages.newInstance] factory method to
 * create an instance of this fragment.
 */
class Messages : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val usersRecycler = view.findViewById<RecyclerView>(R.id.usersRecycler)
        val userAdapter = UsersAdapter(mutableListOf())
        usersRecycler.adapter = userAdapter
        usersRecycler.layoutManager = LinearLayoutManager(requireContext())
        val progress = LoadingScreen(requireContext())
        val alerts = Alerts(requireContext())
        val writeMessage = view.findViewById<Button>(R.id.writeMessage)
        val emptyUsers = view.findViewById<TextView>(R.id.emptyUsers)

        writeMessage.setOnClickListener {
            val createConvoAlert = AlertDialog.Builder(requireContext())
            val createConvoAlertView = LayoutInflater.from(requireContext()).inflate(R.layout.create_conversation, null)

            createConvoAlert.setView(createConvoAlertView)

            val showCreateConvoAlert = createConvoAlert.show()

            val to = createConvoAlertView.findViewById<Button>(R.id.to)
            val message = createConvoAlertView.findViewById<TextInputEditText>(R.id.messageText)
            val send = createConvoAlertView.findViewById<Button>(R.id.send)

            to.setOnClickListener {

                val usersToMessageAlert = AlertDialog.Builder(requireContext())
                val usersToMessageAlertView = LayoutInflater.from(requireContext()).inflate(R.layout.new_users_to_message, null)
                usersToMessageAlert.setView(usersToMessageAlertView)
                val showUsersToMessageAlert = usersToMessageAlert.show()

                val usersToMessageRecycler = usersToMessageAlertView.findViewById<RecyclerView>(R.id.usersToMessageRecycler)
                val usersToMessageAdapter = UsersToMessageAdapter(mutableListOf(), to, showUsersToMessageAlert)

                usersToMessageRecycler.adapter = usersToMessageAdapter
                usersToMessageRecycler.layoutManager = LinearLayoutManager(requireContext())


                CoroutineScope(Dispatchers.IO).launch {
                    val user = try{RetrofitInstance.retro.getNewUsersToMessage("Bearer $token")}
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
                        for(i in user.indices) {
                            usersToMessageAdapter.add(user[i])
                        }
                    }
                }
            }

            send.setOnClickListener {
                if(to.tag == 0){
                    to.error = "Please choose a recipient"
                }else if(message.text.toString().isEmpty()){
                    message.error = "Please fill out this field"
                }else{
                    progress.showLoadingScreen("Sending...")
                    val jsonObject = JSONObject()
                    jsonObject.put("receiver_id", to.tag)
                    jsonObject.put("message", message.text.toString())
                    val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = try{RetrofitInstance.retro.sendNewMessage("Bearer $token", request)}
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
                            userAdapter.add(user)

                            showCreateConvoAlert.dismiss()
                            emptyUsers.isVisible = false
                        }
                    }
                }
            }
        }




        emptyUsers.isVisible = false

        progress.showLoadingScreen("Loading...")
        CoroutineScope(Dispatchers.IO).launch {
            val users = try{ RetrofitInstance.retro.getUsers("Bearer $token") }
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
                if(users.size == 0){
                    emptyUsers.isVisible = true
                }else{
                    emptyUsers.isAllCaps = false
                    for(i in users.indices){
                        userAdapter.add(users[i])
                    }
                }

            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Messages.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Messages().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}