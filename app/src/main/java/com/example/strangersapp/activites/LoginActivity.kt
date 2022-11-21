package com.example.strangersapp.activites

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import android.util.Log
import android.view.View
import com.example.strangersapp.models.User
import com.example.strangersapp.R

class  LoginActivity : AppCompatActivity() {
    var mGoogleSignInClient: GoogleSignInClient? = null
    var RC_SIGN_IN = 11
    var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.currentUser != null) {
            goToNextActivity()
        }
        database = FirebaseDatabase.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        findViewById<View>(R.id.loginBtn).setOnClickListener {
            val intent = mGoogleSignInClient?.getSignInIntent()
            startActivityForResult(intent, RC_SIGN_IN)
            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    fun goToNextActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.result
            authWithGoogle(account.idToken)
        }
    }

    fun authWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    val firebaseUser = User(
                        user!!.uid, user.displayName, user.photoUrl.toString(), "Unknown", 500
                    )
                    database!!.reference
                        .child("profiles")
                        .child(user.uid)
                        .setValue(firebaseUser).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finishAffinity()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception!!.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    //Log.e("profile", user.getPhotoUrl().toString());
                } else {
                    Log.e("err", task.exception!!.localizedMessage)
                }
            }
    }
}