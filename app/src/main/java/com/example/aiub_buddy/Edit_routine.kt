package com.example.aiub_buddy

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aiub_buddy.data.database.AppDatabase
import com.example.aiub_buddy.data.dao.RoutineDao
import com.example.aiub_buddy.data.entity.RoutineEntity
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class Edit_routine : AppCompatActivity() {

    private lateinit var rvEditRoutine: RecyclerView
    private lateinit var routineDao: RoutineDao

    /* ---------------- TIME PICKER ---------------- */

    private fun showTimePicker(target: EditText) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, h, m ->
                target.setText(String.format(Locale.US, "%02d:%02d", h, m))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    /* ---------------- ADD ROUTINE ---------------- */




    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updtae_firebase_routine(routineEntity: RoutineEntity){

        val new_subject = Subject(
            routineEntity.subject_id,
            routineEntity.subject,
            subject_time(
                routineEntity.startTime,
                routineEntity.endTime
            ),
            routineEntity.room,
            routineEntity.subject_id,
            routineEntity.day
            )




        val logged_in_student = AppDatabase.getDatabase(this).studentDao().getLoggedInStudent()
           val database = Firebase.database(
            "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
      val routine  = database.getReference("student").child(logged_in_student?.studentId.toString())
            .child("routine")//.push().setValue(new_subject)


        routine.child(routineEntity.subject_id.toLowerCase()).setValue(new_subject)




      //  Toast.makeText(this,"Student : ${logged_in_student?.studentId}",Toast.LENGTH_SHORT).show()


    }
    private fun delete_firebase_routine(routineEntity: RoutineEntity){
        val logged_in_student = AppDatabase.getDatabase(this).studentDao().getLoggedInStudent()
        val database = Firebase.database(
            "https://aiubbuddy-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        val routine  = database.getReference("student").child(logged_in_student?.studentId.toString())
            .child("routine")
        routine.child(routineEntity.subject_id.toLowerCase()).removeValue()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddRoutineDialog() {

        val view = layoutInflater.inflate(R.layout.dialog_add_routine, null)

        val subjectEt = view.findViewById<AutoCompleteTextView>(R.id.actvSubject)
        val daySpinner = view.findViewById<Spinner>(R.id.spinnerDay)
        val startEt = view.findViewById<EditText>(R.id.etStartTime)
        val endEt = view.findViewById<EditText>(R.id.etEndTime)
        val roomEt = view.findViewById<EditText>(R.id.etRoom)

        val days = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","None")
        daySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)

        startEt.setOnClickListener { showTimePicker(startEt) }
        endEt.setOnClickListener { showTimePicker(endEt) }

        // Load subjects
        Thread {
            val subjects = AppDatabase.getDatabase(this).subjectDao().getAllSubjects()
            runOnUiThread {
                subjectEt.setAdapter(
                    ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects.map { it.name })
                )
            }
        }.start()

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Routine")
            .setView(view)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            val subject = subjectEt.text.toString().trim()
            val day = daySpinner.selectedItem.toString()
            val start = startEt.text.toString()
            val end = endEt.text.toString()
            val room = roomEt.text.toString()

            if (subject.isEmpty() || day == "None" || start.isEmpty() || end.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = "$subject-$day-$start-$end-$end-$room"
                .lowercase(Locale.US)
                .replace(" ", "")

            val entity = RoutineEntity(

                subject_id = id,
                subject = subject,
                day = day,
                startTime = start,
                endTime = end,
                room = room
            )

            Thread {
                updtae_firebase_routine(entity)
                routineDao.insertRoutine(entity)
                loadRoutines()
            }.start()

            dialog.dismiss()
        }
    }

    /* ---------------- EDIT ROUTINE ---------------- */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEditRoutineDialog(routine: Routine) {
        //Toast.makeText(this,"subject Id : ${routine.subject_id}",Toast.LENGTH_SHORT).show()

        val view = layoutInflater.inflate(R.layout.dialog_add_routine, null)

        val subjectEt = view.findViewById<AutoCompleteTextView>(R.id.actvSubject)
        val daySpinner = view.findViewById<Spinner>(R.id.spinnerDay)
        val startEt = view.findViewById<EditText>(R.id.etStartTime)
        val endEt = view.findViewById<EditText>(R.id.etEndTime)
        val roomEt = view.findViewById<EditText>(R.id.etRoom)

        subjectEt.setText(routine.courseName)
        startEt.setText(routine.startTime)
        endEt.setText(routine.endTime)
        roomEt.setText(routine.roomNumber)

        startEt.setOnClickListener { showTimePicker(startEt) }
        endEt.setOnClickListener { showTimePicker(endEt) }

        val days = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","None")
        daySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
        daySpinner.setSelection(days.indexOf(routine.day))

        Thread {
            val subjects = AppDatabase.getDatabase(this).subjectDao().getAllSubjects()
            runOnUiThread {
                subjectEt.setAdapter(
                    ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects.map { it.name })
                )
            }
        }.start()




        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Routine")
            .setView(view)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            Thread {

                delete_firebase_routine(RoutineEntity(
                    subject_id = routine.subject_id,
                    subject = subjectEt.text.toString(),
                    day = daySpinner.selectedItem.toString(),
                    startTime = startEt.text.toString(),
                    endTime = endEt.text.toString(),
                    room = roomEt.text.toString()
                ))
                routineDao.deleteBySubjectId(routine.subject_id)

                val newId = "${subjectEt.text}-${daySpinner.selectedItem}-${startEt.text}-${endEt.text}-${roomEt.text}"
                    .lowercase(Locale.US).replace(" ","")

                routineDao.insertRoutine(
                    RoutineEntity(
                        subject_id = newId,
                        subject = subjectEt.text.toString(),
                        day = daySpinner.selectedItem.toString(),
                        startTime = startEt.text.toString(),
                        endTime = endEt.text.toString(),
                        room = roomEt.text.toString()
                    )
                )
                updtae_firebase_routine(RoutineEntity(
                    subject_id = newId,
                    subject = subjectEt.text.toString(),
                    day = daySpinner.selectedItem.toString(),
                    startTime = startEt.text.toString(),
                    endTime = endEt.text.toString(),
                    room = roomEt.text.toString()
                ))

                loadRoutines()
                runOnUiThread { dialog.dismiss() }
            }.start()
        }
    }

    /* ---------------- LOAD / DELETE ---------------- */

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteRoutine(routine: Routine) {
        Thread {
            delete_firebase_routine(RoutineEntity(
                subject_id = routine.subject_id,
                subject = routine.courseName,
                day = routine.day,
                startTime = routine.startTime,
                endTime = routine.endTime,
                room = routine.roomNumber
            ))
            routineDao.deleteBySubjectId(routine.subject_id)
            loadRoutines()
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRoutines() {
        Thread {
            val routines = routineDao.getAllRoutine().map {
                Routine(it.id ,it.subject_id, it.subject, it.day, it.startTime, it.endTime, it.room)
            }

            runOnUiThread {
                rvEditRoutine.adapter = EditRoutineAdapter(
                    routines,
                    onDelete = { deleteRoutine(it) },
                    onEdit = { showEditRoutineDialog(it) }
                )
            }
        }.start()
    }

    /* ---------------- ACTIVITY ---------------- */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_routine)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, i ->
            v.setPadding(
                i.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                i.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                i.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                i.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            i
        }

        rvEditRoutine = findViewById(R.id.rvEditRoutine)
        rvEditRoutine.layoutManager = LinearLayoutManager(this)

        routineDao = AppDatabase.getDatabase(this).routineDao()

        loadRoutines()

        findViewById<ImageButton>(R.id.add_btn).setOnClickListener {

            showAddRoutineDialog()
        }
        findViewById<ImageButton>(R.id.back_btn2).setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
            finish()
        }
    }
}
