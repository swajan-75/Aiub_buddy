package com.example.aiub_buddy

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aiub_buddy.data.entity.FacultyEntity

class FacultyAdapter(
    private var facultyList: List<FacultyEntity>
) : RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>() {

    // Keep a copy of the full list to filter against
    private var fullList = facultyList.toMutableList()

    inner class FacultyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.tvName)
        val roomTv : TextView = itemView.findViewById(R.id.tvRoom)
        val deptTv: TextView = itemView.findViewById(R.id.tvDepartment)

        val photoIv: ImageView = itemView.findViewById(R.id.ivPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faculty, parent, false)
        return FacultyViewHolder(view)
    }

    override fun getItemCount(): Int = facultyList.size

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val faculty = facultyList[position]
        holder.nameTv.text = faculty.name
        holder.deptTv.text = faculty.department
        //holder.roomTv.text = faculty.room_number
        holder.roomTv.text = "Room: ${faculty.room_number ?: "N/A"}"

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(faculty.profile_photo) // URL from API
            .placeholder(R.drawable.user_logo) // default placeholder
            .error(R.drawable.user_logo)       // fallback if loading fails
            .circleCrop() // make it circular (optional)
            .into(holder.photoIv)

        holder.itemView.setOnClickListener {
            faculty.profile_link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }




        holder.itemView.setOnClickListener {
            // Open profile link
            faculty.profile_link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }
    }

    // Update adapter with new data
    fun updateList(newList: List<FacultyEntity>) {
        facultyList = newList
        fullList = newList.toMutableList()
        notifyDataSetChanged()
    }

    // Filter function for search
    fun filter(query: String) {
        facultyList = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.department.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
