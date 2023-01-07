package com.ntduc.flappybird.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.Result
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.restartApp
import com.ntduc.flappybird.customview.CustomViewFinderView
import com.ntduc.flappybird.databinding.ActivityScanQrBinding
import com.ntduc.flappybird.repository.Repository
import com.ntduc.toastutils.shortToast
import me.dm7.barcodescanner.core.IViewFinder
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanQRActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var binding: ActivityScanQrBinding
    private var scannerView: ZXingScannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    override fun onStart() {
        super.onStart()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null || Repository.user == null) {
            restartApp()
        }

        if (!availableCameraPms()) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (availableCameraPms()) {
            startScan()
            return
        }
    }

    override fun onPause() {
        super.onPause()
        stopScan()
    }

    private fun init() {
        initView()
        initEvent()
    }

    private fun initEvent() {
        binding.toolbar.back.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        scannerView = object : ZXingScannerView(this) {
            override fun createViewFinderView(context: Context): IViewFinder {
                val viewFinderView = CustomViewFinderView(context)
                viewFinderView.setLaserEnabled(true)
                return viewFinderView
            }
        }

        scannerView!!.setResultHandler(this)
        binding.flCameraView.addView(scannerView)
    }

    override fun handleResult(rawResult: Result) {
        stopScan()

        val text = rawResult.text
        checkResultScan(text)
    }

    private fun stopScan() {
        if (scannerView != null) {
            scannerView!!.stopCameraPreview()
            scannerView!!.stopCamera()
        }
    }

    private fun startScan() {
        if (scannerView != null) {
            scannerView!!.setResultHandler(this)
            scannerView!!.startCamera()
        }
    }

    private fun checkResultScan(result: String) {
        if (TextUtils.isEmpty(result)) {
            startScan()
            shortToast("Mã QR không hợp lệ")
            return
        }

        connectToMatch(result)
    }

    private fun connectToMatch(id: String) {
        Log.d("ntduc_debug", "connectToMatch: $id")
    }

    private fun availableCameraPms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}