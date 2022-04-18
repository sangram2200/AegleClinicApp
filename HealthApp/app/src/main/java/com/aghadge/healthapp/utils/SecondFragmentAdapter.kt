package com.aghadge.healthapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghadge.healthapp.R
import com.aghadge.healthapp.database.AppointmentData

class SecondFragmentAdapter(val appointmentList: ArrayList<AppointmentData>) : RecyclerView.Adapter<SecondFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SecondFragmentAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_files, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecondFragmentAdapter.ViewHolder, position: Int) {
        val appointmentData: AppointmentData = appointmentList[position]
        holder.descriptionTextView.text = appointmentData.descriptiveNote
        holder.patientNameTextView.text = appointmentData.patientName
        holder.appointmentTime.text = appointmentData.appointmentTime
    }

    override fun getItemCount(): Int = appointmentList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView
        val patientNameTextView: TextView
        val appointmentTime: TextView
        init {
            descriptionTextView = itemView.findViewById(R.id.descriptionByDoctor)
            patientNameTextView = itemView.findViewById(R.id.appointmentPatientName)
            appointmentTime = itemView.findViewById(R.id.appointmentTime)
        }
    }
}