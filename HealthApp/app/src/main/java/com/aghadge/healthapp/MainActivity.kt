package com.aghadge.healthapp

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var auth: FirebaseAuth
    private lateinit var mABtnSignIn: Button
    private lateinit var mABtnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        auth= FirebaseAuth.getInstance()


        var currentUser = auth.currentUser

        if(currentUser != null) {
            startActivity(Intent(applicationContext, Home::class.java))
            finish()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mABtnSignIn = findViewById(R.id.mABtnSignIn)
        mABtnSignUp = findViewById(R.id.mABtnSignUp)

        mABtnSignIn.setOnClickListener(this)
        mABtnSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            mABtnSignIn -> startActivity(Intent(this, SignUpActivity::class.java))
            mABtnSignUp -> startActivity(Intent(this, SignUpActivity::class.java))

        }

        finish()
    }
}