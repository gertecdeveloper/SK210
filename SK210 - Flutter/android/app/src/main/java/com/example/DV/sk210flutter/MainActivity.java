package com.example.DV.sk210flutter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.DV.sk210flutter.impressao.ImpressoraActivity;
import com.example.DV.sk210flutter.sensor_bobina.SensorBobina;
import com.example.DV.sk210flutter.sensor_presenca.SensorPresence;
import com.example.DV.sk210flutter.tef.Tef;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    public static final String G720 = "SK210";
    private MethodChannel.Result _result; // Instanciando uma variavel do tipo Result, para enviar o resultado para o
    // flutter
    public static String Model = Build.MODEL;
    private String resultado_Leitor; // Instanciando uma variavel que vai armazenar o resultado ao ler o codigo de
    // Barras no V1
    private IntentIntegrator qrScan;
    private String tipo; // Armazerna o tipo de codigo de barra que ser lido
    private ArrayList<String> arrayListTipo;
    private static final String CHANNEL = "samples.flutter.dev/gedi"; // Canal de comunicação do flutter com o Java
    Gson gson = new Gson();

    public MainActivity() {
        super();
        this.arrayListTipo = new ArrayList<String>();
    }

    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    _result = result;
                    Map<String, String> map;
                    Bundle bundle = new Bundle();
                    Intent intent = null;
                    switch (call.method) {
                        case "imprimir":
                            intent = new Intent(MainActivity.this, ImpressoraActivity.class);
                            startActivity(intent);
                            break;

                        case "leitorscann":
                            intent = new Intent(MainActivity.this, LeitorScann.class);
                            startActivity(intent);
                            break;

                        case "sensorbobina":
                            intent = new Intent(MainActivity.this, SensorBobina.class);
                            startActivity(intent);
                            break;

                        case "sensorpresence":
                            intent = new Intent(MainActivity.this, SensorPresence.class);
                            startActivity(intent);
                            break;

                        case "tef":
                            intent = new Intent(MainActivity.this, Tef.class);
                            startActivity(intent);
                            break;
                    }
                });
    }


}
