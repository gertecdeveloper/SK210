package com.example.DV.sk210flutter.impressao;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.DV.sk210flutter.R;
import com.example.DV.sk210flutter.DeviceServiceManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.Align;
import com.topwise.cloudpos.aidl.printer.ImageUnit;
import com.topwise.cloudpos.aidl.printer.PrintCuttingMode;
import com.topwise.cloudpos.aidl.printer.PrintTemplate;
import com.topwise.cloudpos.aidl.printer.TextUnit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImpressoraActivity extends AppCompatActivity {
    private Button buttonText, buttonImage, buttonFrase, buttonQr, buttonBarCode;
    private EditText editText;
    private ImpressoraActivity mContext = ImpressoraActivity.this;
    volatile boolean mInPrinter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressora);

        buttonText = findViewById(R.id.btnImprimirTexto);
        buttonImage = findViewById(R.id.btnImprimirImagem);
        buttonFrase = findViewById(R.id.btnImprimirFrase);
        buttonQr = findViewById(R.id.btnQr);
        buttonBarCode = findViewById(R.id.btnBarCode);
        editText = findViewById(R.id.editText);

        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = editText.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    if (texto.isEmpty()) {
                        Toast.makeText(mContext, "Digite um texto", Toast.LENGTH_SHORT).show();
                    } else {
                        imprimirTexto(texto);
                    }
                }
            }
        });

        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imprimirImagem(BitmapFactory.decodeResource(getResources(), R.drawable.gertec));
            }
        });
        buttonFrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imprimirFrase();
            }
        });

        buttonQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imprimirQr();
            }
        });

        buttonBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imprimirCodigoDeBarras();
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


    public Bitmap generateQRCode(String text, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return qrBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void imprimirQr() {
        try {
            PrintTemplate template = PrintTemplate.getInstance();
            template.init(mContext);
            template.clear();
            template.add(new TextUnit("\n\n"));
            Bitmap qrBitmap = generateQRCode("http://www.skywings.com.br/", 350, 350);
            template.add(new ImageUnit(qrBitmap, 350, 350)); // Adicionar o QR Code ao template

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

    public Bitmap generateBarcode(String data, BarcodeFormat format, int width, int height) {
        BitMatrix bitMatrix = new Code128Writer().encode(data, format, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    public void imprimirCodigoDeBarras() {
        try {
            PrintTemplate template = PrintTemplate.getInstance();
            template.init(mContext);
            template.clear();
            template.add(new TextUnit("\n\n"));
            template.add(new TextUnit("\n\n"));

            // Gerar o código de barras
            String codigoDeBarras = "123456789012";
            BarcodeFormat formatoCodigoDeBarras = BarcodeFormat.CODE_128;
            Bitmap codigoDeBarrasBitmap = generateBarcode(codigoDeBarras, formatoCodigoDeBarras, 400, 100);
            template.add(new ImageUnit(codigoDeBarrasBitmap, 400, 100)); // Adicionar o código de barras ao template

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
}