package com.aghadge.healthapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.aghadge.healthapp.database.AppointmentData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private lateinit var appointmentList: ArrayList<AppointmentData>
    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userId: String
    lateinit var bitmap: Bitmap
    private var qrgEncoder: QRGEncoder? = null
    private lateinit var qrview : CardView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        qrview = requireView().findViewById(R.id.generateQrBtn) as CardView

        edit_profile_new.setOnClickListener{
            startActivity(Intent(activity , EditProfile::class.java))
        }

        about_new.setOnClickListener{
            val url = "https://www.aegleclinic.com/";
            val uri: Uri = Uri.parse(url);
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        health_data.setOnClickListener{
            val healthDataFragment = HealthDataFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.flFragment,healthDataFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        privacy_new.setOnClickListener{
            val builder: android.app.AlertDialog.Builder =
                android.app.AlertDialog.Builder(activity)
            builder.setMessage("Privacy Note : Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                    " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                    " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
                    " ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit" +
                    " in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur" +
                    " sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt" +
                    " mollit anim id est laborum." + "\nLorem ipsum dolor sit amet," +
                    " consectetur adipiscing elit," +
                    " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                    " Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi" +
                    " ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit" +
                    " in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur" +
                    " sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt" +
                    " mollit anim id est laborum.")
                .setPositiveButton(
                    "Ok",
                    null)
            val alertDialog: android.app.AlertDialog? = builder.create()
            alertDialog?.show()
        }

        datauser()
    }
    private fun datauser(){
        db = FirebaseFirestore.getInstance()
        appointmentList = arrayListOf<AppointmentData>()
        firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser!!.uid
        val firebaseUser: FirebaseUser = firebaseAuth.currentUser!!
        var temp:String =""

        val docRef = db.collection("Users").document(firebaseUser.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("existsUser", "DocumentSnapshot data: ${document.data}")
                    temp = document.data.toString()
                    nametv.setText(document.getString("patientName"))
                    emailtv.setText(document.getString("birthDate"))

                    db.collection("Users").document(userId).collection("health_data")
                        .addSnapshotListener(
                            EventListener { value, error ->
                                if(error != null) {
                                    Log.e("Firestore error", error.message.toString())
                                    return@EventListener
                                }
                                if (value != null) {
                                    var num=0
                                    for (dc: DocumentChange in value.documentChanges) {
                                        if(num==1) break
                                        if (dc.type == DocumentChange.Type.ADDED) {
                                            Log.d("existsHealth", "DocumentSnapshot data: ${dc.document.data}")
                                            temp += dc.document.data.toString()
                                            num += 1
                                        }
                                    }
                                    Log.d("existsTemp", "DocumentSnapshot data: $temp")
                                    qrgEncoder =
                                        QRGEncoder(temp, null, QRGContents.Type.TEXT, 300)
                                    try {
                                        // getting our qrcode in the form of bitmap.
                                        bitmap = qrgEncoder!!.encodeAsBitmap()
                                        // the bitmap is set inside our image
                                        // view using .setimagebitmap method.
                                        displayPopupQR(bitmap)

                                    } catch (e: WriterException) {
                                        // this method is called for
                                        // exception handling.
                                        Log.e("Tag", e.toString())
                                    }
                                }
                                if(value == null){
                                    Log.d("existsTemp", "DocumentSnapshot data: $temp")
                                    qrgEncoder =
                                        QRGEncoder(temp, null, QRGContents.Type.TEXT, 300)
                                    try {
                                        // getting our qrcode in the form of bitmap.
                                        bitmap = qrgEncoder!!.encodeAsBitmap()
                                        // the bitmap is set inside our image
                                        // view using .setimagebitmap method.
                                        displayPopupQR(bitmap)

                                    } catch (e: WriterException) {
                                        // this method is called for
                                        // exception handling.
                                        Log.e("Tag", e.toString())
                                    }
                                }
                            })

                } else {
                    Log.d("noExists", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("errorDb", "get failed with ", exception)
            }
    }

    private fun displayPopupQR(bitmap: Bitmap){
        qrview.setOnClickListener {
            val image = ImageView(activity)
            image.setImageBitmap(Bitmap.createScaledBitmap(bitmap,620,620,false))

            val builder: AlertDialog.Builder =
                AlertDialog.Builder(activity).setMessage("User QR Code")
                    .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            dialog.dismiss()
                        }
                    }).setView(image)
            builder.create().show()

        }
    }
}


