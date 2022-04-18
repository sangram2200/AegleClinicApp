package com.aghadge.healthapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.aghadge.healthapp.database.AppointmentData
import com.aghadge.healthapp.database.Database
import com.aghadge.healthapp.database.NewAppointmentData
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class ScheduleAppointment : AppCompatActivity() {

    private lateinit var mPickDateButton: Button
    private lateinit var mPickTimeButton: Button
    private lateinit var mShowSelectedDateText: TextView
    private lateinit var mPatientName: EditText
    private lateinit var mDescription: EditText
    private lateinit var mScheduleButton: Button


    private lateinit var mAuth : FirebaseAuth
    private  val mDatabase = Database()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_appointment)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mAuth = FirebaseAuth.getInstance()
        mPickDateButton = findViewById(R.id.schedule_pick_date_button);
        mPickTimeButton = findViewById(R.id.schedule_pick_time_button);
        mShowSelectedDateText = findViewById(R.id.schedule_apt_show_selected_date);
        mPatientName = findViewById(R.id.schedule_patientName)
        mDescription = findViewById(R.id.schedule_gender)
        mScheduleButton = findViewById(R.id.mScheduleApt)


        // date picker
        val picker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Appointment Time")
                .build();

        mPickDateButton.setOnClickListener{
            picker.show(supportFragmentManager, "MATERIAL_DATE_PICKER")
        };

        // time picker
        val timepicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()

        mPickTimeButton.setOnClickListener{
            timepicker.show(supportFragmentManager , "MATERIAL_TIME_PICKER")
        };

        var date = "";
        var time = "";
        picker.addOnPositiveButtonClickListener{
            date = picker.headerText;
            mShowSelectedDateText.text = "$time $date"
        }

        timepicker.addOnPositiveButtonClickListener{

            var t = timepicker.hour;
            var a = "";
            if (t < 12 && t >= 0) {
                a = " AM";
            } else {
                t -= 12;
                if(t == 0) {
                    t = 12;
                }
                a=  " PM";
            }

            time = t.toString();
            time = time + ":" + timepicker.minute.toString() + a;
            mShowSelectedDateText.text = "$time $date"
        }


        mScheduleButton.setOnClickListener{
            scheduleAppointment();
        }
    }


    private fun scheduleAppointment(){
        val name: String = mPatientName.text.toString().trim { it <= ' ' }
        val description: String = mDescription.text.toString().trim { it <= ' ' }
        val appointmentTime: String = mShowSelectedDateText.text.toString()



        if(name.isEmpty())
        {
            mPatientName.error = "Field is required";
            mPatientName.requestFocus();
            return;
        }

        if(description.isEmpty())
        {
            mDescription.error = "Field is required";
            mDescription.requestFocus();
            return;
        }

        if(appointmentTime == "Appointment Time")
        {
            Toast.makeText(this, "Select Appointment day/time", Toast.LENGTH_SHORT).show()
            return;
        }

        val firebaseUser: FirebaseUser = mAuth.currentUser!!
        val userId = firebaseUser.uid;
        val timeStamp = Timestamp(System.currentTimeMillis())
        val sdf = SimpleDateFormat("ddMMyyyyHHmm")
        val date = sdf.format(Date(timeStamp.time))

        val appointment = AppointmentData(name , description , appointmentTime)
        val newAppointment = NewAppointmentData(name,description,appointmentTime,userId,date)


        mDatabase.setNewAppointment( this ,  appointment, userId,newAppointment,date);

        Handler().postDelayed({
            //doSomethingHere()
            finish()
        }, 2000)

    }
}