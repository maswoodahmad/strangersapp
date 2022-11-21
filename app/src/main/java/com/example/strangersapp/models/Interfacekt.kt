package com.example.strangersapp.models

import android.webkit.JavascriptInterface
import com.example.strangersapp.activites.CallActivity

class InterfaceJava(var callActivity: CallActivity) {
    @JavascriptInterface
    fun onPeerConnected() {
        callActivity.onPeerConnected()
    }
}