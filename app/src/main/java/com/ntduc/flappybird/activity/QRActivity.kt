package com.ntduc.flappybird.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.ntduc.contextutils.displayWidth
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.databinding.ActivityQrBinding
import com.ntduc.flappybird.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class QRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(inflater)
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

    override fun onBackPressed() {
        GlobalScope.launch(Dispatchers.IO){
            Repository.user!!.match.isCreated = false
            Firebase.database.getReference(Repository.user!!.info!!.uid)
                .setValue(Repository.user)
        }

        super.onBackPressed()
    }

    private fun init() {
        initData()
        initEvent()
    }

    private fun initEvent() {
//        TODO("Not yet implemented")
    }

    private fun initData() {
        generateQRCode()
    }

    private fun generateQRCode() {
        val bitmap = generateQr(displayWidth, Repository.user!!.info!!.uid)
        Glide.with(this)
            .load(bitmap)
            .into(binding.qr)
        return
    }

    private fun generateQr(size: Int, uid: String): Bitmap {
        val hints = Hashtable<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = "0"

        val multiFormatWriter = MultiFormatWriter()

        val bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE, size, size, hints);
        val pixels = IntArray(size * size)
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (bitMatrix[j, i]) { // True if is is Black
                    pixels[i * size + j] = -0x1 //White
                } else {
                    pixels[i * size + j] = 0x294628 //Insert the color here.
                }
            }
        }
        return Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
    }

}