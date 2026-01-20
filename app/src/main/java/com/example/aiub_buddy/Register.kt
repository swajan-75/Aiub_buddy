package com.example.aiub_buddy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔹 Get Views
        val firstName = findViewById<EditText>(R.id.first_name)
        val lastName = findViewById<EditText>(R.id.last_name)
        val studentId = findViewById<EditText>(R.id.student_id)
        val nextBtn = findViewById<Button>(R.id.next)
        val back = findViewById<ImageButton>(R.id.back_btn)
        val password = findViewById<EditText>(R.id.password)
        val email = findViewById<EditText>(R.id.email)

        // 🔹 Student ID Pattern (matches 12-12345-1)
        val idPattern = Regex("\\d{2}-\\d{5}-\\d{1}")
        val namePattern = Regex("^[A-Za-z ]+$")
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        val database = FirebaseDatabase.getInstance(
            "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        val studentRef = database.getReference("student")




        back.setOnClickListener {
            finish();
        }


        nextBtn.setOnClickListener {

            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()
            val sId = studentId.text.toString().trim()
            val emailText = email.text.toString().trim()
            val pass = password.text.toString().trim()

            when {
                fName.isEmpty() || !namePattern.matches(fName) -> {
                    Toast.makeText(this, "First name must contain only letters", Toast.LENGTH_SHORT).show()
                }

                lName.isEmpty() || !namePattern.matches(lName) -> {
                    Toast.makeText(this, "Last name must contain only letters", Toast.LENGTH_SHORT).show()
                }

                !idPattern.matches(sId) -> {
                    Toast.makeText(this, "Invalid Student ID format (12-12345-1)", Toast.LENGTH_SHORT).show()
                }

                !emailPattern.matches(emailText) -> {
                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                }

                pass.length < 6 -> {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }

                else -> {

                    checkUserExists(studentRef, sId, emailText, fName, lName, pass)
                }
            }
        }

    }
    private fun checkUserExists(
        studentRef: com.google.firebase.database.DatabaseReference,
        studentId: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String
    ) {
        studentRef.get().addOnSuccessListener { snapshot ->

            // 🔹 Check Student ID
            if (snapshot.hasChild(studentId)) {
                Toast.makeText(this, "Student ID already registered", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // 🔹 Check Email
            for (child in snapshot.children) {
                val existingEmail = child.child("email").getValue(String::class.java)
                if (existingEmail == email) {
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
            }

            // ✅ All good → proceed
            val intent = Intent(this, Email_varification::class.java)
            intent.putExtra("student_id", studentId)
            intent.putExtra("email", email)
            intent.putExtra("first_name", firstName)
            intent.putExtra("last_name", lastName)
            intent.putExtra("password", password)
            startActivity(intent)

        }.addOnFailureListener {
            Toast.makeText(this, "Firebase error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
