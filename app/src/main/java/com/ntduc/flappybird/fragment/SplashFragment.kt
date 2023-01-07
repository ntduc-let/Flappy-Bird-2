package com.ntduc.flappybird.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ntduc.flappybird.R

class SplashFragment : Fragment() {
    private var index = 0

    fun newInstance(index: Int): SplashFragment {
        val args = Bundle()
        args.putInt(INDEX, index)
        val fragment = SplashFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            index = it.getInt(INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (index == 0) {
            inflater.inflate(R.layout.fragment_splash_0, container, false)
        } else if (index == 1) {
            inflater.inflate(R.layout.fragment_splash_1, container, false)

        } else {
            inflater.inflate(R.layout.fragment_splash_2, container, false)
        }
    }

    companion object {
        const val INDEX = "INDEX"
    }
}