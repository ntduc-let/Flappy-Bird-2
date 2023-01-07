package com.ntduc.flappybird.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.databinding.ActivitySinglePlayerBinding

class SinglePlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySinglePlayerBinding
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinglePlayerBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        initData()
//        initView()
//        initEvent()
    }

    private fun initData() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    companion object {
        private const val STATE_GAME_NOT_STARTED = 0
        private const val STATE_GAME_PLAYING = 1
        private const val STATE_GAME_PAUSED = 2
        private const val STATE_GAME_OVER = 3

        const val LEVEL_GAME = "LEVEL_GAME"
        const val TYPE_BIRD = "TYPE_BIRD"

        const val LEVEL_EASY = 0
        const val LEVEL_MEDIUM = 1
        const val LEVEL_HARD = 2
        const val LEVEL_VERY_HARD = 3

        private const val GAP_EASY = 600
        private const val GAP_MEDIUM = 500
        private const val GAP_HARD = 400

        private const val DISTANCE_EASY = 300
        private const val DISTANCE_MEDIUM = 100
        private const val DISTANCE_HARD = 0

        private const val BEST_SCORE_EASY = "BEST_SCORE_EASY"
        private const val BEST_SCORE_MEDIUM = "BEST_SCORE_MEDIUM"
        private const val BEST_SCORE_HARD = "BEST_SCORE_HARD"
        private const val BEST_SCORE_VERY_HARD = "BEST_SCORE_VERY_HARD"
    }
}