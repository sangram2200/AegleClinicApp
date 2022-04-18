package com.aghadge.healthapp.records

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aghadge.healthapp.R

class ViewRecordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mDescription: TextView = itemView.findViewById(R.id.description);
    val mDate: TextView = itemView.findViewById(R.id.date);
    val mDownload: Button = itemView.findViewById(R.id.download);
}


