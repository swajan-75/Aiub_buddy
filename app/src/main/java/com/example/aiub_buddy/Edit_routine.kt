package com.example.aiub_buddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aiub_buddy.data.database.AppDatabase
import com.example.aiub_buddy.data.entity.RoutineEntity
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.aiub_buddy.data.dao.RoutineDao


class Edit_routine : AppCompatActivity() {

    private lateinit var rvEditRoutine : RecyclerView
    private lateinit var routineDao : RoutineDao

    private fun showAddRoutineDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_routine, null)

        val actvSubject = dialogView.findViewById<AutoCompleteTextView>(R.id.actvSubject)
        val spinnerDay = dialogView.findViewById<Spinner>(R.id.spinnerDay)
        val etTime = dialogView.findViewById<EditText>(R.id.etTime)
        val etRoom = dialogView.findViewById<EditText>(R.id.etRoom)

        // Days list
        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","None")
        spinnerDay.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            days
        )

        // Load subjects from DB
        Thread {
            val subjects = AppDatabase.getDatabase(this)
                .subjectDao()
                .getAllSubjects()

            val subjectNames = subjects.map { it.name }

            runOnUiThread {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    subjectNames
                )
                actvSubject.setAdapter(adapter)
            }
        }.start()

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Routine")
            .setView(dialogView)
            .setPositiveButton("Save", null) // override later
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val subject = actvSubject.text.toString().trim()
            val day = spinnerDay.selectedItem.toString()
            val time = etTime.text.toString().trim()
            val room = etRoom.text.toString().trim()

            // Validation with Toast
            when {
                subject.isEmpty() -> {
                    Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show()
                }
                day != "None" && time.isEmpty() -> {
                    Toast.makeText(this, "Please enter time", Toast.LENGTH_SHORT).show()
                }
                day != "None" && room.isEmpty() -> {
                    Toast.makeText(this, "Please enter room", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Save routine
                    val routine = RoutineEntity(
                        subject = subject,
                        day = day,
                        time = time,
                        room = room
                    )

                    Thread {
                        routineDao.insertRoutine(routine)
                        loadRoutines()
                    }.start()

                    dialog.dismiss()
                }
            }
        }
    }



    fun deleteRoutine(routine: Routine) {
        Thread {
            routineDao.deleteByDetails(
                routine.courseName,
                routine.day,
                routine.time,
                routine.roomNumber
            )
            loadRoutines()
        }.start()
    }

    private fun showEditRoutineDialog(routine: Routine) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_routine, null)

        val actvSubject = dialogView.findViewById<AutoCompleteTextView>(R.id.actvSubject)
        val spinnerDay = dialogView.findViewById<Spinner>(R.id.spinnerDay)
        val etTime = dialogView.findViewById<EditText>(R.id.etTime)
        val etRoom = dialogView.findViewById<EditText>(R.id.etRoom)

        // 1. Pre-fill existing data
        actvSubject.setText(routine.courseName)
        etTime.setText(routine.time)
        etRoom.setText(routine.roomNumber)

        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday","None")
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, days)
        spinnerDay.adapter = dayAdapter
        spinnerDay.setSelection(days.indexOf(routine.day))

        // 2. Setup Subject AutoComplete (same as your Add logic)
        Thread {
            val subjects = AppDatabase.getDatabase(this).subjectDao().getAllSubjects()
            val subjectNames = subjects.map { it.name }
            runOnUiThread {
                actvSubject.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjectNames))
            }
        }.start()

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Routine")
            .setView(dialogView)
            .setPositiveButton("Update", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            // ... Validation logic (same as your Add code) ...

            Thread {
                // Delete the old record first (since we don't have a primary key ID in your Routine class)
                routineDao.deleteByDetails(routine.courseName, routine.day, routine.time, routine.roomNumber)

                // Insert the updated version
                val updatedEntity = RoutineEntity(
                    subject = actvSubject.text.toString(),
                    day = spinnerDay.selectedItem.toString(),
                    time = etTime.text.toString(),
                    room = etRoom.text.toString()
                )
                routineDao.insertRoutine(updatedEntity)

                loadRoutines()
                runOnUiThread { dialog.dismiss() }
            }.start()
        }
    }
    fun loadRoutines() {
        Thread {
            val entities = routineDao.getAllRoutine()
            val routines = entities.map {
                Routine(123,it.subject, it.day, it.time, it.room)
            }

            runOnUiThread {
                rvEditRoutine.adapter = EditRoutineAdapter(routines,
                    onDelete = { routine -> deleteRoutine(routine) },
                    onEdit = { routine -> showEditRoutineDialog(routine) } // Call edit dialog
                )
            }
        }.start()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_routine)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvEditRoutine = findViewById(R.id.rvEditRoutine)
        rvEditRoutine.layoutManager = LinearLayoutManager(this)


        val db = AppDatabase.getDatabase(this)
        routineDao = db.routineDao()



        loadRoutines()
//        Thread {
//            val names = AppDatabase.getDatabase(this)
//                .subjectDao()
//                .getAllSubjects()
//                .map { it.name }
//
//            Log.d("SUBJECT_DB", "All Subjects: $names")
//        }.start()

        findViewById<ImageButton>(R.id.back_btn2).setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()

        }

        findViewById<ImageButton>(R.id.add_btn).setOnClickListener {
            showAddRoutineDialog()
        }


    }
}