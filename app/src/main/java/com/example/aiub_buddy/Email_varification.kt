package com.example.aiub_buddy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class Email_varification : AppCompatActivity() {

    // 🔹 Retrofit interface for your NestJS OTP API
    interface OtpApi {
        @POST("otp/send")
        suspend fun sendOtp(@Body body: Map<String, String>): Map<String, String>

       // @FormUrlEncoded
        @POST("otp/verify")
        suspend fun verifyOtp(@Body body: Map<String, String>): Map<String, String>
    }

    private lateinit var otpEditText: EditText
    private lateinit var nextButton: Button
    private lateinit var backButton: ImageButton

    private var studentId: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null

    private var password : String? = null

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.203:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val otpApi = retrofit.create(OtpApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_email_varification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otpEditText = findViewById(R.id.editTextText4)
        nextButton = findViewById(R.id.button)
        backButton = findViewById(R.id.back_btn)

        // 🔹 Get data from registration screen
        studentId = intent.getStringExtra("student_id")
        firstName = intent.getStringExtra("first_name")
        lastName = intent.getStringExtra("last_name")
        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")




        backButton.setOnClickListener { finish() }

        // 🔹 Send OTP immediately when screen opens
       // studentId?.let { sendOtpToEmail(email!!) }
        sendOtpToEmail(email!!)

        nextButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()
            if (otp.length != 6) {
                Toast.makeText(this, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyOtpAndRegister(otp)
        }
    }

    private fun sendOtpToEmail(email: String) {
        Toast.makeText(this,"Email : $email",Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                otpApi.sendOtp(mapOf("email" to email))
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Email_varification, "OTP sent to $email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Email_varification, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verifyOtpAndRegister(otp: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 🔹 Call NestJS API with form fields
                val response = otpApi.verifyOtp(
                    mapOf(
                        "email" to email!!,
                        "otp" to otp
                    )
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Email_varification,
                        response.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // 🔹 OTP verified, now save user in Firebase
                saveUserInFirebase()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Email_varification,
                        "OTP Verified! User Registered.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Redirect to Dashboard or Login
                    val intent = Intent(this@Email_varification, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Email_varification,
                        e.message ?: "OTP Verification Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun saveUserInFirebase() {
        val database = FirebaseDatabase.getInstance(
            "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        val studentRef = database.getReference("student")

        val key = studentId ?: return

        val newUser = hashMapOf(
            "email" to email,
            "first_name" to firstName,
            "last_name" to lastName,
            "password" to password,
            "profile_img" to "",
            "routine" to hashMapOf<String, Any>(), // ✅ MUST be an object
            "student_id" to key
        )

        studentRef.child(key)
            .setValue(newUser)
            .addOnSuccessListener {
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firebase error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}
