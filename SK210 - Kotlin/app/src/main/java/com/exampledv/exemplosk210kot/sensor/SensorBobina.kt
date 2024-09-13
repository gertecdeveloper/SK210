package com.exampledv.exemplosk210kot.sensor

import android.os.Bundle
import android.os.RemoteException
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exampledv.exemplosk210kot.R
import com.exampledv.exemplosk210kot.util.DeviceServiceManager

class SensorBobina : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_bobina)

        val redButton = findViewById<Button>(R.id.redButton)
        redButton.setOnClickListener {
            // Verificar o estado do papel
            val paperState = checkPaperState()

            // Verificar se a tampa da impressora está aberta
            val isCoverOpen = checkCoverState()

            // Verificar o status da impressora
            val printerState = checkPrinterState()
            if (paperState == 0) {
                Toast.makeText(
                    this@SensorBobina,
                    "O papel está quase acabando",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (printerState == 0x01) {
                // A impressora está sem papel, acender o LED vermelho

                Toast.makeText(
                    this@SensorBobina,
                    "O papel acabou",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (printerState == 0x00 && !isCoverOpen) {
                Toast.makeText(
                    this@SensorBobina,
                    "Há papel suficiente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Método para verificar o estado do papel
    private fun checkPaperState(): Int {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().getPosPrintPaperState()
        } catch (e: RemoteException) {
            throw RuntimeException(e)
        }
    }

    // Método para verificar se a tampa da impressora está aberta
    private fun checkCoverState(): Boolean {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().isPosPrinterCoverOpen()
        } catch (e: RemoteException) {
            throw RuntimeException(e)
        }
    }

    // Método para verificar o status da impressora
    private fun checkPrinterState(): Int {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().getPrinterState()
        } catch (e: RemoteException) {
            throw RuntimeException(e)
        }
    }
}