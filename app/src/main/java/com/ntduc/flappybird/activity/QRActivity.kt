package com.ntduc.flappybird.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.displayWidth
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.databinding.ActivityQrBinding
import com.ntduc.flappybird.model.Info
import com.ntduc.flappybird.model.User
import com.ntduc.flappybird.repository.Repository
import com.ntduc.flappybird.utils.PermissionUtil
import com.ntduc.toastutils.shortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class QRActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrBinding

    private var valueEventListener: ValueEventListener? = null
    private var rf: DatabaseReference? = null

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

    override fun onDestroy() {
        super.onDestroy()
        if (valueEventListener != null && rf != null) {
            rf!!.removeEventListener(valueEventListener!!)
        }
    }

    override fun onBackPressed() {
        Repository.user!!.match.isCreated = false
        rf!!.setValue(Repository.user)
        super.onBackPressed()
    }

    private fun init() {
        initData()
        initEvent()
    }

    private fun initEvent() {
        rf = Firebase.database.getReference(Repository.user!!.info!!.uid)
        valueEventListener = object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val userDB = dataSnapshot.getValue<User>()

                if (userDB != null && !userDB.match.isCreated && userDB.match.isConnected && userDB.match.rival != null) {
                    Repository.user = userDB
                    Repository.rival = userDB.match.rival
                    Repository.isHost = true
                    startActivity(Intent(this@QRActivity, PrepareActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        rf!!.addValueEventListener(valueEventListener!!)

        binding.scan.setOnClickShrinkEffectListener {
            if (PermissionUtil.checkPermissionCamera(this)) {
                startActivity(Intent(this, ScanQRActivity::class.java))
            } else {
                requestCameraPermission()
            }
        }
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

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA
            ),
            REQ_CAMERA_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA_CODE) {
            for (permission in permissions) {
                if (permission == Manifest.permission.CAMERA) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            requestCameraPermission()
                        } else {
                            shortToast("Vui lòng cấp quyền camera đi bạn")
                            goSetting()
                        }
                    } else {
                        startActivity(Intent(this, ScanQRActivity::class.java))
                    }
                }
            }
        }
    }

    private fun goSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:" + this.packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(intent)
    }

    companion object {
        private const val REQ_CAMERA_CODE = 4097
    }
}