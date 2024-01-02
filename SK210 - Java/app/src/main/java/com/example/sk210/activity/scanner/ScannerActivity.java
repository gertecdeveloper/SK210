package com.example.sk210.activity.scanner;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.sk210.R;
import com.example.sk210.util.DeviceServiceManager;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.camera.AidlDecodeCallBack;
import com.topwise.cloudpos.aidl.camera.DecodeMode;
import com.topwise.cloudpos.aidl.camera.DecodeParameter;
import com.topwise.cloudpos.aidl.system.AidlSystem;

public class ScannerActivity extends Activity implements View.OnClickListener {

    private Context mContext = ScannerActivity.this;
    AidlCameraScanCode mDecodeManager;
    AidlSystem sytem;
    Button startButton, stopButton;
    private Handler handler1 = new Handler();
    private boolean isDecoding = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configurando a janela da atividade
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        // Inicializando a camera
        mDecodeManager = DeviceServiceManager.getInstance().getCameraManager();
        sytem = DeviceServiceManager.getInstance().getSystemManager();

        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

    }
    // Método que é chamado quando um botão é clicado


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_button) {
            startDecode();
        } else if (v.getId() == R.id.stop_button) {
            stopDecode();
        }
    }
    private void startDecode() {
        if (!isDecoding) {
            try {

                //Inicializa o parametro do scan

                DecodeParameter decodeParameter = new DecodeParameter();
                decodeParameter
                        .setDecodeMode(DecodeMode.MODE_SINGLE_SCAN_CODE)
                        .setFlashLightTimeout(0xffffffff);
                mDecodeManager.startDecode(decodeParameter, new AidlDecodeCallBack.Stub() {
                    @Override
                    public void onResult(String s) throws RemoteException {
                        handleDecode(s);
                    }

                    @Override
                    public void onError(int i) throws RemoteException {
                    }
                });
                isDecoding = true;

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    // Parando o scan
    private void stopDecode() {
        try {
            mDecodeManager.stopDecode();
            isDecoding = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Método para lidar com os resultados do scan
    private void handleDecode(String result) {
        if (result != null) {
            String productName = mapProductCodeToName(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Produto: " + productName, Toast.LENGTH_SHORT).show();
                    onDestroy();
                }
            });
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restartDecode();
                }
            }, 2000); // Reinicia o scan após um atraso de 2000 ms
        }
    }

    // Mapeia códigos de barras para nomes de produtos(opcional)
    private String mapProductCodeToName(String productCode) {
        Log.d("DecodeMainActivity", "Código de Barras Lido: " + productCode);
        if ("0123456789".equals(productCode)) {
            Log.d("DecodeMainActivity", "Produto A");
            return "Produto A";
        } else if ("1234567890142".equals(productCode)) {
            Log.d("DecodeMainActivity", "Produto B");
            return "Produto B";
        } else if ("1234567890159".equals(productCode)) {
            Log.d("DecodeMainActivity", "Produto C");
            return "Produto C";
        } else {
            Log.d("DecodeMainActivity", "Produto Desconhecido");
            return "Produto Desconhecido";
        }
    }

    //Reinicia o scan após 2 segundos
    private void restartDecode() {
        stopDecode();

        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startDecode();
            }
        }, 2000);
    }
    // Garante que o scan seja  interrompido ao encerrar a atividade

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDecodeManager != null) {
            try {
                mDecodeManager.stopDecode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        isDecoding = false;
    }
}