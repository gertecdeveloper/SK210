package com.example.sk210.activity.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sk210.R;
import com.example.sk210.activity.camera.CameraActivity;
import com.example.sk210.activity.scanner.ScannerActivity;
import com.example.sk210.activity.sensor_presenca.SensorActivity;
import com.example.sk210.activity.sensor_bobina.SensorBobinaActivity;
import com.example.sk210.activity.tef.TefActivity;
import com.example.sk210.activity.impressao.ImpressoraActivity;
import com.example.sk210.adapter.ContantsAdapter;
import com.example.sk210.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String version = "v1.0";

    private String[] appPermissions ={
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final int CODIGO_PERMISSOES_REQUISITADAS=1;

    TextView txtProject;

    ArrayList<Constants> projetos = new ArrayList<Constants>();
    ListView lvConstants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtProject = findViewById(R.id.txtNameProject);
        lvConstants= findViewById(R.id.lvConstants);

        txtProject.setText("Android Studio "+ version+" - SK210");

        projetos.add(new Constants("Câmera", R.drawable.ic_camera));
        projetos.add(new Constants("Scanner",R.drawable.ic_scanner));
        projetos.add(new Constants("Impressão",R.drawable.ic_print));
        projetos.add(new Constants("Tef",R.drawable.ic_tef));
        projetos.add(new Constants("Sensor de Presença",R.drawable.ic_sensors));
        projetos.add(new Constants("Sensor de Bobina",R.drawable.ic_sensor_bobina));

        ContantsAdapter adapter = new ContantsAdapter(getBaseContext(), R.layout.listconstants, projetos);
        lvConstants.setAdapter(adapter);

        lvConstants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Constants constants = (Constants) lvConstants.getItemAtPosition(i);

                Intent intent = null;
                switch (constants.getNome()){
                    case "Câmera":
                        intent = new Intent(MainActivity.this, CameraActivity.class);
                        break;
                    case "Scanner":
                        intent = new Intent(MainActivity.this, ScannerActivity.class);
                        break;
                    case "Impressão":
                        intent = new Intent(MainActivity.this, ImpressoraActivity.class);
                        break;
                    case "Tef":
                        intent = new Intent(MainActivity.this, TefActivity.class);
                        break;
                    case "Sensor de Presença":
                        intent = new Intent(MainActivity.this, SensorActivity.class);
                        break;
                    case "Sensor de Bobina":
                        intent = new Intent(MainActivity.this, SensorBobinaActivity.class);
                        break;
                }
                if(intent != null){
                    startActivity(intent);
                }
            }
        });
    }

    public boolean permissoes(){
        List<String> permissoesRequeridas = new ArrayList<>();

        for (String permissao : appPermissions){
            if(ContextCompat.checkSelfPermission(this,permissao) != PackageManager.PERMISSION_GRANTED){
                permissoesRequeridas.add(permissao);
            }
        }

        if(!permissoesRequeridas.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    permissoesRequeridas.toArray(new String[permissoesRequeridas.size()]),
                    CODIGO_PERMISSOES_REQUISITADAS);
            return false;
        }
        return true;
    }
}