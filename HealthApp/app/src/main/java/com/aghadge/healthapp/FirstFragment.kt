package com.aghadge.healthapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

class FirstFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_first,container,false)
        val landingPage = view.findViewById<LinearLayout>(R.id.landing_page)
        landingPage.setOnClickListener {
            val url: String = "https://www.aegleclinic.com/";
            val uri: Uri = Uri.parse(url);
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        return view
    }
}