package com.exampledv.exemplosk210kot.scanner

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.exampledv.exemplosk210kot.R
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode
import com.topwise.cloudpos.aidl.camera.AidlDecodeCallBack
import com.topwise.cloudpos.aidl.camera.DecodeMode
import com.topwise.cloudpos.aidl.camera.DecodeParameter
import com.topwise.cloudpos.aidl.system.AidlSystem

class Scanner : Activity(), View.OnClickListener {


    private val mContext: Context = this@Scanner
    private var mDecodeManager: AidlCameraScanCode? = null
    private lateinit var sytem: AidlSystem
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var clearButton: Button
    private lateinit var barcodeListView: ListView
    private val barcodeList = ArrayList<String>()
    private lateinit var barcodeAdapter: ArrayAdapter<String>
    private val handler1 = Handler()
    private var isDecoding = false
    private var mSoundPool: SoundPool? = null
    private var mSuccessSound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        initBeepSound()

        // Inicializando a câmera
        // Inicializando a camera
        mDecodeManager = com.exampledv.exemplosk210kot.util.DeviceServiceManager.getInstance().getCameraManager()
        if (mDecodeManager == null) {
            // Tratar o erro adequadamente
            return
        }

        sytem = com.exampledv.exemplosk210kot.util.DeviceServiceManager.getInstance().systemManager

        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        clearButton = findViewById(R.id.clear_button)
        barcodeListView = findViewById(R.id.barcode_list)

        startButton.setOnClickListener(this)
        stopButton.setOnClickListener(this)
        clearButton.setOnClickListener(this)

        // Configurando o adaptador para a lista de códigos de barras
        barcodeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, barcodeList)
        barcodeListView.adapter = barcodeAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.start_button -> startDecode()
            R.id.stop_button -> stopDecode()
            R.id.clear_button -> clearList()
        }
    }

    private fun startDecode() {
        if (!isDecoding) {
            try {
                //Inicializa o parametro do scan

                val decodeParameter = DecodeParameter()
                decodeParameter
                    .setDecodeMode(DecodeMode.MODE_SINGLE_SCAN_CODE)
                    .setFlashLightTimeout(-0x1)
                mDecodeManager!!.startDecode(decodeParameter, object : AidlDecodeCallBack.Stub() {
                    @Throws(RemoteException::class)
                    override fun onResult(s: String?) {
                        // Verifique se o resultado é nulo ou vazio
                        if (!s.isNullOrEmpty()) {
                            handleDecode(s)
                        } else {
                            // Tratamento para caso o resultado seja nulo ou vazio
                            Log.e("MainActivity", "Resultado de scan é nulo ou vazio.")
                        }
                    }


                    @Throws(RemoteException::class)
                    override fun onError(i: Int) {
                    }
                })
                isDecoding = true
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopDecode() {
        try {
            mDecodeManager?.stopDecode()
            isDecoding = false
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun handleDecode(result: String) {
        if (result.isNotEmpty()) {
            playBeepSound()
            mDecodeManager?.playDecodeSucLight()

            // Removendo caracteres especiais do código de barras
            val cleanedResult = result.replace("[^\\d]".toRegex(), "")
            runOnUiThread {
                barcodeList.add(cleanedResult)
                barcodeAdapter.notifyDataSetChanged()
            }

            // Reinicia o scan após um atraso de 2000 ms
            handler1.postDelayed({ restartDecode() }, 2000)
        }
    }

    private fun initBeepSound() {
        mSoundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        mSuccessSound = mSoundPool?.load(this, R.raw.beep1, 1) ?: 0
    }

    private fun playBeepSound() {
        mSoundPool?.play(mSuccessSound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private fun clearList() {
        barcodeList.clear()
        barcodeAdapter.notifyDataSetChanged()
    }

    private fun restartDecode() {
        stopDecode()
        handler1.postDelayed({ startDecode() }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDecodeManager?.let {
            try {
                it.stopDecode()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        mSoundPool?.release()
        isDecoding = false
    }
}