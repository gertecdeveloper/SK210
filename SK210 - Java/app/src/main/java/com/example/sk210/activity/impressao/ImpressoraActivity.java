package com.example.sk210.activity.impressao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sk210.R;
import com.example.sk210.util.DeviceServiceManager;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.PrintCuttingMode;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.printer.TextUnit;


import java.text.SimpleDateFormat;
import java.util.Date;

public class ImpressoraActivity extends AppCompatActivity {
    private Button buttonText,buttonImage,buttonFrase;
    private EditText editText;
    private Context mContext = ImpressoraActivity.this;
    volatile boolean mInPrinter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);

        buttonText = findViewById(R.id.btnImprimirTexto);
        buttonImage = findViewById(R.id.btnImprimirImagem);
        buttonFrase = findViewById(R.id.btnImprimirFrase);
        editText = findViewById(R.id.editText);

        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = editText.getText().toString();
                if (texto.isEmpty()) {
                    Toast.makeText(mContext, "Digite um texto", Toast.LENGTH_SHORT).show();
                } else {
                    imprimirTexto(texto);
                }
            }
        });

    buttonImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            imprimirImagem(BitmapFactory.decodeResource(getResources(), R.drawable.park));
        }
    });
        buttonFrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               imprimirFrase();
            }
        });
    }
    public void imprimirTexto(String texto) {
        try {
            String startTime = getCurTime();
            texto = editText.getText().toString(); // Obtém o texto do EditText
            PrintTemplate template = PrintTemplate.getInstance();
            template.init(mContext);
            template.clear();
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit(texto, 60, Align.CENTER));
            DeviceServiceManager.getInstance().getPrintManager().addRuiImage(rotateBitmap(template.getPrintBitmap(), 180), 0);
            DeviceServiceManager.getInstance().getPrintManager().printRuiQueue(new AidlPrinterListener.Stub() {
                @Override
                public void onError(int i) throws RemoteException {
                    mInPrinter = false;
                }

                @Override
                public void onPrintFinish() throws RemoteException {
                    DeviceServiceManager.getInstance().getPrintManager().cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT);
                    mInPrinter = false;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void imprimirImagem(Bitmap bitmap) {
        try {
            PrintTemplate template = PrintTemplate.getInstance();
            template.init(mContext);
            template.clear();
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit("\n\n"));
            template.add(new ImageUnit(bitmap, 400, 200));
            DeviceServiceManager.getInstance().getPrintManager().addRuiImage(rotateBitmap(template.getPrintBitmap(), 180), 0);
            DeviceServiceManager.getInstance().getPrintManager().printRuiQueue(new AidlPrinterListener.Stub() {
                @Override
                public void onError(int i) throws RemoteException {
                    mInPrinter = false;
                }

                @Override
                public void onPrintFinish() throws RemoteException {
                    DeviceServiceManager.getInstance().getPrintManager().cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT);
                    mInPrinter = false;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void imprimirFrase() {
        try {
            String startTime = getCurTime();
            String texto = "O Smart Kiosk SK210 é a solução ideal para quem busca\n" +
                    "inovar no atendimento com um baixo investimento e\n" +
                    "facilidade de instalação"; // Texto pré-definido
            PrintTemplate template = PrintTemplate.getInstance();
            template.init(mContext);
            template.clear();
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit(texto, 34, Align.CENTER)); // Adiciona o texto ao template
            DeviceServiceManager.getInstance().getPrintManager().addRuiImage(rotateBitmap(template.getPrintBitmap(), 180), 0);
            DeviceServiceManager.getInstance().getPrintManager().printRuiQueue(new AidlPrinterListener.Stub() {
                @Override
                public void onError(int i) throws RemoteException {
                    mInPrinter = false;
                }

                @Override
                public void onPrintFinish() throws RemoteException {
                    DeviceServiceManager.getInstance().getPrintManager().cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT);
                    mInPrinter = false;
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Bitmap rotateBitmap(Bitmap original, float degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
        original.recycle();
        return rotatedBitmap;
    }

    private String getCurTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String time = format.format(date);
        return time;
    }
}