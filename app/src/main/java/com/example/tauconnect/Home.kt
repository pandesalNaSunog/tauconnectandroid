package com.example.tauconnect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emptyPosts = view.findViewById<TextView>(R.id.emptyPosts)
        emptyPosts.isVisible = false
        val postRecycler = view.findViewById<RecyclerView>(R.id.postsRecycler)
        val postAdapter = PostAdapter(mutableListOf())
        postRecycler.adapter = postAdapter
        postRecycler.layoutManager = LinearLayoutManager(requireContext())
        val progress = LoadingScreen(requireContext())
        val alerts = Alerts(requireContext())
        val postText = view.findViewById<TextInputEditText>(R.id.postText)
        val postPost = view.findViewById<Button>(R.id.postPost)
        val db = UserDatabase(requireContext())
        val token = db.getToken()

        postPost.setOnClickListener {
            if(postText.text.toString().isEmpty()){
                postText.error = "Please fill out this field"
            }else{
                progress.showLoadingScreen("Posting...")
                val jsonObject = JSONObject()
                jsonObject.put("description", postText.text.toString())
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                CoroutineScope(Dispatchers.IO).launch {
                    val postResponse = try{ RetrofitInstance.retro.writePost("Bearer $token", request) }
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
                        postAdapter.add(postResponse)
                        postText.text?.clear()
                    }
                }
            }
        }

        progress.showLoadingScreen("Loading...")
        CoroutineScope(Dispatchers.IO).launch {
            val posts = try{RetrofitInstance.retro.getPosts()}
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
                if(posts.isEmpty()){
                    emptyPosts.isVisible = true
                }else{
                    emptyPosts.isVisible = false
                    for(i in posts.indices){
                        postAdapter.add(posts[i])
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
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}