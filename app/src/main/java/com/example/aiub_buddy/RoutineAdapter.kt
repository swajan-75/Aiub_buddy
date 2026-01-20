package com.example.aiub_buddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoutineAdapter(private val list: List<Routine>) : RecyclerView.Adapter<RoutineAdapter.ViewHolder>() {

    // ViewHolder class
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCourseName: TextView = view.findViewById(R.id.tvCourseName)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvRoom: TextView = view.findViewById(R.id.tvRoom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.routine_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val routine = list[position] // get the Routine object
        holder.tvCourseName.text = routine.courseName
        holder.tvDay.text = routine.day
        holder.tvTime.text = routine.startTime + " - " + routine.endTime
        holder.tvRoom.text = routine.roomNumber
    }

    override fun getItemCount(): Int = list.size
}
