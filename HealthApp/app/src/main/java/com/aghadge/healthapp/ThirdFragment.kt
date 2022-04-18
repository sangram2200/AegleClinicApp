package com.aghadge.healthapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aghadge.healthapp.database.Database
import com.aghadge.healthapp.records.RecordData
import com.aghadge.healthapp.records.ViewRecordAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class ThirdFragment : Fragment(){

    private var emptyView: TextView? = null
    private  val  mDatabase: Database = Database();
    private  lateinit var recordData: ArrayList<RecordData>;
    private lateinit  var mAdapter : ViewRecordAdapter;
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View =  inflater.inflate(R.layout.fragment_third,container,false)

        emptyView = view.findViewById(R.id.empty_view)
        db = FirebaseFirestore.getInstance()
        //setup recycle view
        val mRecyclerView : RecyclerView = view.findViewById(R.id.recycleView)
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.layoutManager = LinearLayoutManager(activity);

        recordData = arrayListOf<RecordData>()
        mAdapter = ViewRecordAdapter(recordData);
        mRecyclerView.adapter = mAdapter


        fetchData();
        return view
    }

    private fun fetchData() {
        val auth : FirebaseAuth = FirebaseAuth.getInstance();
        val user = auth.currentUser!!.uid
        val em: Boolean = mDatabase.fetchRecords(requireActivity().baseContext , recordData , user , mAdapter);

    }

}