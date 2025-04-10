package com.gertec.sk210.balancask210;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    //classes de comunicacao
    private BroadcastReceiver usbReceiver;
    private UsbManager manager;
    private UsbSerialPort serialPort;
    private UsbDeviceConnection connection;

    private SerialInputOutputManager usbIoManager;

    public static final String ACTION_USB_PERMISSION = "br.com..gertec.USB_PERMISSION";

    private Thread thread;

    TextView text_status, text_peso;
    Button btn_abreConexao, btn_fechaConexao;

    int numBytesRead = 0;
    String receivedData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //ativar modo fullscreen
        ativarFullScreen();

        //init componentes
        text_peso = findViewById(R.id.text_peso);
        text_status = findViewById(R.id.text_status);
        btn_abreConexao = findViewById(R.id.btn_abreConexao);
        btn_fechaConexao = findViewById(R.id.btn_fechaConexao);

        //oculta o peso ao iniciar a aplicacao
        runOnUiThread(() -> text_peso.setVisibility(View.INVISIBLE));


        //===================================CONEXAO USB-SERIAL===================================//

        //broadcast para iniciar a conexao
        usbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                try {
                    if (device != null) {
                        UsbSerialDriver usbDriverSerial = UsbSerialProber.getDefaultProber().probeDevice(device);

                        if (usbDriverSerial != null) {
                            //pede permissao, se necessario
                            if (ACTION_USB_PERMISSION.equals(action)) {

                                if (intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                                    //tratativa para permissao concedida
                                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                                        try {
                                            //inicia a balanca
                                            iniciaBalanca(usbDriverSerial, manager);

                                        } catch (IOException e) {
                                            runOnUiThread(() -> text_status.setText("Falha ao abrir comunicação com a balança."));
                                            Log.e("USB-Serial", "Falha ao abrir a porta serial", e);

                                        }
                                    } else {
                                        runOnUiThread(() -> text_status.setText("Permissão negada para acessar o dispositivo USB"));
                                        Log.e("USB-Serial", "Permissão negada para acessar o dispositivo USB");
                                    }
                                }

                                //tratativa para balança removida
                            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                                runOnUiThread(() -> text_status.setText("Balança removida."));

                                try {
                                    fechaConexao();

                                } catch (Throwable t) {
                                    t.printStackTrace(); // Registra o erro sem fechar o app
                                    Log.w("USB-Serial", "Balança removida!");
                                }

                            } else {
                                //balança detectada
                                runOnUiThread(() -> text_status.setText("Balança detectada, abra a conexão."));

                                try {

                                    //inicia a balanca
                                    iniciaBalanca(usbDriverSerial, manager);

                                } catch (IOException e) {
                                    Log.e("USB-Serial", "Falha ao abrir a porta serial", e);
                                    runOnUiThread(() -> text_status.setText("Falha ao abrir comunicação com a balança."));
                                }
                            }
                        }
                    }
                } catch (Throwable t) {
                    fechaConexao();
                    t.printStackTrace(); // Registra o erro sem fechar o app

                }
            }
        };

        //botao de abrir conexao
        btn_abreConexao.setOnClickListener(v -> {

            UsbSerialDriver device = null;
            manager = (UsbManager) getSystemService(Context.USB_SERVICE);

            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

            for (UsbSerialDriver availableDriver : availableDrivers) {
                device = availableDriver;
            }

            if (device != null) {

                try {
                    iniciaBalanca(device, manager);

                } catch (IOException e) {
                    Log.e("USB-Serial", "Falha ao abrir a porta serial", e);
                    runOnUiThread(() -> text_status.setText("Falha ao abrir comunicação com a balança"));
                }
            }
        });

        //botao de fechar conexao
        btn_fechaConexao.setOnClickListener(v -> {
            fechaConexao();
        });
    }


    //inicia a balanca
    private void iniciaBalanca(UsbSerialDriver driver, UsbManager usbManager) throws IOException {
        try {

            //validacao de permissao
            if (!usbManager.hasPermission(driver.getDevice())) {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
                usbManager.requestPermission(driver.getDevice(), permissionIntent);

                runOnUiThread(() -> text_status.setText("Abra novamente para confirmar a conexão."));
                Log.i("USB-Serial", "pediu permissão para conectar");
                return;
            } else {
                connection = usbManager.openDevice(driver.getDevice());
                Log.w("USB-Serial", "não foi necessario pedir permissão");
            }
            if (connection == null) {
                runOnUiThread(() -> text_status.setText("Não foi possível se comunicar com a balança."));
                Toast.makeText(this, "Não foi possivel comunicar com a balança", Toast.LENGTH_SHORT).show();
                return;

            }

            //pega a porta serial e abre a conexao
            serialPort = driver.getPorts().get(0);
            serialPort.open(connection);

            //configuracao de trafego de dados
            serialPort.setParameters(4800, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialPort.FlowControl.NONE);

            if (serialPort.isOpen()) { //porta aberta

                Log.i("USB-Serial", "Comunicação estabelecida com a balança");
                runOnUiThread(() -> text_status.setText("Comunicação estabelecida com a balança."));

                runOnUiThread(() -> text_peso.setVisibility(View.VISIBLE));

                //tratativa de retorno da balanca
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                //configuracao de dados para leitura
                                byte[] buffer = new byte[64]; // Buffer de leitura
                                numBytesRead = serialPort.read(buffer, 1000);
                                receivedData = new String(buffer, 0, numBytesRead);

                                if (numBytesRead == 0) {
                                    runOnUiThread(() -> text_peso.setText("00.00 kg"));

                                } else {
                                    //leitura
                                    runOnUiThread(() -> text_peso.setText(receivedData + "kg"));
                                    Log.d("USB-Serial", "Recebido: " + receivedData + "kg");
                                }
                                try {
                                    Thread.sleep(1000); // Atraso de 1 segundo para estabilizacao da balanca
                                } catch (Throwable t) {
                                    t.printStackTrace(); // Registra o erro sem fechar o app
                                }

                            } catch (Throwable t) {
                                t.printStackTrace(); // Registra o erro sem fechar o app

                            }

                        }
                    }
                });

                thread.start();


            } else {
                Log.e("USB-Serial", "Não foi possível comunicar com a balança.");
                runOnUiThread(() -> text_status.setText("Não foi possível comunicar com a balança."));
                return;
            }

            usbIoManager = new SerialInputOutputManager(serialPort, this);
            usbIoManager.start();

        } catch (Throwable t) {
            runOnUiThread(() -> text_status.setText("Houve um erro ao tentar se conectar novamente."));
            Log.e("USB-Serial", "iniciaBalanca:Houve um erro ao tentar se conectar novamente. ");

            t.printStackTrace(); // Registra o erro sem fechar o app

        }
    }

    private void fechaConexao() {
        try {

            if (usbIoManager != null) {

                usbIoManager.stop();
            }

            if (serialPort != null && serialPort.isOpen()) {

                thread.interrupt();
                serialPort.close();

            }
            runOnUiThread(() -> text_status.setText("Conexão fechada."));
            runOnUiThread(() -> text_peso.setVisibility(View.INVISIBLE));

        } catch (Throwable t) {
            t.printStackTrace(); // Registra o erro sem fechar o app
        }

    }


    //ativar fullscreen
    private void ativarFullScreen() {
        // Ativa o modo full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }

        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(decorView);

        if (controller != null) {
            // Oculta as barras de sistema
            controller.hide(WindowInsetsCompat.Type.systemBars());
            // Permite que as barras apareçam com swipe
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    //===================================CICLO DE VIDA===================================//
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //finaliza a conexao
        if (usbIoManager != null) {
            usbIoManager.stop();
        }
        try {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.close();
            }
        } catch (IOException e) {
            Log.e("USB-Serial", "Falha ao fechar a porta serial: " + e.getMessage());
            runOnUiThread(() -> text_status.setText("Falha ao encerrar comunicação com a balança: " + e.getMessage()));
        }
        unregisterReceiver(usbReceiver);
    }


    @Override
    public void onNewData(byte[] data) {

    }

    @Override
    public void onRunError(Exception e) {

    }
}