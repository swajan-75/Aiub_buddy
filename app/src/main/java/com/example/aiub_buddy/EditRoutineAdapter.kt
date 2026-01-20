package com.example.aiub_buddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EditRoutineAdapter(
    private val list: List<Routine>,
    private val onDelete: (Routine) -> Unit,
    private val onEdit: (Routine) -> Unit
) : RecyclerView.Adapter<EditRoutineAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCourseName: TextView = view.findViewById(R.id.tvCourseName)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvRoom: TextView = view.findViewById(R.id.tvRoom)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_routine_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val routine = list[position]

        holder.tvCourseName.text = routine.courseName
        holder.tvDay.text = routine.day
        holder.tvTime.text = "${routine.startTime} - ${routine.endTime}"
        holder.tvRoom.text = routine.roomNumber

        holder.btnDelete.setOnClickListener {
            onDelete(routine)
        }

        holder.btnEdit.setOnClickListener {
            onEdit(routine)
        }
    }

    override fun getItemCount(): Int = list.size
}
