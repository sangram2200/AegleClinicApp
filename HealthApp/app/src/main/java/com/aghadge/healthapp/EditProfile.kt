package com.aghadge.healthapp

import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aghadge.healthapp.database.Database
import com.aghadge.healthapp.database.UserData
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.jar.Attributes.Name


class EditProfile : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mPickDateButton: Button
    private lateinit var mShowSelectedDateText: TextView
    private lateinit var mSubmitButton : Button
    private lateinit var mPatientName : EditText
    private lateinit var mGender : EditText
    private lateinit var mContactNumber : EditText
    private val mDatabase: Database = Database()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance()

        val db= FirebaseFirestore.getInstance()

        val firebaseUser: FirebaseUser = mAuth.currentUser!!

        val userID = firebaseUser.uid

        mSubmitButton = findViewById(R.id.mBtnRegister)
        // date picker
        mPickDateButton = findViewById(R.id.pick_date_button);
        mShowSelectedDateText = findViewById(R.id.show_selected_date);
        mPatientName = findViewById(R.id.name);
        mGender = findViewById(R.id.gender);
        mContactNumber = findViewById(R.id.contact_number);

        val docRef = db.collection("Users").document(firebaseUser.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("exists", "DocumentSnapshot data: ${document.data}")

                    mPatientName.setText(document.getString("patientName"))
                    mGender.setText(document.getString("gender"))
                    mShowSelectedDateText.setText(document.getString("birthDate"))
                    mContactNumber.setText(document.getString("contactNumber"))
                } else {
                    Log.d("noExists", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("errorDb", "get failed with ", exception)
            }

        // date picker
        val picker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Birth date")
                .build();

        mPickDateButton.setOnClickListener{
            picker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        };

        picker.addOnPositiveButtonClickListener{
            mShowSelectedDateText.text = picker.headerText;
        }


        mSubmitButton.setOnClickListener{
            editProfile();
        }
    }

    private fun editProfile() {
        val name: String = mPatientName.text.toString().trim { it <= ' ' }
        val gender: String = mGender.text.toString().trim { it <= ' ' }
        val contact: String = mContactNumber.text.toString().trim { it <= ' ' }
        val birthDate: String = mShowSelectedDateText.text.toString()

        if(name.isEmpty())
        {
            mPatientName.error = "Field is required";
            mPatientName.requestFocus();
            return;
        }

        if(gender.isEmpty())
        {
            mGender.error = "Field is required";
            mGender.requestFocus();
            return;
        }


        if(contact.isEmpty())
        {
            mContactNumber.error = "Field is required";
            mContactNumber.requestFocus();
            return;
        }


        if(birthDate == "Birth Date")
        {
            Toast.makeText(this, "Select Birth Date", Toast.LENGTH_SHORT).show()
            return;
        }

        val firebaseUser: FirebaseUser = mAuth.currentUser!!

        val userId = firebaseUser.uid;

        val user =  UserData(name , gender , birthDate , contact );

        mDatabase.setNewUser( this ,  user, userId);

        Handler().postDelayed({
            //doSomethingHere()
            finish()
        }, 2000)
    }
}