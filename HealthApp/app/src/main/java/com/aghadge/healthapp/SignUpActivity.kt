package com.aghadge.healthapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.aghadge.healthapp.database.Database
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    private lateinit var mMobileNumber: EditText
    private lateinit var mOtp: EditText
    private lateinit var mFetchOtp: Button

    private val mDatabase = Database()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        auth=FirebaseAuth.getInstance()


        var currentUser = auth.currentUser

        if(currentUser != null) {
            startActivity(Intent(applicationContext, Home::class.java))
            finish()
        }

        mOtp = findViewById(R.id.otp);
        mMobileNumber = findViewById(R.id.mobile_number);

        mFetchOtp = findViewById(R.id.fetchOtp);


        // set otp edittext off intially
        mOtp.isEnabled = false

        mFetchOtp.setOnClickListener {
            if(!mMobileNumber.isEnabled)
            {
                var otp= mOtp.text.toString().trim()
                if(otp.isNotEmpty()){
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        storedVerificationId.toString(), otp)
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()
                }
//                closeKeyboard()
            }
            else
                login();
        }





        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {


                // set otp edittext on when code sent
                mOtp.isEnabled = true

                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

    }

    private fun buttonClickChanges(){
        // disable mobile number entry on code send;
        mMobileNumber.isEnabled = false
        // change otp button to verify
        val s = "Verify"
        mFetchOtp.text = s
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkLoginFirstTime()
// ...
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }

    private fun login() {
        // disable mobile number entry on code send;
        mMobileNumber.isEnabled = false
        // change otp button to verify
        val s = "Verify"
        mFetchOtp.text = s

        var number= mMobileNumber.text.toString().trim()

        if(!number.isEmpty()){
            number="+91"+number
            sendVerificationcode(number)
        }else{
            Toast.makeText(this,"Enter mobile number",Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationcode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun checkLoginFirstTime(){

        val firebaseUser: FirebaseUser = auth.currentUser!!
        val userId = firebaseUser.uid;
        mDatabase.firstLogin(this , userId)
        Handler().postDelayed({
            finish()
        }, 2000)
    }

    fun closeKeyboard(view: View) {
        val hideMe = getSystemService( Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideMe.hideSoftInputFromWindow(view.windowToken, 0)

//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

}