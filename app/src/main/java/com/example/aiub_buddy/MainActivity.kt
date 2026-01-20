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
import com.example.aiub_buddy.data.database.AppDatabase
import com.example.aiub_buddy.data.entity.FacultyEntity
import com.example.aiub_buddy.data.entity.RoutineEntity
import com.example.aiub_buddy.data.entity.StudentEntity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


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

        val test_email = "22-46838-1@student.aiub.edu";
        val test_pass = "123456"









        btn_login.setOnClickListener {
           // Toast.makeText(this, "Firebase test started", Toast.LENGTH_SHORT).show()
            user_name.setText(test_email)
            user_password.setText(test_pass)
            val database = Firebase.database(
                "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
            )
            val student = database.getReference("student")



            student.get()
                .addOnSuccessListener { snapshot ->

                    var flag = false
                    var logged_in_student : DataSnapshot?=null

                    for (student_data in snapshot.children) {
                        val email = student_data.child("email").getValue(String::class.java)
                        val password = student_data.child("password").getValue(String::class.java)



                        //Toast.makeText(this,"Email  : $email \n Password : $password",Toast.LENGTH_SHORT).show()
                        //Toast.makeText(this, "Firebase test started", Toast.LENGTH_SHORT).show()")

                        if (user_name.text.toString() == email && user_password.text.toString() == password) {

                            logged_in_student = student_data;
                            break;
                        }

                    }

                    if (logged_in_student!=null) {


                        val email = logged_in_student.child("email").getValue(String::class.java)
                        val firstName = logged_in_student.child("first_name").getValue(String::class.java)
                        val lastName = logged_in_student.child("last_name").getValue(String::class.java)
                        val profileImg = logged_in_student.child("profile_img").getValue(String::class.java)
                        val  student_id = logged_in_student.child("student_id").getValue(String::class.java)






                         val db = AppDatabase.getDatabase(this)
                        val student_entity = StudentEntity(
                            studentId = student_id!!,
                            email = email!!,
                            firstName = firstName!!,
                            lastName = lastName!!,
                            profileImg = profileImg
                        )



                        db.studentDao().insertStudent(student_entity)
                      //  Toast.makeText(this, "test 1", Toast.LENGTH_SHORT).show()


                        db.routineDao().deleteAll()

//                        val routines = AppDatabase.getDatabase(this)
//                            .routineDao()
//                            .getAllRoutine()

//                       val  user = AppDatabase.getDatabase(this)
//                           .studentDao()
//                           .getLoggedInStudent()
//
//                        Toast.makeText(this,"User : ${user?.firstName} ${user?.lastName} \n ID : ${user?.studentId}",Toast.LENGTH_SHORT).show()

                        val routine_snap = logged_in_student.child("routine")
                        for(routine_data in routine_snap.children){
                            val subject_id = routine_data.child("subject_id").getValue(String::class.java)
                            val subject = routine_data.child("subject_name").getValue(String::class.java)
                            val day = routine_data.child("day").getValue(String::class.java)
                           // Toast.makeText(this,"Subject : $subject_id \n Day : $day",Toast.LENGTH_SHORT).show()
                            //val time = routine_data.child("time").getValue(String::class.java)
                            val startTime = routine_data.child("time").child("starting_time").getValue(String::class.java)?:""
                            val endTime = routine_data.child("time").child("ending_time").getValue(String::class.java)?:""

                            val room = routine_data.child("room").getValue(String::class.java)
                           // Toast.makeText(this,"Subject : $subject \n Day : $day \n Time : $startTime - $endTime \n Room : $room",Toast.LENGTH_SHORT).show()
                            //runOnUiThread {
                            //    Toast.makeText(this, "$startTime - $endTime", Toast.LENGTH_SHORT).show()

                            val routineEntity = RoutineEntity(
                                subject_id = subject_id!!,
                                subject = subject!!,
                                day = day!!,
                                startTime = startTime,
                                endTime = endTime,
                                room = room!!
                            )

                            db.routineDao().insertRoutine(routineEntity)

                        }

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