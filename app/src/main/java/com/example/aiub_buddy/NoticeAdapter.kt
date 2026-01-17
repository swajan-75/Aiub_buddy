package com.example.aiub_buddy

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoticeAdapter(private val list: List<Notice>) : RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNoticeTitle)
        val tvDate: TextView = view.findViewById(R.id.tvNoticeDate)
        val tvDesc: TextView = view.findViewById(R.id.tvNoticeDesc)
        val tvLink: TextView = view.findViewById(R.id.tvNoticeLink)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notice_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notice = list[position]
        holder.tvTitle.text = notice.title
        holder.tvDate.text = "${notice.day} ${notice.month} ${notice.year}"
        holder.tvDesc.text = notice.description
        holder.tvLink.text = notice.link


        holder.tvLink.text = "Link"
        holder.tvLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.link))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
