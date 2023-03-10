package com.ntduc.flappybird

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntduc.flappybird.activity.SinglePlayerActivity
import com.ntduc.sharedpreferenceutils.get
import com.ntduc.sharedpreferenceutils.put

class App : Application(), Application.ActivityLifecycleCallbacks {
    private lateinit var mediaPlayerMusic: MediaPlayer
    private lateinit var mediaPlayerEffect: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        instance = this
        firebaseDatabase = Firebase.database

        initData()

        registerActivityLifecycleCallbacks(this)
    }

    private fun initData() {
        mediaPlayerMusic = MediaPlayer.create(this, R.raw.background)
        mediaPlayerMusic.isLooping = true
        mediaPlayerMusic.start()

        mediaPlayerEffect = MediaPlayer.create(this, R.raw.wing)
    }

    fun startEffect() {
        mediaPlayerEffect.reset()
        mediaPlayerEffect = MediaPlayer.create(this, R.raw.wing)
        mediaPlayerEffect.start()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        if (activity is SinglePlayerActivity) return
        mediaPlayerMusic.start()
    }

    override fun onActivityPaused(activity: Activity) {
        if (mediaPlayerMusic.isPlaying) {
            mediaPlayerMusic.pause()
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        const val VOLUME_MASTER = "VOLUME_MASTER"
        const val VOLUME_MUSIC = "VOLUME_MUSIC"
        const val VOLUME_EFFECT = "VOLUME_EFFECT"

        private var instance: App? = null
        private var firebaseDatabase: FirebaseDatabase? = null

        fun getInstance(): App {
            return instance!!
        }

        fun getDatabase(): FirebaseDatabase {
            return firebaseDatabase!!
        }
    }
}