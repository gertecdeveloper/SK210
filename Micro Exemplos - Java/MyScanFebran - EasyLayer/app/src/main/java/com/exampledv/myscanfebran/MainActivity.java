package com.exampledv.myscanfebran;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.gertec.easylayer.codescanner.CodeScanner;

public class MainActivity extends FullScreenActivity {

    private ArrayList<String> consultOrder;
    private ArrayAdapter<String> adapter;
    private CodeScanner codeScanner;
    private boolean startFebraban = true;

    // Componentes da UI
    private ListView listScanner;
    private Button btnFebraban;
    private SurfaceView febrabanPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialização
        codeScanner = CodeScanner.getInstance(this);
        consultOrder = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, consultOrder);

        // Referência aos componentes da UI
        listScanner = findViewById(R.id.listScanner);
        btnFebraban = findViewById(R.id.btnFebraban);
        febrabanPreview = findViewById(R.id.febrabanPreview);

        listScanner.setAdapter(adapter);

        // Clique no botão para ativar/desativar scanner
        btnFebraban.setOnClickListener(view -> setLayoutFebraban());
    }

    private void setLayoutFebraban() {
        if (startFebraban) {
            febrabanPreview.setVisibility(VISIBLE);

            // Iniciando o scanner com o SurfaceHolder
            codeScanner.scanCodeFebraban(this, febrabanPreview);
            btnFebraban.setBackgroundResource(R.drawable.shape_stop);
            btnFebraban.setText("Stop");

        } else {
            btnFebraban.setBackgroundResource(R.drawable.shape_start);
            btnFebraban.setText("Start");
            febrabanPreview.setVisibility(GONE);
            codeScanner.stopService();
        }

        startFebraban = !startFebraban;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (codeScanner != null) {
                codeScanner.stopService();
                Toast.makeText(MainActivity.this, "Stop Scanner!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            String content = data.getStringExtra("content");
            if (content != null) {
                febrabanPreview.setVisibility(GONE);
                if (!startFebraban) {
                    setLayoutFebraban();
                }
                consultOrder.add(content);
                adapter.notifyDataSetChanged();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
