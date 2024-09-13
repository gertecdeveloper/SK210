package com.exampledv.exemplosk210kot

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.exampledv.exemplosk210kot.camera.Camera
import com.exampledv.exemplosk210kot.impressora.Printer
import com.exampledv.exemplosk210kot.scanner.Scanner
import com.exampledv.exemplosk210kot.sensor.SensorActivity
import com.exampledv.exemplosk210kot.sensor.SensorBobina
import com.exampledv.exemplosk210kot.tef.TefActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnPrinter: Button
    private lateinit var btnScanner: Button
    private lateinit var btnCamera: Button
    private lateinit var btnSensorBobina: Button
    private lateinit var btnSensorPresenca: Button
    private lateinit var btnTef: Button
    private lateinit var infor: Button

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o modo full screen
        window.setDecorFitsSystemWindows(false)
        val controller = ViewCompat.getWindowInsetsController(window.decorView)
        controller?.let {
            it.hide(WindowInsetsCompat.Type.systemBars()) // Oculta as barras de sistema
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // Permite que as barras apareçam com swipe
        }
        setContentView(R.layout.activity_main)

        // Inicializa os botões
        btnPrinter = findViewById(R.id.btnPrinter)
        btnScanner = findViewById(R.id.btnScanner)
        btnCamera = findViewById(R.id.btnCamera)
        btnSensorBobina = findViewById(R.id.btnSensorBobina)
        btnSensorPresenca = findViewById(R.id.btnSensorPresenca)
        btnTef = findViewById(R.id.btnTef)
        infor = findViewById(R.id.infor)

        // Ação para abrir o diálogo ao clicar no botão "Infor"
        infor.setOnClickListener {
            // Constrói o AlertDialog
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Informações")
                .setMessage("Equipamento: SK210\nSDK: DecodeLibrary_1.8.03.A28\nLinguagem: Kotlin\nVersão do app: 1.0.0")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss() // Fecha o diálogo ao clicar em OK
                }
                .create()

            // Exibe o AlertDialog
            alertDialog.show()
        }

        // Configuração dos outros botões
        btnPrinter.setOnClickListener {
            val intent = Intent(this, Printer::class.java)
            startActivity(intent)
        }

        btnScanner.setOnClickListener {
            val intent = Intent(this, Scanner::class.java)
            startActivity(intent)
        }

        btnCamera.setOnClickListener {
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }

        btnSensorPresenca.setOnClickListener {
            val intent = Intent(this, SensorActivity::class.java)
            startActivity(intent)
        }

        btnSensorBobina.setOnClickListener {
            val intent = Intent(this, SensorBobina::class.java)
            startActivity(intent)
        }

        btnTef.setOnClickListener {
            val intent = Intent(this, TefActivity::class.java)
            startActivity(intent)
        }
    }
}
