package com.aghadge.healthapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.datepicker.MaterialDatePicker
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.aghadge.healthapp.database.Database
import com.aghadge.healthapp.database.UserData

import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mPickDateButton: Button
    private lateinit var mShowSelectedDateText: TextView
    private lateinit var mRegisterButton : Button
    private lateinit var mPatientName : EditText
    private lateinit var mGender : EditText
    private lateinit var mContactNumber : EditText
    private val mDatabase: Database = Database()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance()

        mRegisterButton = findViewById(R.id.mBtnRegister)
        // date picker
        mPickDateButton = findViewById(R.id.pick_date_button);
        mShowSelectedDateText = findViewById(R.id.show_selected_date);
        mPatientName = findViewById(R.id.patientName);
        mGender = findViewById(R.id.gender);
        mContactNumber = findViewById(R.id.contact_number);


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


        mRegisterButton.setOnClickListener{
            registerUser();
        }



    }
    fun closeKeyboard(view: View) {
        val hideMe = getSystemService( Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideMe.hideSoftInputFromWindow(view.windowToken, 0)

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun registerUser() {

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


