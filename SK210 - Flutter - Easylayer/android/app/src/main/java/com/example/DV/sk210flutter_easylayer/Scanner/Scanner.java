package com.example.DV.sk210flutter_easylayer.Scanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.DV.sk210flutter_easylayer.R;

import java.util.ArrayList;

import br.com.gertec.easylayer.codescanner.CodeScanner;

public class Scanner extends AppCompatActivity {
    private ArrayList<String> consultaOrder;
    private ArrayAdapter<String> adapter;
    private ListView listScanner;
    private Button btnStart;
    private Button btnStop;
    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scanner);

        codeScanner = CodeScanner.getInstance(this);
        consultaOrder = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, consultaOrder);

        listScanner = findViewById(R.id.listScanner);
        listScanner.setAdapter(adapter);

//        TextView txtStatus = findViewById(R.id.status);
//        txtStatus.setText("Inicie o serviço");

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(view -> {
            codeScanner.scanCode(this);
            btnStart.setClickable(false);
            btnStart.setBackgroundResource(R.drawable.shape_btn_off);
            btnStop.setBackgroundResource(R.drawable.shape_stop);
            btnStop.setClickable(true);

            Toast.makeText(Scanner.this, "Serviço iniciado", Toast.LENGTH_SHORT).show();
        });

        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(view -> {
            stopScannerService();

            btnStart.setClickable(true);
            btnStart.setBackgroundResource(R.drawable.shape_start);
            btnStop.setBackgroundResource(R.drawable.shape_btn_off);
            btnStop.setClickable(false);
            Toast.makeText(Scanner.this, "Serviço finalizado", Toast.LENGTH_SHORT).show();
        });

        btnStop.setClickable(false);
        btnStart.setBackgroundResource(R.drawable.shape_start);
        btnStop.setBackgroundResource(R.drawable.shape_btn_off);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScannerService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra("content");
            if (content != null) {
                consultaOrder.add(content);
                adapter.notifyDataSetChanged();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void stopScannerService() {
        try {
            codeScanner.stopService();
            Toast.makeText(Scanner.this, "Stop Scanner!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
