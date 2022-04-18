package com.aghadge.healthapp.database

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aghadge.healthapp.Home
import com.aghadge.healthapp.R
import com.aghadge.healthapp.RegistrationActivity
import com.aghadge.healthapp.records.RecordData
import com.aghadge.healthapp.records.ViewRecordAdapter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Database() {

    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()

     fun setNewUser( context: Context  ,user: UserData , userId : String){
        db.collection("Users").document(userId).set(user)
            .addOnSuccessListener {
                val intent = Intent(context , Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(context, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    fun setNewHealthData(context: Context  ,newHealthData: newHealthData , userId : String){
        val query: CollectionReference = db.collection("Users").document(userId).collection("health_data")

        db.collection("Users").document(userId).collection("health_data").document("health").set(newHealthData).addOnSuccessListener {
            val intent = Intent(context , Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
            Toast.makeText(context, "Health Data Added", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(context, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    fun firstLogin(context: Context  , userId: String){

        db.collection("Users").document(userId).get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    context.startActivity(Intent(context, Home::class.java))
                } else {
                    context.startActivity(Intent(context, RegistrationActivity::class.java))
                }
            } else {
                Toast.makeText(context , "Oops something wrong happened" , Toast.LENGTH_SHORT).show();
                Log.d("Failed with: ", Objects.requireNonNull(task.exception).toString())
            }
        })
    }

    fun setNewAppointment(context: Context , appointmentData: AppointmentData ,userId: String, newAppointmentData: NewAppointmentData, appointmentId: String){
            val query: CollectionReference = db.collection("Users").document(userId).collection("Appointments")

            query.document(appointmentId).set(appointmentData)
            .addOnSuccessListener {
                val intent = Intent(context , Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
//                Toast.makeText(context, "Appointment booked for ${appointmentData.appointmentTime}", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{
                Toast.makeText(context, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }

        db.collection("Appointments").document(appointmentId).set(newAppointmentData)
            .addOnSuccessListener {
                val intent = Intent(context , Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(intent)
                Toast.makeText(context, "New Appointment booked for ${appointmentData.appointmentTime}", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{
                Toast.makeText(context, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    fun fetchRecords(
        context: Context?, records: ArrayList<RecordData>,
        userId: String, adapter: ViewRecordAdapter): Boolean {


        val query: CollectionReference = db.collection("Users").document(userId).collection("record_files")

             query.addSnapshotListener(EventListener { value, error ->
                 if (error != null) {
                     Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                     return@EventListener

                 }
                 if (value != null) {
                     for (dc: DocumentChange in value.documentChanges) {
                         if (dc.type == DocumentChange.Type.ADDED) {

                             val description = dc.document.data.getValue("description").toString()
                             val date = dc.document.data.getValue("date").toString()
                             val url = dc.document.data.getValue("url").toString()
                             records.add(RecordData(description, url, date));
                         }
                     }
                     adapter.notifyDataSetChanged()

                 }
             })
        if(records.isEmpty()) return false
        return true
    }
}