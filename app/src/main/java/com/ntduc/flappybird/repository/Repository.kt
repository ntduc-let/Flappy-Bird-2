package com.ntduc.flappybird.repository

import com.ntduc.flappybird.R
import com.ntduc.flappybird.model.Bird
import com.ntduc.flappybird.model.Rival
import com.ntduc.flappybird.model.User

object Repository {
    private val listBird = arrayListOf<Bird>()
    var isHost = false
    var user: User? = null
    var rival: User? = null

    fun getListBird(): ArrayList<Bird> {
        if (listBird.isEmpty()){
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
            listBird.add(Bird(name = "Hổ", src = R.drawable.tiger, res1 = 0, res2 = 0))
        }

        return listBird
    }

    const val STATE_GAME_PREPARE = 0
    const val STATE_GAME_NOT_STARTED = 1
    const val STATE_GAME_PLAYING = 2
    const val STATE_GAME_PAUSED = 3
    const val STATE_GAME_OVER = 4

    const val LEVEL_EASY = 0
    const val LEVEL_MEDIUM = 1
    const val LEVEL_HARD = 2
    const val LEVEL_VERY_HARD = 3

    const val GAP_EASY = 600
    const val GAP_MEDIUM = 500
    const val GAP_HARD = 400

    const val DISTANCE_EASY = 300
    const val DISTANCE_MEDIUM = 100
    const val DISTANCE_HARD = 0
}