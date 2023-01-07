package com.ntduc.flappybird.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ntduc.clickeffectutils.setOnClickShrinkEffectListener
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.databinding.ActivityCompeteBinding
import com.ntduc.flappybird.repository.Repository
import com.ntduc.flappybird.utils.PermissionUtil
import com.ntduc.toastutils.shortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompeteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompeteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompeteBinding.inflate(inflater)
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
        initEvent()
    }

    private fun initEvent() {
        binding.host.setOnClickShrinkEffectListener {
            GlobalScope.launch(Dispatchers.IO) {
                Repository.user!!.match.isCreated = true
                Firebase.database.getReference(Repository.user!!.info!!.uid)
                    .setValue(Repository.user)
            }

            startActivity(Intent(this@CompeteActivity, QRActivity::class.java))
        }

        binding.join.setOnClickShrinkEffectListener {
            if (PermissionUtil.checkPermissionCamera(this)) {
                startActivity(Intent(this, ScanQRActivity::class.java))
            } else {
                requestCameraPermission()
            }
        }
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