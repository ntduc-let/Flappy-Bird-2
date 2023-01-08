package com.ntduc.flappybird.activity

import android.content.Intent
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ntduc.activityutils.enterFullScreenMode
import com.ntduc.contextutils.displayHeight
import com.ntduc.contextutils.displayWidth
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.App
import com.ntduc.flappybird.R
import com.ntduc.flappybird.databinding.ActivityMultiPlayerBinding
import com.ntduc.flappybird.databinding.ActivitySinglePlayerBinding
import com.ntduc.flappybird.model.*
import com.ntduc.flappybird.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MultiPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnTouchListener {
    private lateinit var binding: ActivityMultiPlayerBinding
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var auth: FirebaseAuth

    private var valueEventListener: ValueEventListener? = null
    private var rf: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultiPlayerBinding.inflate(inflater)
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

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (valueEventListener != null && rf != null) {
            rf!!.removeEventListener(valueEventListener!!)
        }
    }

    override fun onBackPressed() {}

    private fun init() {
        if (Repository.user == null) return

        initData()
        initView()
        initEvent()
    }

    private fun initEvent() {
        rf = if (Repository.isHost) {
            Firebase.database.getReference(Repository.user!!.info!!.uid)
        } else {
            Firebase.database.getReference(Repository.rival!!.info!!.uid)
        }

//        valueEventListener = object :
//            ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val userDB = dataSnapshot.getValue<User>()
//
//                if (userDB != null && userDB.match.isPlaying) {
//                    startActivity(Intent(this@PrepareActivity, MultiPlayerActivity::class.java))
//                    finish()
//                } else if (userDB != null && (!userDB.match.isConnected || userDB.match.rival == null)) {
//                    finish()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        }
//        rf!!.addValueEventListener(valueEventListener!!)


        binding.surfaceView.setOnTouchListener(this)
//        binding.scoreboard.exit.setOnClickListener {
//            finish()
//        }
        binding.scoreboard.playAgain.setOnClickListener {
            resetData()
        }
        binding.paused.resume.setOnClickListener {

        }
        binding.paused.exit.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        enterFullScreenMode()

        setupSurfaceHolder()
        setupVideoBackground()
    }

    private fun setupVideoBackground() {
        binding.videoBackground.setOnPreparedListener {
            val videoRatio = it.videoWidth / it.videoHeight.toFloat()
            val screenRatio =
                binding.videoBackground.width / binding.videoBackground.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                binding.videoBackground.scaleX = scaleX
            } else {
                binding.videoBackground.scaleY = 1f / scaleX
            }

            it.isLooping = true
        }

        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.background1)
        binding.videoBackground.setVideoURI(uri)
        binding.videoBackground.start()
    }

    private fun setupSurfaceHolder() {
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceHolder.addCallback(this)
    }

    private fun initData() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        createDataGame()
    }

    private fun createDataGame() {
        mTopTube = BitmapFactory.decodeResource(resources, R.drawable.toptube)
        mBottomTube = BitmapFactory.decodeResource(resources, R.drawable.bottomtube)
        mCoin = BitmapFactory.decodeResource(resources, R.drawable.coin)
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val heightNavigationBar = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0

        mDisplayWidth = displayWidth
        mDisplayHeight = displayHeight + heightNavigationBar
        mRect = Rect(0, 0, mDisplayWidth, mDisplayHeight)

        mBirds = listOf(
            BitmapFactory.decodeResource(resources, R.drawable.birda),
            BitmapFactory.decodeResource(resources, R.drawable.birdb)
        )

        resetData()
    }

    private fun resetData() {
        gameState = STATE_GAME_NOT_STARTED

        score = ScoreMatch()
        score!!.score = 0
        binding.score.text = "${score!!.score}"
        score!!.scoringTube = 0
        score!!.scoringCoin = true

        bird = BirdMatch()
        bird!!.birdX = (mDisplayWidth - mBirds[0].width).toFloat() / 2
        bird!!.birdY = (mDisplayHeight - mBirds[0].height).toFloat() / 2

        tube = TubeMatch()
        tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4
        tube!!.maxTubeOffset = mDisplayHeight - tube!!.minTubeOffset - tube!!.gap

        updateDataLevel(Repository.user!!.level.level)

        tube!!.tubeX.clear()
        tube!!.topTubeY.clear()
        for (i in 0 until numberOfTubes) {
            tube!!.tubeX.add(mDisplayWidth + tube!!.distanceBetweenTubes * i)
            tube!!.topTubeY.add(tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1))

            if (Repository.user!!.level.level == LEVEL_VERY_HARD) {
                tube!!.tubeMove.add(tube!!.tubeMoveDefault)
            }
        }

        coin = CoinMatch()
        coin!!.coinX.clear()
        coin!!.coinY.clear()
        coin!!.coinShowing.clear()
        for (i in 0 until numberOfTubes) {
            coin!!.coinX.add(tube!!.tubeX[i] + tube!!.distanceBetweenTubes / 2 + random.nextInt(tube!!.distanceBetweenTubes - tube!!.distanceBetweenTubes / 2 - (2 * mCoin!!.width) + 1))
            coin!!.coinY.add(tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1))
            coin!!.coinShowing.add(true)
        }
    }

    private fun updateDataLevel(level: Int) {
        when (level) {
            LEVEL_EASY -> {
                tube!!.gap = GAP_EASY
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_EASY
            }
            LEVEL_MEDIUM -> {
                tube!!.gap = GAP_MEDIUM
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_MEDIUM
            }
            LEVEL_HARD, LEVEL_VERY_HARD -> {
                tube!!.gap = GAP_HARD
                tube!!.distanceBetweenTubes = mDisplayWidth * 3 / 4 + DISTANCE_HARD
            }
        }
        tube!!.minTubeOffset = tube!!.gap / 2
        tube!!.maxTubeOffset = mDisplayHeight - tube!!.minTubeOffset - tube!!.gap
    }

    companion object {
        private const val STATE_GAME_NOT_STARTED = 0
        private const val STATE_GAME_PLAYING = 1
        private const val STATE_GAME_PAUSED = 2
        private const val STATE_GAME_OVER = 3

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
    }

    private var gameState: Int = STATE_GAME_NOT_STARTED

    private var mRect: Rect? = null
    private var mDisplayWidth: Int = 0              //Width screen
    private var mDisplayHeight: Int = 0             //Height screen

    private var mediaHit: MediaPlayer? = null
    private var mediaPoint: MediaPlayer? = null
    private var mediaWing: MediaPlayer? = null

    private var random: Random = Random()

    private var bird: BirdMatch? = null
    private var mBirds: List<Bitmap> = listOf()     //List bitmap for bird
    private var birdFrame: Int = 0                  //Theo dõi trạng thái của bird

    private var tube: TubeMatch? = null
    private var mTopTube: Bitmap? = null
    private var mBottomTube: Bitmap? = null
    private var numberOfTubes: Int = 2

    private var coin: CoinMatch? = null
    private var mCoin: Bitmap? = null

    private var score: ScoreMatch? = null

    override fun surfaceCreated(holder: SurfaceHolder) {
        runGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val action = event?.action

        if (action == MotionEvent.ACTION_DOWN) {

            when (gameState) {
                STATE_GAME_NOT_STARTED -> {
                    startMediaWing()

                    bird!!.velocity = -30
                    gameState = STATE_GAME_PLAYING
                }
                STATE_GAME_PLAYING -> {
                    startMediaWing()

                    bird!!.velocity = -30
                }
                STATE_GAME_PAUSED -> {

                }
                STATE_GAME_OVER -> {

                }
            }
        }
        return true
    }

    private fun runGame() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                val canvas: Canvas = withContext(Dispatchers.Main) {
                    surfaceHolder.lockCanvas()
                } ?: continue

                clearBackGroundGame(canvas)

                when (gameState) {
                    STATE_GAME_NOT_STARTED -> {
                        drawBird(canvas)

                        hideScoreBoard()
                        hideScore()
                        hidePaused()
                    }
                    STATE_GAME_PLAYING -> {
                        updateTube(canvas)
                        updateCoin(canvas)

                        updateBird(canvas)

                        showScore()
                        hidePaused()

                        if (isBirdHitCoin()) {
                            updateScoreCoin()
                        }

                        if (isBirdHitTube()) {
                            startMediaHit()

                            gameState = STATE_GAME_OVER
                        } else {
                            updateScoreTube()
                        }
                    }
                    STATE_GAME_PAUSED -> {
                        drawTube(canvas)
                        drawCoin(canvas)

                        drawBird(canvas)

                        showPaused()
                    }
                    STATE_GAME_OVER -> {
                        drawTube(canvas)
                        drawCoin(canvas)

                        drawBird(canvas)

                        hideScore()
                        showScoreBoard()
                        hidePaused()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (surfaceHolder.surface.isValid) surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private suspend fun hidePaused() {
        withContext(Dispatchers.Main) {
            binding.paused.root.visibility = View.GONE
        }
    }

    private suspend fun showPaused() {
        withContext(Dispatchers.Main) {
            binding.paused.root.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScore() {
        withContext(Dispatchers.Main) {
            binding.score.visibility = View.GONE
        }
    }

    private suspend fun showScore() {
        withContext(Dispatchers.Main) {
            binding.score.visibility = View.VISIBLE
        }
    }

    private suspend fun hideScoreBoard() {
        withContext(Dispatchers.Main) {
            binding.scoreboard.root.visibility = View.GONE
        }
    }

    private suspend fun showScoreBoard() {
        withContext(Dispatchers.Main) {
//            binding.scoreboard.score.text = "${score!!.score}"
            binding.scoreboard.root.visibility = View.VISIBLE

            if (Repository.user!!.level.bestCoin < score!!.score) {
                Repository.user!!.level.bestCoin = score!!.score
            }

            withContext(Dispatchers.IO){
                Firebase.database.getReference(Repository.user!!.info!!.uid)
                    .setValue(Repository.user)
            }

//            binding.scoreboard.best.text = "${Repository.user!!.level.bestCoin}"
        }
    }

    private fun isBirdHitTube(): Boolean {
        for (i in 0 until numberOfTubes) {
            if (bird!!.birdX.toInt() + mBirds[0].width >= tube!!.tubeX[i]
                && (bird!!.birdY.toInt() <= tube!!.topTubeY[i] || bird!!.birdY.toInt() + mBirds[0].height >= tube!!.topTubeY[i] + tube!!.gap)
                && bird!!.birdX.toInt() <= tube!!.tubeX[i] + mTopTube!!.width
            ) return true
        }
        return false
    }

    private fun isBirdHitCoin(): Boolean {
        for (i in 0 until numberOfTubes) {
            if (bird!!.birdX.toInt() + mBirds[0].width >= coin!!.coinX[i]
                && (bird!!.birdY.toInt() + mBirds[0].height >= coin!!.coinY[i] && bird!!.birdY.toInt() <= coin!!.coinY[i] + mCoin!!.height)
                && bird!!.birdX.toInt() <= coin!!.coinX[i] + mCoin!!.width
            ) {
                coin!!.coinShowing[i] = false
                return true
            }
        }
        return false
    }

    private suspend fun updateScoreTube() {
        if (tube!!.tubeX[score!!.scoringTube] < (bird!!.birdX - mTopTube!!.width)) {
            score!!.score += 1
            score!!.scoringTube =
                if (score!!.scoringTube < numberOfTubes - 1) score!!.scoringTube + 1 else 0

            startMediaPoint()

            withContext(Dispatchers.Main) {
                binding.score.text = "${score!!.score}"
            }
        }
    }

    private suspend fun updateScoreCoin() {
        if (score!!.scoringCoin) {
            score!!.score += 5
            score!!.scoringCoin = false

            startMediaPoint()

            withContext(Dispatchers.Main) {
                binding.score.text = "${score!!.score}"
            }
        }
    }

    private suspend fun updateCoin(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (coin!!.coinX[i] < -mCoin!!.width) {   //Xét coin ra ngoài màn hình
                score!!.scoringCoin = true
                coin!!.coinShowing[i] = true
                coin!!.coinX[i] =
                    tube!!.tubeX[i] + tube!!.distanceBetweenTubes / 2 + random.nextInt(tube!!.distanceBetweenTubes - tube!!.distanceBetweenTubes / 2 - (2 * mCoin!!.width) + 1)
                coin!!.coinY[i] =
                    tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1)
            } else {
                coin!!.coinX[i] -= tube!!.tubeVelocity
            }
        }
        drawCoin(canvas)
    }


    private suspend fun updateTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (tube!!.tubeX[i] < -mTopTube!!.width) {   //Xét tube ra ngoài màn hình
                tube!!.tubeX[i] += numberOfTubes * tube!!.distanceBetweenTubes
                tube!!.topTubeY[i] =
                    tube!!.minTubeOffset + random.nextInt(tube!!.maxTubeOffset - tube!!.minTubeOffset + 1)

                if (Repository.user!!.level.level == LEVEL_VERY_HARD) {
                    tube!!.tubeMove[i] = tube!!.tubeMoveDefault
                }
            } else {
                tube!!.tubeX[i] -= tube!!.tubeVelocity
                if (Repository.user!!.level.level == LEVEL_VERY_HARD) {
                    tube!!.tubeMove[i] =
                        if ((tube!!.topTubeY[i] + tube!!.tubeMove[i] <= tube!!.minTubeOffset)
                            || (tube!!.topTubeY[i] + tube!!.tubeMove[i] >= tube!!.maxTubeOffset)
                        )
                            0 - tube!!.tubeMove[i]
                        else tube!!.tubeMove[i]
                    tube!!.topTubeY[i] += tube!!.tubeMove[i]
                }
            }
        }
        drawTube(canvas)
    }

    private suspend fun updateBird(canvas: Canvas) {
        if (bird!!.birdY < mDisplayHeight - mBirds[0].height || bird!!.velocity < 0) {     //Xét bird không rơi khỏi màn hình
            //Xét bird đang rơi, càng rơi càng nhanh
            bird!!.velocity += bird!!.gravity

            bird!!.birdY =
                if (bird!!.birdY + bird!!.velocity < 0) 0f else bird!!.birdY + bird!!.velocity
        } else {
            startMediaHit()

            gameState = STATE_GAME_OVER
        }
        drawBird(canvas)
    }

    private suspend fun drawCoin(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            if (coin!!.coinShowing[i]) {
                withContext(Dispatchers.Main) {
                    canvas.drawBitmap(
                        mCoin!!,
                        coin!!.coinX[i].toFloat(),
                        coin!!.coinY[i].toFloat(),
                        null
                    )
                }
            }
        }
    }

    private suspend fun drawTube(canvas: Canvas) {
        for (i in 0 until numberOfTubes) {
            withContext(Dispatchers.Main) {
                canvas.drawBitmap(
                    mTopTube!!,
                    tube!!.tubeX[i].toFloat(),
                    (tube!!.topTubeY[i] - mTopTube!!.height).toFloat(),
                    null
                )
                canvas.drawBitmap(
                    mBottomTube!!,
                    tube!!.tubeX[i].toFloat(),
                    (tube!!.topTubeY[i] + tube!!.gap).toFloat(),
                    null
                )
            }
        }
    }

    private val delay = 100L
    private var current = System.currentTimeMillis()
    private suspend fun drawBird(canvas: Canvas) {
        if (System.currentTimeMillis() - current >= delay) {
            current = System.currentTimeMillis()
            birdFrame = if (birdFrame == 0) 1 else 0
        }
        withContext(Dispatchers.Main) {
            canvas.drawBitmap(mBirds[birdFrame], bird!!.birdX, bird!!.birdY, null)
        }
    }

    private suspend fun clearBackGroundGame(canvas: Canvas) {
        val clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        withContext(Dispatchers.Main) {
            canvas.drawRect(
                0f,
                0f,
                mDisplayWidth.toFloat(),
                mDisplayHeight.toFloat(),
                clearPaint
            )
        }
    }

    private fun startMediaPoint() {
        createMediaPoint()
        mediaPoint?.start()
    }

    private fun createMediaPoint() {
        mediaPoint?.reset()
        mediaPoint = MediaPlayer.create(this, R.raw.point)
    }

    private fun startMediaHit() {
        createMediaHit()
        mediaHit?.start()
    }

    private fun createMediaHit() {
        mediaHit?.reset()
        mediaHit = MediaPlayer.create(this, R.raw.hit)
    }

    private fun startMediaWing() {
        createMediaWing()
        mediaWing?.start()
    }

    private fun createMediaWing() {
        mediaWing?.reset()
        mediaWing = MediaPlayer.create(this, R.raw.wing)
    }
}