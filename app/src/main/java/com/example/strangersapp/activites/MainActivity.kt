package com.example.strangersapp.activites

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
//import com.kaopiz.kprogresshud.KProgressHUD
import android.os.Bundle
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import android.content.Intent
//import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.example.strangersapp.databinding.ActivityMainBinding

import com.example.strangersapp.models.User


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var auth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
   // var coins: Long = 0
    var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val requestCode = 1
    var user: User? = null

   //var progress: KProgressHUD? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
//        MobileAds.initialize(this) { }
//        progress = KProgressHUD.create(this)
//        progress?.setDimAmount(0.5f)
//        progress?.show()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val currentUser = auth!!.currentUser
        database!!.reference.child("profiles")
            .child(currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   // progress?.dismiss()
                    user = snapshot.getValue(User::class.java)
//
                    Glide.with(this@MainActivity)
                        .load(user!!.profile)
                        .into(binding!!.profilePicture)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        binding!!.findButton.setOnClickListener {
            if (isPermissionsGranted) {
//                if (coins > 5) {
//                    coins = coins - 5
//                    database!!.reference.child("profiles")
//                        .child(currentUser.uid)
//                        .child("coins")
//                        .setValue(coins)
                    val intent = Intent(this@MainActivity, ConnectingActivity::class.java)
                    intent.putExtra("profile", user!!.profile)
                    startActivity(intent)
                    //startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
//                } else {
//                    Toast.makeText(this@MainActivity, "Insufficient Coins", Toast.LENGTH_SHORT)
//                        .show()
//                }
            } else {
                askPermissions()
            }
        }
//        binding!!.rewardBtn.setOnClickListener {
//            startActivity(
//                Intent(
//                    this@MainActivity,
//                    RewardActivity::class.java
//                )
//            )
//        }
    }

    fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private val isPermissionsGranted: Boolean
        private get() {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) return false
            }
            return true
        }
}