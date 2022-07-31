package com.example.tauconnect

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.zxing.client.android.Intents.Scan.RESULT
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.SocketTimeoutException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var profilePicture: CircleImageView
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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val progress = LoadingScreen(requireContext())
        val alerts = Alerts(requireContext())


        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bytes = stream.toByteArray()
            val image = Base64.encodeToString(bytes,Base64.DEFAULT)


            progress.showLoadingScreen("Uploading...")
            val jsonObject = JSONObject()
            jsonObject.put("image", image)
            val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            CoroutineScope(Dispatchers.IO).launch {
                val imageResponse = try{ RetrofitInstance.retro.updateProfilePicture("Bearer $token", request) }
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
                    Glide.with(requireContext()).load("http://tauconnect.online/${imageResponse.profile_picture}").error(R.drawable.ic_baseline_account_circle_24).into(profilePicture)
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val moreOptions = view.findViewById<Button>(R.id.moreOptions)

        val db = UserDatabase(requireContext())
        val token = db.getToken()
        val progress = LoadingScreen(requireContext())
        val alerts = Alerts(requireContext())

        profilePicture = view.findViewById(R.id.profilePicture)
        val name = view.findViewById<TextView>(R.id.name)
        val email = view.findViewById<TextView>(R.id.email)
        val userType = view.findViewById<TextView>(R.id.userType)

        val complaintRecycler = view.findViewById<RecyclerView>(R.id.complaintsRecycler)
        val complaintAdapter = ComplaintAdapter(mutableListOf())
        val emptyComplaints = view.findViewById<TextView>(R.id.emptyComplaints)
        val updateProfilePicture = view.findViewById<Button>(R.id.updateProfilePicture)
        complaintRecycler.adapter = complaintAdapter
        complaintRecycler.layoutManager = LinearLayoutManager(requireContext())

        emptyComplaints.isVisible = false
        updateProfilePicture.setOnClickListener {
            if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1) }
            }else{
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 2)
            }
        }


        CoroutineScope(Dispatchers.IO).launch {
            val complaints = try{ RetrofitInstance.retro.getComplaints("Bearer $token") }

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
                if(complaints.size == 0){
                    emptyComplaints.isVisible = true
                }else{
                    emptyComplaints.isVisible = false
                    for(i in complaints.indices){
                        complaintAdapter.add(complaints[i])
                    }
                }

            }
        }


        progress.showLoadingScreen("Loading...")
        CoroutineScope(Dispatchers.IO).launch {
            var profile = try{RetrofitInstance.retro.getProfile("Bearer $token")}
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
                Glide.with(requireContext()).load("https://tauconnect.online/${profile.profile_picture}").error(R.drawable.ic_baseline_account_circle_24).into(profilePicture)
                name.text = profile.name
                email.text = profile.email
                userType.text = "(${profile.user_type})"
            }
        }

        moreOptions.setOnClickListener {
            val moreOptionsSheet = BottomSheetDialog(requireContext())
            val sheetView = LayoutInflater.from(requireContext()).inflate(R.layout.more_options, null)

            moreOptionsSheet.setContentView(sheetView)
            moreOptionsSheet.show()

            val logout = sheetView.findViewById<Button>(R.id.logout)
            val submitComplaint = sheetView.findViewById<Button>(R.id.submitComplaint)
            val updateProfile = sheetView.findViewById<Button>(R.id.updateProfile)
            updateProfile.setOnClickListener {
                val updateProfileDialog = AlertDialog.Builder(requireContext())
                val updateProfileView = LayoutInflater.from(requireContext()).inflate(R.layout.update_profile_form, null)
                updateProfileDialog.setView(updateProfileView)

                val showUpdateProfileDialog = updateProfileDialog.show()

                val editName = updateProfileView.findViewById<TextInputEditText>(R.id.name)
                val editEmail = updateProfileView.findViewById<TextInputEditText>(R.id.email)
                val editPassword = updateProfileView.findViewById<TextInputEditText>(R.id.password)
                val confirmPassword = updateProfileView.findViewById<TextInputEditText>(R.id.confirmPassword)
                val confirmEdit = updateProfileView.findViewById<Button>(R.id.confirm)



                confirmEdit.setOnClickListener {
                    if(editName.text.toString().isEmpty()){
                        editName.error = "Please fill out this field"
                    }else if(editEmail.text.toString().isEmpty()){
                        editEmail.error = "Please fill out this field"
                    }else if(editPassword.text.toString().isEmpty()){
                        editPassword.error = "Please fill out this field"
                    }else if(confirmPassword.text.toString().isEmpty()){
                        confirmPassword.error = "Please fill out this field"
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(editEmail.text.toString()).matches()){
                        editEmail.error = "Please enter a valid email address."
                    }else if(confirmPassword.text.toString() != editPassword.text.toString()){
                        editPassword.error = "Password mismatch"
                    }else{
                        progress.showLoadingScreen("Updating...")
                        val jsonObject = JSONObject()
                        jsonObject.put("name", editName.text.toString())
                        jsonObject.put("email", editEmail.text.toString())
                        jsonObject.put("password", editPassword.text.toString())
                        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                        CoroutineScope(Dispatchers.IO).launch {
                            val userResponse = try{RetrofitInstance.retro.updateProfile("Bearer $token", request)}
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
                                showUpdateProfileDialog.dismiss()
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Success")
                                    .setMessage("Profile successfully updated.")
                                    .setPositiveButton("OK", null)
                                    .show()

                                name.text = userResponse.name
                                email.text = userResponse.email
                            }
                        }
                    }
                }
            }

            submitComplaint.setOnClickListener {
                val complaintAlert = AlertDialog.Builder(requireContext())
                val complaintAlertView = LayoutInflater.from(requireContext()).inflate(R.layout.submit_complaint, null)
                complaintAlert.setView(complaintAlertView)
                val complaintAlertShow = complaintAlert.show()

                val complaintText = complaintAlertView.findViewById<TextInputEditText>(R.id.complaintText)
                val confirmComplaint = complaintAlertView.findViewById<Button>(R.id.confirmComplaint)

                confirmComplaint.setOnClickListener {
                    if(complaintText.text.toString().isEmpty()){
                        complaintText.error = "Please fill out this field."
                    }else{
                        progress.showLoadingScreen("Submitting...")
                        val jsonObject = JSONObject()
                        jsonObject.put("complaint", complaintText.text.toString())
                        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                        CoroutineScope(Dispatchers.IO).launch {
                            val complaintResponse = try{ RetrofitInstance.retro.submitComplaint("Bearer $token",request) }
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
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Success")
                                    .setMessage("Complaint submitted successfully.")
                                    .setPositiveButton("OK", null)
                                    .show()
                                complaintAdapter.add(complaintResponse)
                                emptyComplaints.isVisible = false
                                complaintAlertShow.dismiss()
                            }
                        }
                    }
                }
            }
            logout.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("YES"){_,_->
                        progress.showLoadingScreen("Logging Out...")

                        CoroutineScope(Dispatchers.IO).launch {
                            val logoutResponse = try{RetrofitInstance.retro.logout("Bearer $token")}
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
                                if(logoutResponse.isSuccessful){
                                    db.deleteAll()
                                    val intent = Intent(requireContext(), Login::class.java)
                                    startActivity(intent)
                                    activity?.finishAffinity()
                                }else{
                                    AlertDialog.Builder(requireContext())
                                        .setTitle("Error")
                                        .setMessage("Something went wrong.")
                                        .setPositiveButton("OK", null)
                                        .show()

                                }
                            }
                        }
                    }.setNegativeButton("NO", null)
                    .show()
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
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}