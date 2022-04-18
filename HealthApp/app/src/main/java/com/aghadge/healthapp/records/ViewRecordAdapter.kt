package com.aghadge.healthapp.records

import  android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aghadge.healthapp.R
import java.lang.reflect.Array.get


class ViewRecordAdapter(private var records: ArrayList<RecordData>) : RecyclerView.Adapter<ViewRecordHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewRecordHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.record_tile, parent , false)
        return ViewRecordHolder(layoutInflater)
    }

    override fun getItemCount(): Int = records.size


    override fun onBindViewHolder(holder: ViewRecordHolder, position: Int) {
        holder.mDescription.text = records[position].description
        holder.mDate.text = records[position].date
        holder.mDownload.setOnClickListener{
            downloadFile(holder.mDescription.context,
                records[position].description!!,".pdf",DIRECTORY_DOWNLOADS, records[position].url);

            Toast.makeText(holder.mDescription.context, "Download started check notifications", Toast.LENGTH_LONG).show()

        }
    }

    private fun downloadFile(
        context: Context,
        fileName: String,
        fileExtension: String,
        destinationDirectory: String?,
        url: String?
    ) {
        val downloadManager : DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            fileName + fileExtension
        )
        downloadManager.enqueue(request)
    }
}