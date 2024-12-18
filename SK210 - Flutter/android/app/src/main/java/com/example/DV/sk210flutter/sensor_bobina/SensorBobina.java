package com.example.DV.sk210flutter.sensor_bobina;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.DV.sk210flutter.DeviceServiceManager;
import com.example.DV.sk210flutter.R;

public class SensorBobina extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_bobina);

        Button redButton = findViewById(R.id.redButton);
        redButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Verificar o estado do papel
                int paperState = checkPaperState();

                // Verificar se a tampa da impressora está aberta
                boolean isCoverOpen = checkCoverState();

                // Verificar o status da impressora
                int printerState = checkPrinterState();
                if (paperState == 0) {
                    Toast.makeText(SensorBobina.this, "O papel está quase acabando", Toast.LENGTH_SHORT).show();

                } else if (printerState == 0x01) {
                    // A impressora está sem papel, acender o LED vermelho

                    Toast.makeText(SensorBobina.this, "O papel acabou", Toast.LENGTH_SHORT).show();

                } else if (printerState == 0x00 && !isCoverOpen) {

                    Toast.makeText(SensorBobina.this, "Há papel suficiente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // Método para verificar o estado do papel
    private int checkPaperState() {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().getPosPrintPaperState();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para verificar se a tampa da impressora está aberta
    private boolean checkCoverState() {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().isPosPrinterCoverOpen();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para verificar o status da impressora
    private int checkPrinterState() {
        try {
            return DeviceServiceManager.getInstance().getPrintManager().getPrinterState();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
