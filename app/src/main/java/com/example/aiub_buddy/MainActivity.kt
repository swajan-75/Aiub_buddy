package com.example.aiub_buddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import com.google.firebase.database.getValue


class MainActivity : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user_name = findViewById<EditText>(R.id.user_id);
        val user_password = findViewById<EditText>(R.id.password);
        val btn_login = findViewById<Button>(R.id.btn_login);
        val btn_register = findViewById<Button>(R.id.btn_register);

        btn_login.setOnClickListener {
           // Toast.makeText(this, "Firebase test started", Toast.LENGTH_SHORT).show()

            val database = Firebase.database(
                "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
            )
            val student = database.getReference("student")



            student.get()
                .addOnSuccessListener { snapshot ->

                    var flag = false

                    for (student_data in snapshot.children) {
                        val email = student_data.child("email").getValue(String::class.java)
                        val password = student_data.child("password").getValue(String::class.java)



                        //Toast.makeText(this,"Email  : $email \n Password : $password",Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this, "Firebase test started", Toast.LENGTH_SHORT).show()")

                        if (user_name.text.toString() == email && user_password.text.toString() == password) {
                            flag = true;
                            break;
                        }

                    }

                    if (flag) {




                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Unable To Connect Database", Toast.LENGTH_SHORT).show()
                }
        }


        btn_register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent);
            //finish();
        }

    }



}