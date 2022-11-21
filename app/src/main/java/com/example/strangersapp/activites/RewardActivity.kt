//package com.example.strangersapp.activites
//
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//import android.os.Bundle
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
////import com.google.android.gms.ads.rewarded.RewardedAd
//import android.app.Activity
//import com.example.strangersapp.databinding.ActivityRewardBinding
//
////import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
////import com.google.android.gms.ads.LoadAdError
////import com.appsians.strangers.databinding.ActivityRewardBinding
////import com.google.android.gms.ads.AdRequest
//
//class RewardActivity : AppCompatActivity() {
//    var binding: ActivityRewardBinding? = null
//    private var mRewardedAd: RewardedAd? = null
//    var database: FirebaseDatabase? = null
//    var currentUid: String? = null
//    var coins = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRewardBinding.inflate(layoutInflater)
//        setContentView(binding!!.root)
//        database = FirebaseDatabase.getInstance()
//        currentUid = FirebaseAuth.getInstance().uid
//        loadAd()
//        database!!.reference.child("profiles")
//            .child(currentUid!!)
//            .child("coins")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    coins = snapshot.getValue(Int::class.java)!!
//                    binding!!.coins.text = coins.toString()
//                }
//
//                override fun onCancelled(error: DatabaseError) {}
//            })
//        binding!!.video1.setOnClickListener {
//            if (mRewardedAd != null) {
//                val activityContext: Activity = this@RewardActivity
//                mRewardedAd!!.show(activityContext) {
//                    loadAd()
//                    coins = coins + 200
//                    database!!.reference.child("profiles")
//                        .child(currentUid!!)
//                        .child("coins")
//                        .setValue(coins)
//                    binding!!.video1Icon.setImageResource(R.drawable.check)
//                }
//            } else {
//            }
//        }
//    }
//
//    fun loadAd() {
//        val adRequest = AdRequest.Builder().build()
//        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
//            adRequest, object : RewardedAdLoadCallback() {
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    // Handle the error.
//                    mRewardedAd = null
//                }
//
//                override fun onAdLoaded(rewardedAd: RewardedAd) {
//                    mRewardedAd = rewardedAd
//                }
//            })
//    }
//}