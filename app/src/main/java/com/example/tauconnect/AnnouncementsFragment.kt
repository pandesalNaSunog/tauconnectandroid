package com.example.tauconnect

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
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
 * Use the [AnnouncementsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnnouncementsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_announcements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = UserDatabase(requireContext())
        val userType = db.getUserType()
        val token = db.getToken()
        val announcementCard = view.findViewById<CardView>(R.id.postAnnouncementCard)
        val postAnnouncementText = view.findViewById<TextInputEditText>(R.id.postAnnouncementText)
        val postAnnouncement = view.findViewById<Button>(R.id.postAnnouncement)
        val progress = LoadingScreen(requireContext())
        val alerts = Alerts(requireContext())

        val announcementRecycler = view.findViewById<RecyclerView>(R.id.announcementRecycler)
        val announcementAdapter = AnnouncementAdapter(mutableListOf())
        announcementRecycler.adapter = announcementAdapter
        announcementRecycler.layoutManager = LinearLayoutManager(requireContext())

        postAnnouncement.setOnClickListener {
            if(postAnnouncementText.text.toString().isEmpty()){
                postAnnouncement.error = "Please fill out this field"
            }else{
                progress.showLoadingScreen("Posting...")
                val jsonObject = JSONObject()
                jsonObject.put("announcement", postAnnouncementText.text.toString())
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    val announcement = try{ RetrofitInstance.retro.postAnnouncement("Bearer $token",request) }
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
                        announcementAdapter.add(announcement)
                        postAnnouncementText.text?.clear()
                    }
                }
            }
        }

        announcementCard.isVisible = userType != "Student"


        val emptyAnnouncements = view.findViewById<TextView>(R.id.emptyAnnouncements)

        emptyAnnouncements.isVisible = false


        progress.showLoadingScreen("Loading...")

        CoroutineScope(Dispatchers.IO).launch {
            val announcements = try{RetrofitInstance.retro.getAnnouncements()}
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
                for(i in announcements.indices){
                    announcementAdapter.add(announcements[i])
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
         * @return A new instance of fragment AnnouncementsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnnouncementsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}