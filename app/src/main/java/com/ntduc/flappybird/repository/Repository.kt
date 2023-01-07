package com.ntduc.flappybird.repository

import com.ntduc.flappybird.R
import com.ntduc.flappybird.model.Bird
import com.ntduc.flappybird.model.Rival
import com.ntduc.flappybird.model.User

object Repository {
    private val listBird = arrayListOf<Bird>()
    var user: User? = null
    var rival: Rival? = null

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
}