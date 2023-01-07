package com.ntduc.flappybird.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ntduc.flappybird.fragment.SplashFragment

class FragmentSplashAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        return SplashFragment().newInstance(position)
    }

    override fun getItemCount(): Int {
        return 3
    }
}