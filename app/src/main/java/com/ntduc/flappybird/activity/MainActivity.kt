package com.ntduc.flappybird.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.App
import com.ntduc.flappybird.adapter.ChooseBirdsAdapter
import com.ntduc.flappybird.databinding.ActivityMainBinding
import com.ntduc.flappybird.repository.Repository
import com.ntduc.viewpager2utils.BackgroundToForegroundTransformer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            restartApp()
        }
    }

    private fun init() {
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.start.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
        }

        binding.compete.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
        }

        binding.map.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
        }

        binding.setting.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
        }
    }

    private fun initView() {
        binding.vpChooseBird.adapter = ChooseBirdsAdapter(this, Repository.getListBird())
        binding.vpChooseBird.setPageTransformer(BackgroundToForegroundTransformer())

        val list = Repository.getListBird()
        list.indices.forEach {
            if (list[it] == Repository.user!!.bird) {
                binding.vpChooseBird.currentItem = it
            }
        }

        val recyclerView = binding.vpChooseBird.getChildAt(0) as RecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val itemCount = binding.vpChooseBird.adapter?.itemCount ?: 0

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
                val lastItemVisible = layoutManager.findLastVisibleItemPosition()
                if (firstItemVisible == (itemCount - 1) && dx > 0) {
                    recyclerView.scrollToPosition(1)
                } else if (lastItemVisible == 0 && dx < 0) {
                    recyclerView.scrollToPosition(itemCount - 2)
                }
            }
        })
    }
}