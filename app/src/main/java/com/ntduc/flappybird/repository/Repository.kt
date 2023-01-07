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
            listBird.add(Bird(name = "Tí nhanh nhảu", src = R.drawable.src_ti_nn, res1 = R.drawable.ic_ti_nn, res2 = R.drawable.ic_ti_nn_2))
            listBird.add(Bird(name = "Sửu nhẫn nại", src = R.drawable.src_suu, res1 = R.drawable.ic_suu_nn_1, res2 = R.drawable.ic_suu_nn_2))
            listBird.add(Bird(name = "Dần hùng hổ", src = R.drawable.src_dan, res1 = R.drawable.ic_dan_1, res2 = R.drawable.ic_dan_2))
            listBird.add(Bird(name = "Thìn bướng bỉnh", src = R.drawable.src_thin, res1 = R.drawable.ic_suu_nn_1, res2 = R.drawable.ic_thin_2))
            listBird.add(Bird(name = "Tị lươn lẹo", src = R.drawable.src_ti_ll, res1 = R.drawable.ic_ti_1, res2 = R.drawable.ic_ti_2))
            listBird.add(Bird(name = "Ngọ tốc độ", src = R.drawable.src_ngo, res1 = R.drawable.ic_ngo_1, res2 = R.drawable.ic_ngo_2))
            listBird.add(Bird(name = "Mùi nhút nhát", src = R.drawable.src_mui, res1 = R.drawable.ic_mui_1, res2 = R.drawable.ic_mui_2))
            listBird.add(Bird(name = "Thân khéo léo", src = R.drawable.src_than, res1 = R.drawable.ic_than_1, res2 = R.drawable.ic_than_2))
            listBird.add(Bird(name = "Dậu cần cù", src = R.drawable.src_dau, res1 = R.drawable.ic_dau_1, res2 = R.drawable.ic_dau_2))
            listBird.add(Bird(name = "Tuất kiên định", src = R.drawable.src_tuat, res1 = R.drawable.ic_tuat_1, res2 = R.drawable.ic_tuat_2))
            listBird.add(Bird(name = "Hợi hào hiệp", src = R.drawable.src_hoi, res1 = R.drawable.ic_hoi_1, res2 = R.drawable.ic_hoi_2))
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