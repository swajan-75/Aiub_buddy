package com.example.aiub_buddy

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        // 🔹 Student ID Pattern (matches 12-12345-1)
        val idPattern = Regex("\\d{2}-\\d{5}-\\d{1}")


        back.setOnClickListener {
            finish();
        }




        nextBtn.setOnClickListener {

            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()
            val sId = studentId.text.toString().trim()

            // 🔹 Validations
            when {
                fName.isEmpty() -> {
                    Toast.makeText(this, "Please enter First Name", Toast.LENGTH_SHORT).show()
                }

                lName.isEmpty() -> {
                    Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_SHORT).show()
                }

                !idPattern.matches(sId) -> {
                    Toast.makeText(
                        this,
                        "Invalid Student ID. Use format: 12-12345-1",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val intent = Intent(this, Email_varification::class.java)
                    intent.putExtra("studnet_id",sId)
                    startActivity(intent);
                    //finish()
                }
            }
        }
    }
}
