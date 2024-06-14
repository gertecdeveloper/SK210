package com.example.easylayer_217_sk210.Scanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.easylayer_217_sk210.R;

import java.util.ArrayList;

import br.com.gertec.easylayer.codescanner.CodeScanner;

public class Scanner extends AppCompatActivity {

    private ArrayList<String> consultaOrder;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanner);

        //Instanciando a classe CodeScanner
        CodeScanner codeScanner = CodeScanner.getInstance(this);
        consultaOrder = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, consultaOrder);

        ListView listScanner = findViewById(R.id.listScanner);
        listScanner.setAdapter(adapter);

        //Botão de iniciar
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(view -> {

            //Iniciar o scanner
            codeScanner.scanCode(Scanner.this);

        });

        //Botão de parar
        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(view -> {

            //Para o scanner
            codeScanner.stopService();

        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra("content");

            //Exibir o código lido através de um ListView
            if (content != null) {
                consultaOrder.add(content);
                adapter.notifyDataSetChanged();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}