package com.example.strangersapp.activites

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import android.webkit.WebChromeClient
import android.webkit.PermissionRequest
import android.os.Build
import android.os.Handler
import android.view.View
import android.webkit.WebViewClient
import android.webkit.WebView
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseError
import android.widget.Toast
import com.example.strangersapp.R
//import com.example.strangersapp.databinding.ActivityCallBinding
import com.example.strangersapp.models.User
import com.example.strangersapp.databinding.ActivityCallBinding
import com.example.strangersapp.models.InterfaceJava
import java.util.*

class CallActivity : AppCompatActivity() {
    var binding: ActivityCallBinding? = null
    var uniqueId = ""
    var auth: FirebaseAuth? = null
    var username: String? = ""
    var friendsUsername: String? = ""
    var isPeerConnected = false
    var firebaseRef: DatabaseReference? = null
    var isAudio = true
    var isVideo = true
    var createdBy: String? = null
    var pageExit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        auth = FirebaseAuth.getInstance()
        firebaseRef = FirebaseDatabase.getInstance().reference.child("users")
        username = intent.getStringExtra("username")
        val incoming = intent.getStringExtra("incoming")
        createdBy = intent.getStringExtra("createdBy")

//        friendsUsername = "";
//
//        if(incoming.equalsIgnoreCase(friendsUsername))
//            friendsUsername = incoming;
        friendsUsername = incoming
        setupWebView()
        binding!!.micBtn.setOnClickListener {
            isAudio = !isAudio
            callJavaScriptFunction("javascript:toggleAudio(\"$isAudio\")")
            if (isAudio) {
                binding!!.micBtn.setImageResource(R.drawable.btn_unmute_normal)
            } else {
                binding!!.micBtn.setImageResource(R.drawable.btn_mute_normal)
            }
        }
        binding!!.videoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavaScriptFunction("javascript:toggleVideo(\"$isVideo\")")
            if (isVideo) {
                binding!!.videoBtn.setImageResource(R.drawable.btn_video_normal)
            } else {
                binding!!.videoBtn.setImageResource(R.drawable.btn_video_muted)
            }
        }
        binding!!.endCall.setOnClickListener { finish() }
    }

    fun setupWebView() {
        binding!!.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        }
        binding!!.webView.settings.javaScriptEnabled = true
        binding!!.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding!!.webView.addJavascriptInterface(InterfaceJava(this), "Android")
        loadVideoCall()
    }

    fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        binding!!.webView.loadUrl(filePath)
        binding!!.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                initializePeer()
            }
        }
    }

    fun initializePeer() {
        uniqueId = getUniqueId()
        callJavaScriptFunction("javascript:init(\"$uniqueId\")")
        if (createdBy.equals(username, ignoreCase = true)) {
            if (pageExit) return
            firebaseRef!!.child(username!!).child("connId").setValue(uniqueId)
            firebaseRef!!.child(username!!).child("isAvailable").setValue(true)
            binding!!.loadingGroup.visibility = View.GONE
            binding!!.controls.visibility = View.VISIBLE
            FirebaseDatabase.getInstance().reference
                .child("profiles")
                .child(friendsUsername!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(
                            User::class.java
                        )
                        Glide.with(this@CallActivity).load(user!!.profile)
                            .into(binding!!.profile)
                        binding!!.name.text = user.name
                        binding!!.city.text = user.city
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            Handler().postDelayed({
                friendsUsername = createdBy
                FirebaseDatabase.getInstance().reference
                    .child("profiles")
                    .child(friendsUsername!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(
                                User::class.java
                            )
                            Glide.with(this@CallActivity).load(user!!.profile)
                                .into(binding!!.profile)
                            binding!!.name.text = user.name
                            binding!!.city.text = user.city
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(friendsUsername!!)
                    .child("connId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value != null) {
                                sendCallRequest()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }, 3000)
        }
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }

    fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(
                this,
                "You are not connected. Please check your internet.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        listenConnId()
    }

    fun listenConnId() {
        firebaseRef!!.child(friendsUsername!!).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) return
                    binding!!.loadingGroup.visibility = View.GONE
                    binding!!.controls.visibility = View.VISIBLE
                    val connId = snapshot.getValue(String::class.java)
                    callJavaScriptFunction("javascript:startCall(\"$connId\")")
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun callJavaScriptFunction(function: String?) {
        binding!!.webView.post { binding!!.webView.evaluateJavascript(function!!, null) }
    }

    @JvmName("getUniqueId1")
    fun getUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        pageExit = true
        firebaseRef!!.child(createdBy!!).setValue(null)
        finish()
    }
}