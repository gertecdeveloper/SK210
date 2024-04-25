package com.example.scan_teste_cliente;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.scan_teste_cliente.util.DeviceServiceManager;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.camera.AidlDecodeCallBack;
import com.topwise.cloudpos.aidl.camera.DecodeMode;
import com.topwise.cloudpos.aidl.camera.DecodeParameter;
import com.topwise.cloudpos.aidl.system.AidlSystem;

import java.util.ArrayList;

public class MainActivity  extends Activity implements View.OnClickListener {

        private Context mContext = MainActivity.this;
        AidlCameraScanCode mDecodeManager;
        AidlSystem sytem;
        private Button startButton, stopButton, clearButton;
        private ListView barcodeListView;
        private ArrayList<String> barcodeList = new ArrayList<>();
        private ArrayAdapter<String> barcodeAdapter;
        private Handler handler1 = new Handler();
        private boolean isDecoding = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Configurando a janela da atividade
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // Inicializando a camera
            mDecodeManager = com.example.scan_teste_cliente.util.DeviceServiceManager.getInstance().getCameraManager();
            sytem = DeviceServiceManager.getInstance().getSystemManager();

            startButton = findViewById(R.id.start_button);
            stopButton = findViewById(R.id.stop_button);
            clearButton = findViewById(R.id.clear_button);
            barcodeListView = findViewById(R.id.barcode_list);

            startButton.setOnClickListener(this);
            stopButton.setOnClickListener(this);
            clearButton.setOnClickListener(this);
            // Configurando o adaptador para a lista de códigos de barras
            barcodeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, barcodeList);
            barcodeListView.setAdapter(barcodeAdapter);

        }
        // Método que é chamado quando um botão é clicado

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.start_button) {
                startDecode();
            } else if (v.getId() == R.id.stop_button) {
                stopDecode();
            } else if (v.getId() == R.id.clear_button) {
                clearList();
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

        //Trata exceção caso tenha algum objeto nulo 
//    private void stopDecode() {
//        try {
//            mDecodeManager.stopDecode();
//            isDecoding = false;
//        } catch (RemoteException e) {
//            if (e instanceof DeadObjectException) {
//                Log.e("MainActivity", "O processo hospedeiro foi encerrado", e);
//            } else {
//                e.printStackTrace();
//            }
//        }
//
//    }

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

        private void handleDecode(String result) {
            if (result != null) {
                // Removendo os asteriscos e outros caracteres especiais do código de barras
                String cleanedResult = result.replaceAll("[^\\d]", "");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Exibindo apenas o número do código de barras na tela
                        barcodeList.add(cleanedResult);
                        barcodeAdapter.notifyDataSetChanged();
                    }
                });
                // Reinicia o scan após um atraso de 2000 ms
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restartDecode();
                    }
                }, 2000);
            }
        }


        private void clearList() {
            barcodeList.clear();
            barcodeAdapter.notifyDataSetChanged();
        }

        //Reinicia o scan após 2 segundos
        private void restartDecode() {
            stopDecode();

            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startDecode();
                }
            }, 1000);
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