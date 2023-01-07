package com.ntduc.flappybird.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.databinding.ActivityPrepareBinding
import com.ntduc.flappybird.model.User
import com.ntduc.flappybird.repository.Repository

class PrepareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrepareBinding

    private var valueEventListener: ValueEventListener? = null
    private var rf: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrepareBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null || Repository.user == null) {
            restartApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (valueEventListener != null && rf != null) {
            rf!!.removeEventListener(valueEventListener!!)
        }
    }

    private fun init() {
        initView()
        initEvent()
    }

    private fun initEvent() {
        rf = if (Repository.isHost) {
            Firebase.database.getReference(Repository.user!!.info!!.uid)
        } else {
            Firebase.database.getReference(Repository.rival!!.info!!.uid)
        }

        valueEventListener = object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val userDB = dataSnapshot.getValue<User>()

                if (userDB != null && userDB.match.isPlaying) {
                    startActivity(Intent(this@PrepareActivity, MultiPlayerActivity::class.java))
                    finish()
                } else if (userDB != null && (!userDB.match.isConnected || userDB.match.rival == null)) {
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        rf!!.addValueEventListener(valueEventListener!!)

        binding.start.setOnClickShrinkEffectListener {
            if (Repository.isHost) {
                Repository.user!!.match.isPlaying = true
                rf!!.setValue(Repository.user)
            } else {
                Repository.rival!!.match.isPlaying = true
                rf!!.setValue(Repository.rival)
            }
        }

        binding.back.setOnClickShrinkEffectListener {
            if (Repository.isHost) {
                Repository.user!!.match.isConnected = false
                Repository.user!!.match.rival = null
                rf!!.setValue(Repository.user)
            } else if (!Repository.isHost) {
                Repository.rival!!.match.isConnected = false
                Repository.rival!!.match.rival = null
                rf!!.setValue(Repository.rival)
            }
        }
    }

    private fun initView() {
        binding.imgYou.setImageResource(Repository.user!!.bird.src)
        binding.imgRival.setImageResource(Repository.rival!!.bird.src)
        binding.coinYou.text =
            if (Repository.user!!.coin > 1000) 1000.toString() else Repository.user!!.coin.toString()
        binding.coinYou.text =
            if (Repository.rival!!.coin > 1000) 1000.toString() else Repository.rival!!.coin.toString()
    }
}