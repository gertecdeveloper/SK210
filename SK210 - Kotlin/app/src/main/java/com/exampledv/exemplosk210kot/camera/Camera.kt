package com.exampledv.exemplosk210kot.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.exampledv.exemplosk210kot.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class Camera : AppCompatActivity() {
    private lateinit var scanBtn: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        scanBtn = findViewById(R.id.scanner)
        textView = findViewById(R.id.text)

        scanBtn.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this@Camera)
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.setPrompt("Scan a QR Code")
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            intentIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            val contents = intentResult.contents
            if (contents != null) {
                textView.text = contents
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}
