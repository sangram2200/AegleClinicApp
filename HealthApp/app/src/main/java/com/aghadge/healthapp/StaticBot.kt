package com.aghadge.healthapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aghadge.healthapp.R
import com.aghadge.healthapp.database.Message
import com.aghadge.healthapp.utils.BotResponse
import com.aghadge.healthapp.utils.Constants.OPEN_GOOGLE
import com.aghadge.healthapp.utils.Constants.OPEN_HOME
import com.aghadge.healthapp.utils.Constants.OPEN_SCHEDULE_APPOINTMENT
import com.aghadge.healthapp.utils.Constants.OPEN_SEARCH
import com.aghadge.healthapp.utils.Constants.RECEIVE_ID
import com.aghadge.healthapp.utils.Constants.SEND_ID
import com.aghadge.healthapp.utils.Time
import kotlinx.android.synthetic.main.activity_static_bot.*
import kotlinx.android.synthetic.main.fragment_health_data.*
import kotlinx.coroutines.*

class StaticBot : Fragment() {
    private val TAG = "MainActivity"

    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("Peter", "Rajesh", "Alex", "Igor")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View =  inflater.inflate(com.aghadge.healthapp.R.layout.activity_static_bot,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView()

        clickEvents()

        val random = (0..3).random()
        customBotMessage("Hello! Today you're speaking with ${botList[random]}, how may I help?")
        val faq = resources.getStringArray(R.array.FAQ);
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item, faq);
        et_message.movementMethod = ScrollingMovementMethod()
        et_message.setAdapter(arrayAdapter)
    }


    private fun clickEvents() {

        //Send a message
        btn_send.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(activity)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()


        if (message.isNotEmpty()) {
            //Adds it to our local list
//            messagesList.add(Message(message, SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(500)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)

                //Adds it to our local list
//                messagesList.add(Message(response, RECEIVE_ID, timeStamp))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    OPEN_GOOGLE -> {
                        delay(1000)
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.aegleclinic.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        delay(1000)
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }
                    OPEN_SCHEDULE_APPOINTMENT -> {
                        delay(1000)
                        startActivity(Intent(activity, ScheduleAppointment::class.java))
                    }
                    OPEN_HOME -> {
                        delay(2000)
                        startActivity(Intent(activity, Home::class.java))
                    }

                }
            }
        }
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
//                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}