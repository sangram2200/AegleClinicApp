package com.aghadge.healthapp.database

data class AppointmentData(val patientName: String , val descriptiveNote: String , val appointmentTime : String);
data class NewAppointmentData(val name: String , val note: String , val time : String, val uid: String,val apid: String);