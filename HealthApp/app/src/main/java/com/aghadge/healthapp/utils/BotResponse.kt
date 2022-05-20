package com.aghadge.healthapp.utils

import com.aghadge.healthapp.utils.Constants.OPEN_GOOGLE
import com.aghadge.healthapp.utils.Constants.OPEN_HOME
import com.aghadge.healthapp.utils.Constants.OPEN_SCHEDULE_APPOINTMENT
import com.aghadge.healthapp.utils.Constants.OPEN_SEARCH
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message:String):String{
        val random = (0..2).random()
        val message = _message.lowercase()

        return when{

            //Hello
            message.contains("hello") || message.contains("hey") || message.contains("hi") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Hey"
                    2 -> "Hello, How can I help you?"
                    else -> "error" }
            }

            //How are you?
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I'm doing fine, thanks!"
                    1 -> "Great!! Here to make your day awesome..."
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
            }

            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") || message.contains("website")-> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                OPEN_SEARCH
            }

            //schedule an appointment
            message.contains("schedule an appointment") || message.contains("book") || message.contains("schedule")->{
                OPEN_SCHEDULE_APPOINTMENT
            }

            //Bye
            message.contains("bye") ->{
                OPEN_HOME
            }



            else -> {
                when (random) {
                    0 -> "Sorry I don't understand, Try asking me something like 'Schedule an Appointment'"
                    1 -> "Sorry I don't understand, choose a message from FAQ"
                    2 -> "Try asking me something like 'search fever symptoms' "
                    else -> "error"
                }
            }
        }

    }
}