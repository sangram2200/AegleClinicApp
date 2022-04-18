package com.aghadge.healthapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.aghadge.healthapp.database.AppointmentData
import com.aghadge.healthapp.database.Database
import com.aghadge.healthapp.database.UserData
import com.aghadge.healthapp.database.newHealthData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_health_data.*


class HealthDataFragment : Fragment() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mPickDateButton: Button
    private lateinit var mShowSelectedDateText: TextView
    private lateinit var mSubmitButton : Button

    private val mDatabase: Database = Database()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        return inflater.inflate(R.layout.fragment_health_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val db= FirebaseFirestore.getInstance()

        val firebaseUser: FirebaseUser = mAuth.currentUser!!

        val userID = firebaseUser.uid
        db.collection("Users").document(userID).collection("health_data")
            .addSnapshotListener(
                EventListener { value, error ->
                if(error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@EventListener
                }
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            weight.setText(dc.document.data.getValue("weight").toString())
                            height.setText(dc.document.data.getValue("height").toString())
                            bloodgroup.setText(dc.document.data.getValue("bloodGroup").toString())
                        }
                    }
                }
            })

        mBtnHealth.setOnClickListener {
            editProfile()
        }
    }

    private fun editProfile() {
        val mWeight: String = weight.text.toString().trim { it <= ' ' }
        val mHeight: String = height.text.toString().trim { it <= ' ' }
        val mBloodGroup: String = bloodgroup.text.toString().trim { it <= ' ' }

        if (mWeight.isEmpty()) {
            weight.error = "Field is required";
            weight.requestFocus();
            return;
        }

        if (mHeight.isEmpty()) {
            height.error = "Field is required";
            height.requestFocus();
            return;
        }


        if (mBloodGroup.isEmpty()) {
            bloodgroup.error = "Field is required";
            bloodgroup.requestFocus();
            return;
        }

        val firebaseUser: FirebaseUser = mAuth.currentUser!!

        val userId = firebaseUser.uid;

        val userHealth =  newHealthData(mWeight , mHeight , mBloodGroup );

        activity?.let { mDatabase.setNewHealthData(it,userHealth,userId) }

        Handler().postDelayed({
            //doSomethingHere()
            startActivity(Intent(activity, Home::class.java))
        }, 500)

    }


}