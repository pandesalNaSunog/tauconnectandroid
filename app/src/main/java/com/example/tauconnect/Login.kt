package com.example.tauconnect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.SocketTimeoutException

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val db = UserDatabase(this)

        if(db.getSize() > 0){
            val intent = Intent(this, Navigation::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val email = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.password);
        val login = findViewById<Button>(R.id.login)
        val alerts = Alerts(this)
        val loadingScreen = LoadingScreen(this)



        login.setOnClickListener {

            if(email.text.toString().isEmpty()){
                email.error = "Please fill out this field"
            }else if(password.text.toString().isEmpty()){
                password.error = "Please fill out this field"
            }else{
                loadingScreen.showLoadingScreen("Logging in...")


                val jsonObject = JSONObject()
                jsonObject.put("email", email.text.toString())
                jsonObject.put("password", password.text.toString())
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

                CoroutineScope(Dispatchers.IO).launch {
                    val loginDetails = try{RetrofitInstance.retro.login(request)}
                    catch(e: SocketTimeoutException){
                        withContext(Dispatchers.Main){
                            loadingScreen.dismiss()
                            alerts.timeout()
                        }
                        return@launch
                    }catch(e: HttpException){
                        withContext(Dispatchers.Main){
                            loadingScreen.dismiss()
                            AlertDialog.Builder(this@Login)
                                .setTitle("Error")
                                .setMessage("Invalid Email and Password")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                        return@launch
                    }catch(e: Exception){
                        withContext(Dispatchers.Main){
                            loadingScreen.dismiss()
                            alerts.error(e.toString())
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main){
                        loadingScreen.dismiss()

                        db.add(loginDetails)
                        val intent = Intent(this@Login, Navigation::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}