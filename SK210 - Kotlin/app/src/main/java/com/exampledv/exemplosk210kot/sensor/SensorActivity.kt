package com.exampledv.exemplosk210kot.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exampledv.exemplosk210kot.R


class SensorActivity : AppCompatActivity() {
    private lateinit var sensorStatusTV: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var proximitySensor: Sensor
    private lateinit var sensorListView: ListView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var sensorData: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        sensorStatusTV = findViewById(R.id.sensorStatusTV)
        sensorListView = findViewById(R.id.sensorListView)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        sensorData = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sensorData)
        sensorListView.adapter = adapter

        startButton.setOnClickListener { startSensor() }
        stopButton.setOnClickListener { stopSensor() }

        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)!!

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startSensor() {
        sensorManager.registerListener(proximitySensorEventListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun stopSensor() {
        sensorManager.unregisterListener(proximitySensorEventListener)
    }

    private val proximitySensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                val status = if (event.values[0] == 0f) "Near" else "Away"
                sensorStatusTV.text = status
                sensorData.add(status)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}
