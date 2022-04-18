package com.aghadge.healthapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghadge.healthapp.database.AppointmentData
import com.aghadge.healthapp.utils.SecondFragmentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore


class SecondFragment : Fragment(){
    private lateinit var recyclerView: RecyclerView
    private var emptyView: TextView? = null
    private lateinit var appointmentList: ArrayList<AppointmentData>
    private lateinit var adapter: SecondFragmentAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_second,container,false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        emptyView = view.findViewById(R.id.empty_view)

        db = FirebaseFirestore.getInstance()
        appointmentList = arrayListOf<AppointmentData>()
        adapter = SecondFragmentAdapter(appointmentList)
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid

        recyclerView.adapter = adapter

        AppointmentListener()
        return view

    }

    private fun AppointmentListener() {
        db.collection("Users").document(userId).collection("Appointments")
            .addSnapshotListener(EventListener { value, error ->
                if(error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@EventListener
                }
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val note = dc.document.data.getValue("descriptiveNote").toString()
                            val appointmentTime = dc.document.data.getValue("appointmentTime").toString()
                            val patientName = dc.document.data.getValue("patientName").toString()
                            appointmentList.add(AppointmentData(patientName, note, appointmentTime))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                if(value!!.documentChanges.isEmpty()){
                    emptyView?.visibility = View.VISIBLE;
                    Log.d("exists", "DocumentSnapshot data: ${value.documentChanges}")
                }
            })
    }
}