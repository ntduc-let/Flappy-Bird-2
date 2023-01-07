package com.ntduc.flappybird.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntduc.activityutils.setStatusBarColor
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.App
import com.ntduc.flappybird.R
import com.ntduc.flappybird.adapter.ChooseBirdsAdapter
import com.ntduc.flappybird.databinding.ActivityMainBinding
import com.ntduc.flappybird.repository.Repository
import com.ntduc.viewpager2utils.BackgroundToForegroundTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        if (auth.currentUser == null || Repository.user == null) {
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
            Repository.user!!.bird = Repository.getListBird()[binding.vpChooseBird.currentItem - 1]
            startActivity(Intent(this, SinglePlayerActivity::class.java))
        }

        binding.compete.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
            Repository.user!!.bird = Repository.getListBird()[binding.vpChooseBird.currentItem - 1]
            Repository.user!!.match.isCreated = true

            GlobalScope.launch(Dispatchers.IO) {
                Firebase.database.getReference(Repository.user!!.info!!.uid)
                    .setValue(Repository.user)
            }

            startActivity(Intent(this, QRActivity::class.java))
        }

        binding.map.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
            Repository.user!!.bird = Repository.getListBird()[binding.vpChooseBird.currentItem - 1]
        }

        binding.setting.setOnClickShrinkEffectListener {
            App.getInstance().startEffect()
        }
    }

    private fun initView() {
        setStatusBarColor(R.color.main)
        binding.vpChooseBird.adapter = ChooseBirdsAdapter(this, Repository.getListBird())
        binding.vpChooseBird.setPageTransformer(BackgroundToForegroundTransformer())
        binding.vpChooseBird.clipToPadding = false
        binding.vpChooseBird.clipChildren = false
        binding.vpChooseBird.offscreenPageLimit = 3
        binding.vpChooseBird.currentItem = 1

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