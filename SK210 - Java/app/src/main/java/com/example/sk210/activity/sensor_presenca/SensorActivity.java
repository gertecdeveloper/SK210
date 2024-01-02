package com.example.sk210.activity.sensor_presenca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sk210.R;

import java.util.ArrayList;

public class SensorActivity extends AppCompatActivity {
    TextView sensorStatusTV;
    SensorManager sensorManager;
    Sensor proximitySensor;
    ListView sensorListView;
    Button startButton, stopButton;
    ArrayList<String> sensorData;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorStatusTV = findViewById(R.id.sensorStatusTV);
        sensorListView = findViewById(R.id.sensorListView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        sensorData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorData);
        sensorListView.setAdapter(adapter);

        startButton.setOnClickListener(v -> startSensor());
        stopButton.setOnClickListener(v -> stopSensor());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startSensor() {
        sensorManager.registerListener(proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sensorManager.unregisterListener(proximitySensorEventListener);
    }

    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    sensorStatusTV.setText("Near");
                } else {
                    sensorStatusTV.setText("Away");
                }
                sensorData.add(sensorStatusTV.getText().toString());
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
