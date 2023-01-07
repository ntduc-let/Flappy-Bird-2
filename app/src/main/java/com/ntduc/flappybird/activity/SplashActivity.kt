package com.ntduc.flappybird.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.App
import com.ntduc.flappybird.databinding.ActivitySplashBinding
import com.ntduc.flappybird.dialog.DialogLoginLoading
import com.ntduc.flappybird.model.Info
import com.ntduc.flappybird.model.User
import com.ntduc.flappybird.repository.Repository

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.started.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
            val user = auth.currentUser
            if (user != null) {
                getInfoUser(user)
            } else {
                // Choose authentication providers
                val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                signInLauncher.launch(signInIntent)
            }
        }
    }

    private fun initView() {

    }

    private fun initData() {
        auth = FirebaseAuth.getInstance()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = auth.currentUser
            if (user != null) {
                getInfoUser(user)
            }
        }
    }

    private fun getInfoUser(user: FirebaseUser) {
        val dialog = DialogLoginLoading(this)
        dialog.show()

        val rf = Firebase.database.getReference(user.uid)
        rf.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val userDB = dataSnapshot.getValue<User>()

                if (userDB != null) {
                    Repository.user = userDB
                } else {
                    val info = Info(
                        uid = user.uid,
                        name = user.displayName ?: "",
                        photoUrl = user.photoUrl.toString()
                            .replace("s96-c", "s400-c", true),
                        email = user.email ?: ""
                    )
                    val bird = Repository.getListBird()[0]
                    Repository.user = User(info = info, bird = bird)
                    rf.setValue(Repository.user)
                }

                dialog.cancel()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.cancel()
            }
        })
    }
}