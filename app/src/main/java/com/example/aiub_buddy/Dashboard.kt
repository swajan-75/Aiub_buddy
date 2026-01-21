package com.example.aiub_buddy

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aiub_buddy.api_service.ApiService
import com.example.aiub_buddy.data.dao.RoutineDao
import com.example.aiub_buddy.data.database.AppDatabase
import com.example.aiub_buddy.data.entity.FacultyEntity
import com.example.aiub_buddy.data.seed.SubjectSeeder

// Retrofit Networking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Dashboard : AppCompatActivity() {

    private lateinit var rvRoutine: RecyclerView
    private lateinit var rvNotices: RecyclerView
    lateinit var dayButtons: List<Button>
    private lateinit var tvNoClass: TextView
    private lateinit var routineDao: RoutineDao


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val facultyRecycler = findViewById<RecyclerView>(R.id.rvFaculty) // Make sure your RecyclerView in XML has id rvFaculty
        facultyRecycler.layoutManager = LinearLayoutManager(this)


        val facultyAdapter = FacultyAdapter(emptyList())
        facultyRecycler.adapter = facultyAdapter

// Full list to keep all faculty
        val allFacultyList = mutableListOf<FacultyEntity>()

        val apiService = ApiService()
        apiService.getAllFaculty(
            onSuccess = { facultyList ->
                allFacultyList.clear()
                allFacultyList.addAll(facultyList.map {
                    FacultyEntity(
                        email = it.email,
                        name = it.name,
                        faculty = it.faculty,
                        designation = it.designation,
                        position = it.position,
                        department = it.department,
                        profile_photo = it.profile_photo,
                        profile_link = it.profile_link,
                        room_number = it.room_number,
                        building_number = it.building_number,
                        academic_interests = it.academic_interests.joinToString(","),
                        research_interests = it.research_interests.joinToString(",")
                    )
                })

                // Update adapter with full list
                runOnUiThread {
                    facultyAdapter.updateList(allFacultyList)
                }
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )

        val searchEditText = findViewById<EditText>(R.id.search_faculty)
        searchEditText.addTextChangedListener { text ->
            val query = text.toString()
            if (query.isEmpty()) {
                facultyRecycler.visibility = View.GONE
                findViewById<View>(R.id.facultyBackground).visibility = View.GONE
            } else {
                facultyRecycler.visibility = View.VISIBLE
                findViewById<View>(R.id.facultyBackground).visibility = View.VISIBLE
                facultyAdapter.filter(query)
            }
        }





        tvNoClass = findViewById<TextView>(R.id.tvNoClass)
        fun updateRoutineUI(list: List<Routine>) {
            findViewById<TextView>(R.id.user_name_tv).text = AppDatabase.getDatabase(this).studentDao().getLoggedInStudent()?.firstName

            if (list.isEmpty()) {

                tvNoClass.visibility = View.VISIBLE
            } else {

                tvNoClass.visibility = View.GONE

            }
            rvRoutine.adapter = RoutineAdapter(list)
        }



        val settings_btn = findViewById<Button>(R.id.button2)
        settings_btn.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_dashboard, popupMenu.menu)


            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.profile -> {
                        Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.logout -> {
                        logoutUser()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }









        val db = AppDatabase.getDatabase(this)
        val subjectDao = db.subjectDao()

        Thread {
            val subjects = SubjectSeeder.getSubjects()
            if(subjectDao.getAllSubjects().isEmpty()){
                subjectDao.insertAll(subjects)
            }
        }.start()

        dayButtons = listOf(
            findViewById(R.id.button6),  // Sunday
            findViewById(R.id.button7),  // Monday
            findViewById(R.id.button8),  // Tuesday
            findViewById(R.id.button9),  // Wednesday
            findViewById(R.id.button10), // Thursday
            findViewById(R.id.button11), // Friday
            findViewById(R.id.button12)  // Saturday
        )

        fun highlightButton(selected : Button){
            dayButtons.forEach {
                it.background = getDrawable(R.drawable.card_background)
                it.setTextColor(getColor(android.R.color.black))
            }
            selected.background = getDrawable(R.drawable.card_background_selected)
            selected.setTextColor(getColor(android.R.color.white))

        }










        fun fetchNotices() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://aiub-public-api.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(NoticeApiService::class.java)

            service.getNotices().enqueue(object : Callback<NoticeApiResponse> {
                override fun onResponse(call: Call<NoticeApiResponse>, response: Response<NoticeApiResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {

                        // Extract the list from the "data" field
                        val apiData = response.body()?.data ?: emptyList()

                        // Map to your local Notice class used by the Adapter
                        val noticeList = apiData.map { item ->
                            Notice(
                                day = item.date,        // Mapping "date" to "day"
                                month = item.month,
                                year = item.year,
                                title = item.title,
                                description = item.desc, // Mapping "desc" to "description"
                                link = item.link
                            )
                        }

                        runOnUiThread {
                            rvNotices.adapter = NoticeAdapter(noticeList)
                        }
                    } else {
                        Log.e("API_ERROR", "Status: ${response.body()?.status}")
                    }
                }

                override fun onFailure(call: Call<NoticeApiResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Network failure: ${t.message}")
                    Toast.makeText(this@Dashboard, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }



//
//        val routineList = listOf(
//            Routine("Math", "Monday", "10:00 - 11:00", "Room 101"),
//            Routine("Physics", "Tuesday", "12:00 - 1:00", "Room 102"),
//            Routine("Chemistry", "Wednesday", "2:00 - 3:00", "Room 103"),
//            Routine("English", "Saturday", "9:00 - 10:00", "Room 104")
//        )

        val routine = db.routineDao().getAllRoutine();
        val routineList = routine.map {
            Routine(it.id,it.subject_id,it.subject,it.day , it.startTime,it.endTime,it.room)
        }


        fun filter_routine_by_day(day : String , routineList : List<Routine>): List<Routine> {
            val filtered_list = routineList.filter { it.day.uppercase() == day.uppercase() }
            return filtered_list;
        }

        // ---- Routine RecyclerView ----
        rvRoutine = findViewById(R.id.rvRoutine)
        rvRoutine.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)




        val today = java.time.LocalDate.now().dayOfWeek.name;
        val todays_list = filter_routine_by_day(today, routineList)

        updateRoutineUI(todays_list)

        when (today) {
            "SUNDAY" -> highlightButton(findViewById(R.id.button6))
            "MONDAY" -> highlightButton(findViewById(R.id.button7))
            "TUESDAY" -> highlightButton(findViewById(R.id.button8))
            "WEDNESDAY" -> highlightButton(findViewById(R.id.button9))
            "THURSDAY" -> highlightButton(findViewById(R.id.button10))
            "FRIDAY" -> highlightButton(findViewById(R.id.button11))
            "SATURDAY" -> highlightButton(findViewById(R.id.button12))
        }

        // ---- routine button ---

        findViewById<Button>(R.id.button6).setOnClickListener {

            highlightButton(findViewById(R.id.button6))
            updateRoutineUI(filter_routine_by_day("Sunday", routineList))

        }
        findViewById<Button>(R.id.button7).setOnClickListener {
            highlightButton(findViewById(R.id.button7))

            updateRoutineUI(filter_routine_by_day("Monday", routineList))
        }
        findViewById<Button>(R.id.button8).setOnClickListener {
            highlightButton(findViewById(R.id.button8))
            updateRoutineUI(filter_routine_by_day("Tuesday", routineList))
        }
        findViewById<Button>(R.id.button9).setOnClickListener {
            highlightButton(findViewById(R.id.button9))
            updateRoutineUI(filter_routine_by_day("Wednesday", routineList))
        }
        findViewById<Button>(R.id.button10).setOnClickListener {
            highlightButton(findViewById(R.id.button10))
            updateRoutineUI( filter_routine_by_day("Thursday", routineList))
        }
        findViewById<Button>(R.id.button11).setOnClickListener {
            highlightButton(findViewById(R.id.button11))
            updateRoutineUI(filter_routine_by_day("Friday", routineList))
        }
        findViewById<Button>(R.id.button12).setOnClickListener {

            highlightButton(findViewById(R.id.button12))
            updateRoutineUI(filter_routine_by_day("Saturday", routineList))
        }
        findViewById<Button>(R.id.button13).setOnClickListener {
            highlightButton(findViewById(R.id.button13))
            updateRoutineUI(filter_routine_by_day("NONE", routineList))
        }


        findViewById<ImageButton>(R.id.edit_routine).setOnClickListener {
            val intent = Intent(this, Edit_routine::class.java)
            startActivity(intent)
            finish()
        }






        // ---- Notice RecyclerView ----
        rvNotices = findViewById(R.id.rvNotices)
        rvNotices.layoutManager = LinearLayoutManager(this)
        rvNotices.adapter = NoticeAdapter(emptyList())

        fetchNotices()

//        val noticeList = listOf(
//            Notice("07", "Dec", "2025", "Seat Plan of Final-Term Exams of Fall 2025-26 [ ...",
//                "Please contact Exam Control Room if your name or course is not in the list Annex 5 Exam Control Room # ...",
//                "https://www.aiub.edu/seat-plan-of-final-term-exams-of-fall-2025-26--only-for-llb-and-bpharm--day-1--oct-12-2025--monday-"
//            ),
//            Notice("04", "Dec", "2025", "Final-Term Exam Schedule of Fall 2025-26 [LLB & ...",
//                "Final-Term Exam Schedule of Fall 2025-26",
//                "https://www.aiub.edu/final-term-exam-schedule-of-fall-2025-26-llb--b-pharma-onlypublished-on-dec-03-2025"
//            ),
//            Notice("03", "Dec", "2025", "Admission Test Written Exam of Spring 2025-26 ...",
//                "Admission Test WRITTEN RESULT Spring 2025-26 [Slot-1] will be published on December 08, 2025 (Monday) ...",
//                "https://www.aiub.edu/admission-test-written-exam-of-spring-2025-26-slot-1"
//            ),
//            Notice("02", "Dec", "2025", "Final-term Exam Permit for Fall 2025-26 for LLB ...",
//                "Final-term Exam Permit for Fall 2024-25 (Only for LLB and BPharm) will be available from VUES office from Tuesday (02 12 ...",
//                "https://www.aiub.edu/final-term-exam-permit-for-fall-2025-26-for-llb-and-bpharm-students"
//            ),
//            Notice("30", "Nov", "2025", "Seat Plan of Mid-Term Exams of Fall 2025-26 ...",
//                "Please contact Exam Control Room if your name or course is not in the list Annex 2 Exam Control Room # ...",
//                "https://www.aiub.edu/seat-plan-of-mid-term-exams-of-fall-2025-26-except-llb--b-pharm-day-7-dec-01-2025--monday-"
//            ),
//            Notice("30", "Nov", "2025", "FST Midterm Set-B Exam :: Fall 2025-26",
//                "CSE, DS, and CNCS Set-B",
//                "https://www.aiub.edu/fst-midterm-set-b-exam--fall-2025-26"
//            ),
//            Notice("30", "Nov", "2025", "Result of Merit Based Scholarship Examination ...",
//                "Merit Based Scholarship Examination Result Summer 2024-25 & Fall 2025-26 Based on the examination ...",
//                "https://www.aiub.edu/result-of-merit-based-scholarship-examination-for-summer-2024-2025--fall-2025-2026-"
//            ),
//            Notice("29", "Nov", "2025", "Seat Plan of Mid-Term Exams of Fall 2025-26 ...",
//                "Please contact Exam Control Room if your name or course is not in the list Annex 2 Exam Control Room # ...",
//                "https://www.aiub.edu/seat-plan-of-mid-term-exams-of-fall-2025-26-except-llb--b-pharm-day-6-nov-30-2025--sunday-"
//            ),
//            Notice("26", "Nov", "2025", "Seat Plan of Mid-Term Exams of Fall 2025-26 ...",
//                "Please contact Exam Control Room if your name or course is not in the list Annex 2 Exam Control Room # ...",
//                "https://www.aiub.edu/seat-plan-of-mid-term-exams-of-fall-2025-26-except-llb--b-pharm-day-5-nov-27-2025--thursday-"
//            ),
//            Notice("26", "Nov", "2025", "FASS Set B Mid-Term Exam for Fall 2025-26",
//                "Click here for details",
//                "https://www.aiub.edu/fass-set-b-mid-term-exam-for-fall-2025-26"
//            )
//        )

        //val noticeList =

        //rvNotices.adapter = NoticeAdapter(noticeList)
    }
    private fun logoutUser() {
        Thread {
            val db = AppDatabase.getDatabase(this)

            // Clear the student and routine tables
            db.studentDao().logout()
            db.routineDao().deleteAll()

            runOnUiThread {
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                // Redirect to login screen and clear back stack
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }.start()
    }
}
