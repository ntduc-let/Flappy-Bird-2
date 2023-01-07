package com.ntduc.flappybird.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.R
import com.ntduc.flappybird.databinding.ActivityPrepareBinding
import com.ntduc.flappybird.repository.Repository

class PrepareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrepareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrepareBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initView()
    }

    private fun initView() {
        binding.imgYou.setImageResource(Repository.user!!.bird.src)
        binding.imgRival.setImageResource(Repository.user!!.bird.src)
    }
}