package com.example.strangersapp.activites

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import android.view.View
import com.example.strangersapp.R

class WelcomeActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null) {
            goToNextActivity()
        }
        findViewById<View>(R.id.getStarted).setOnClickListener { goToNextActivity() }
    }

    fun goToNextActivity() {
        startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        finish()
    }
}