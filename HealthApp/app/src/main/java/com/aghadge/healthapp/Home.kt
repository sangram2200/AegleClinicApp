package com.aghadge.healthapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import com.aghadge.healthapp.R

import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.delay


class Home : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var drawer : DrawerLayout
    private lateinit var drawerToggle : ActionBarDrawerToggle
    private lateinit var mDrawerNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        auth = FirebaseAuth.getInstance()

        val firstFragment=FirstFragment()
        val secondFragment=SecondFragment()
        val thirdFragment=ThirdFragment()
        val staticBot = StaticBot()

        setCurrentFragment(firstFragment)

         val onNavigationItemSelectedListener=  BottomNavigationView.OnNavigationItemSelectedListener {
                 it->
            when(it.itemId){
                R.id.home-> {
                    setCurrentFragment(firstFragment)
                    return@OnNavigationItemSelectedListener true

                }
                R.id.appointments->{
                    setCurrentFragment(secondFragment)
                    return@OnNavigationItemSelectedListener true

                }
                R.id.files->{
                    setCurrentFragment(thirdFragment)
                    return@OnNavigationItemSelectedListener true

                }
                R.id.chat -> {
                    setCurrentFragment(staticBot)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Action bar Navigation drawer

        drawer = findViewById(R.id.drawer)
        drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawerNavigationView = findViewById(R.id.navigation_view)
        setupDrawerContent(mDrawerNavigationView)
    }



    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            addToBackStack(null)
            commit()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener {
            selectedItem(it);
            return@setNavigationItemSelectedListener true
        }
    }

    private fun selectedItem(item: MenuItem) {
        when(item.itemId){
            R.id.profile ->{
                setCurrentFragment(ProfileFragment())
            }
            R.id.nav_files  -> {
                setCurrentFragment(ThirdFragment())
            }
            R.id.add_appointment -> {
                startActivity(Intent(this, ScheduleAppointment::class.java))
            }
            R.id.edit_profile ->{
                startActivity(Intent(this , EditProfile::class.java))
            }
            R.id.nav_about -> {
                val url = "https://www.aegleclinic.com/";
                val uri: Uri = Uri.parse(url);
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
            R.id.nav_privacy -> {
                val builder: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(this)
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
            R.id.nav_logout -> {
                val builder: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(this)
                builder.setMessage("Are you sure that you want to Sign Out ?")
                    .setPositiveButton(
                        "Yes",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            FirebaseAuth.getInstance().signOut()
                            startActivity(Intent(this, SignUpActivity::class.java))
                            finish()
                        }).setNegativeButton("No", null)
                val alertDialog: android.app.AlertDialog? = builder.create()
                alertDialog?.show()
            }
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}