package com.example.DV.sk210flutter_easylayer.impressao;

import static android.widget.Toast.makeText;
import static br.com.gertec.easylayer.printer.Alignment.CENTER;
import static br.com.gertec.easylayer.printer.Alignment.LEFT;
import static br.com.gertec.easylayer.printer.Alignment.RIGHT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.DV.sk210flutter_easylayer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.com.gertec.easylayer.printer.Alignment;
import br.com.gertec.easylayer.printer.BarcodeFormat;
import br.com.gertec.easylayer.printer.BarcodeType;
import br.com.gertec.easylayer.printer.CutType;
import br.com.gertec.easylayer.printer.OrientationType;
import br.com.gertec.easylayer.printer.PrintConfig;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.easylayer.printer.PrinterException;
import br.com.gertec.easylayer.printer.PrinterUtils;
import br.com.gertec.easylayer.printer.Receipt;
import br.com.gertec.easylayer.printer.TableFormat;
import br.com.gertec.easylayer.printer.TextFormat;

public class ImpressoraActivity extends AppCompatActivity implements Printer.Listener{

    private EditText edTxt;
    private int ret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_impressora);

        //Inicialização da classe Printer
        Printer printer = Printer.getInstance( this, this);

        //Contexto
        Context context = this.getApplicationContext();

        //Inicialização da classe PrinterUtils
        PrinterUtils printerUtils = printer.getPrinterUtils();

        //Inicialização da classe PrintConfig
        PrintConfig printConfig = new PrintConfig();

        //Definição de alinhamento
        printConfig.setAlignment(CENTER);

        edTxt = findViewById(R.id.edt_text);

        //Impressão do texto
        Button btn_print_text = findViewById(R.id.btn_print_text);
        btn_print_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    String text = null;
                    if (edTxt.getText().toString().isEmpty()) {
                        makeText(getApplicationContext(), "Digite um texto", Toast.LENGTH_SHORT).show();

                    } else {
                        text = edTxt.getText().toString();

                        //Formato 1
                        TextFormat textFormat = new TextFormat();
                        textFormat.setBold(true);
                        textFormat.setUnderscore(true);
                        textFormat.setFontSize(20);
                        textFormat.setLineSpacing(6);
                        textFormat.setAlignment(CENTER);

                        //Impressão formato 1
                        printer.printText(textFormat, text);

                        //Formato 2
                        TextFormat textFormat2 = new TextFormat();
                        textFormat2.setBold(false);
                        textFormat2.setUnderscore(false);
                        textFormat2.setFontSize(25);
                        textFormat2.setLineSpacing(6);
                        textFormat2.setAlignment(LEFT);

                        //Impressão formato 2
                        printer.printText(textFormat2, text);

                        //Formato 3
                        TextFormat textFormat3 = new TextFormat();
                        textFormat3.setBold(false);
                        textFormat3.setUnderscore(true);
                        textFormat3.setFontSize(30);
                        textFormat3.setLineSpacing(6);
                        textFormat3.setAlignment(RIGHT);

                        //Impressão formato 3
                        printer.printText(textFormat3, text);
                        printer.scrollPaper(3);

                        //Toast de impressão
                        makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    //Mensagem de erro
                    Log.i(String.valueOf(getApplicationContext()), "onClick: Erro ao imprimir");
                }
            }
        });

        //Botão de imprimir cupom
        Button btn_print_cupom = findViewById(R.id.btn_print_cupom);
        btn_print_cupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Formatação do cupom
                    ArrayList<String> listItens = new ArrayList<>();
                    listItens.add("001 00345 1 cx (1,25) - Pasta de alho");
                    listItens.add("002 00345 1 cx (5,00) - Bife de figado");
                    listItens.add("003 00345 1 cx (1,25) - Creme de barbear");
                    listItens.add("004 00345 1 cx (1,25) - Creme Hidratante");
                    listItens.add("005 00345 1 cx (1,25) - Bolacha negresco");
                    listItens.add("006 00345 1 cx (1,25) - Peito de frango");

                    ArrayList<String> listItensValue = new ArrayList<>();
                    listItensValue.add("7,00");
                    listItensValue.add("15,00");
                    listItensValue.add("15,00");
                    listItensValue.add("10,00");
                    listItensValue.add("5,00");
                    listItensValue.add("18,00");

                    Receipt cupom = new Receipt();
                    cupom.setListItens(listItens);
                    cupom.setListValueItens(listItensValue);

                    //Imprimir cupom
                    printer.printXml(cupom);
                    printer.scrollPaper(3);

                    //Toast de impressão
                    makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Botão de imprimir imagem
        Button btn_print_imagem = findViewById(R.id.btn_print_imagem);
        btn_print_imagem.setOnClickListener(v -> {
            try {
                Bitmap coloredBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gertec);

                if (coloredBitmap != null) {
                    Bitmap monochromaticBitmap = printerUtils.toMonochromatic(coloredBitmap, 0.5);
//                showReturn(ret);
//
//                    //Dimensionamento da imagem
//                    printConfig.setWidth(600);
//                    printConfig.setHeight(600);

//                    //Alinhamento
//                    printConfig.setAlignment(Alignment.CENTER);

                    //Impressão
                    printer.printImageAutoResize(monochromaticBitmap);

                    printer.scrollPaper(3);

                    //Toast de impressão
                    makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

                }
            } catch (PrinterException e) {
                throw new RuntimeException(e);
            }
        });


        //Botão de imprimir HTML
        Button btn_print_html = findViewById(R.id.btn_print_html);
        btn_print_html.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Formatação do HTML
                final String html = "<!DOCTYPE html>" +
                        "<html>" +
                        "   <head>" +
                        "      <meta charset='UTF-8'>" +
                        "      <style type='text/css'>" +
                        "         h6 { font-size: 50%; }" +
                        "         h5 { font-size: 80%; }" +
                        "         h4 { font-size: 100%; }" +
                        "         h3 { font-size: 150%; }" +
                        "         h2 { font-size: 200%; }" +
                        "         h1 { font-size: 250%; }" +
                        "         right { float:right; }" +
                        "         left { float:left; }" +
                        "         hr { border-top: 2px solid black; }" +
                        "         table { width: 384px; }" +
                        "         body { font-size: 11px; font-family: sans serif}" +
                        "      </style>" +
                        "   </head>" +
                        "   <body>" +
                        "      <b>NOME DO ESTABELECIMENTO</b>" +
                        "      <br>Endereço, 101 - Bairro</br>" +
                        "      São Paulo - SP - CEP 13.001-000</br>" +
                        "      <b>CNPJ: 11.222.333/0001-44</b></br>" +
                        "      IE: 000.000.000.000</br>" +
                        "      M: 0.000.000-0</br>" +
                        "      <hr>" +
                        "      </hr>" +
                        "      <center><b>CUPOM FISCAL ELETRONICO - SAT</b></center>" +
                        "      </br>" +
                        "      <table>" +
                        "         <tr>" +
                        "            <th>ITEM</th>" +
                        "            <th>CÓDIGO</th>" +
                        "            <th>DESCRIÇÃO</th>" +
                        "         </tr>" +
                        "         <tr>" +
                        "            <th>QTD</th>" +
                        "            <th>UN.</th>" +
                        "            <th>VL. UNIT (R$) ST</th>" +
                        "            <th>VL. ITEM(R$)</th>" +
                        "         </tr>" +
                        "      </table>" +
                        "      </br>" +
                        "      <table>" +
                        "         <tr>" +
                        "            <td>001</td>" +
                        "            <td>1011213</td>" +
                        "            <td>NOME DO PRODUTO</td>" +
                        "         </tr>" +
                        "         <tr>" +
                        "            <td>001</td>" +
                        "            <td>000UN</td>" +
                        "            <td>12,00 F1 A</td>" +
                        "            <td>25,00</td>" +
                        "         </tr>" +
                        "      </table>" +
                        "      </br>" +
                        "      <b>" +
                        "         <hr>" +
                        "         </hr>" +
                        "      </b>" +
                        "      <b>" +
                        "         TOTAL" +
                        "         <right>R$25,00</right>" +
                        "      </b>" +
                        "      </br>" +
                        "      <hr>" +
                        "      </hr>" +
                        "      <div style='font-size: 8px; font-family: courier;'>" +
                        "         Tributos Totais Incidentes (Lei Federal 12.741/2012)" +
                        "         <right>R$4,00</right>" +
                        "      </div>" +
                        "      <hr>" +
                        "      </hr>" +
                        "      <table>" +
                        "         <tr>" +
                        "            <td>N. 0000000139</td>" +
                        "            <td>Série 1</td>" +
                        "            <td>01/01/2016 15:00:00</td>" +
                        "         </tr>" +
                        "      </table>" +
                        "      </br>" +
                        "      <center>Consulte pela chave de acesso em</br>" +
                        "         http://www.nfb.fazenda.sp.gov.br</br>CHAVE DE ACESSO" +
                        "      </center>" +
                        "      </br>" +
                        "   </body>" +
                        "</html>";
                try {
                    //Imprimir HTML
                    printer.printHtml(getApplicationContext(), html);
                    printer.scrollPaper(3);

                    //Toast de impressão
                    makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

                } catch (PrinterException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Botão de imprimir código de barras
        Button btn_barcode = findViewById(R.id.btn_print_barcode);
        btn_barcode.setOnClickListener(view -> {
            try {
                //Implementação
                BarcodeFormat format = new BarcodeFormat(BarcodeType.AZTEC); //AZTEC
                BarcodeFormat format5 = new BarcodeFormat(BarcodeType.DATA_MATRIX); //DATA MATRIX
                BarcodeFormat format12 = new BarcodeFormat(BarcodeType.PDF_417); //PDF 417
                BarcodeFormat format1 = new BarcodeFormat(BarcodeType.CODABAR); //CODA BAR
                BarcodeFormat format2 = new BarcodeFormat(BarcodeType.CODE_39); //CODE 39
                BarcodeFormat format4 = new BarcodeFormat(BarcodeType.CODE_128); //CODE 168
                BarcodeFormat format6 = new BarcodeFormat(BarcodeType.EAN_8); //EAN 8
                BarcodeFormat format7 = new BarcodeFormat(BarcodeType.EAN_13); //EAN 13
                BarcodeFormat format8 = new BarcodeFormat(BarcodeType.ITF); //ITF
                BarcodeFormat format9 = new BarcodeFormat(BarcodeType.UPC_A); //UPC A
                BarcodeFormat format10 = new BarcodeFormat(BarcodeType.QR_CODE); //QR CODE

                //Formatação
                int scrollLines = 1;
                TextFormat textFormat = new TextFormat();
                textFormat.setAlignment(Alignment.CENTER);
                textFormat.setFontSize(30);

                //Tamanho
                BarcodeFormat.Size size = BarcodeFormat.Size.FULL_PAPER;

                //AZTEC"
                printer.printText(textFormat,"AZTEC");
                format.setSize(size);
                printer.printBarcode(format, "12345");
                printer.scrollPaper(scrollLines);

                //DATA_MATRIX
                printer.printText(textFormat,"DATA_MATRIX");
                format5.setSize(size);
                printer.printBarcode(format5, "CODEGS1DATAMATRIX22X22");
                printer.scrollPaper(scrollLines);

                //PDF_417
                printer.printText(textFormat,"PDF_417");
                format12.setSize(size);
                printer.printBarcode(format12, "12345");
                printer.scrollPaper(scrollLines);

                //QRCODE
                printer.printText(textFormat,"QRCODE");
                format10.setSize(size);
                printer.printBarcode(format10, "https://www.gertec.com.br");

                //CODABAR
                printer.printText(textFormat,"CODABAR");
                printer.printBarcode(format1, "12345");
                printer.scrollPaper(scrollLines);

                //CODE_39
                printer.printText(textFormat,"CODE_39");
                printer.printBarcode(format2, "12345");
                printer.scrollPaper(scrollLines);

                //CODE_128
                printer.printText(textFormat,"CODE_128");
                printer.printBarcode(format4, "12345");
                printer.scrollPaper(scrollLines);

                //EAN_8
                printer.printText(textFormat,"EAN_8");
                printer.printBarcode(format6, "12345678");
                printer.scrollPaper(scrollLines);

                //EAN_13
                printer.printText(textFormat,"EAN_13");
                printer.printBarcode(format7, "0123456789012");
                printer.scrollPaper(scrollLines);

                //ITF
                printer.printText(textFormat,"ITF");
                printer.printBarcode(format8, "47257091688216");
                printer.scrollPaper(scrollLines);

                //UPC_A
                printer.printText(textFormat,"UPC_A");
                printer.printBarcode(format9, "63938200039");

                printer.scrollPaper(scrollLines+2);

                //Toast de impressão
                makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        //Botão de imprimir tabelas
        Button btn_tabelas = findViewById(R.id.btn_print_tabelas);
        btn_tabelas.setOnClickListener(view -> {
            try {
                //Formatação
                String[] headerArray = {"Números"};
                List<String> header = Arrays.asList(headerArray);
                String[][] rowsArray =
                        {
                                {"11", "12", "13", "14"},
                                {"21", "22", "23", "24"},
                                {"31", "32", "33", "34"}
                        };
                List<List<String>> rows = new ArrayList<>();
                for(int i = 0; i < rowsArray.length; i++) {
                    rows.add(Arrays.asList(rowsArray[0])); }

                //Impressão
                TableFormat tableFormat = new TableFormat();
                tableFormat.setFontSize(30);
                tableFormat.setHeaderAlignment(CENTER);
                tableFormat.setRowAlignment(LEFT);

                try {
                    //Impressão
                    printer.printTable(context, tableFormat, header, rows);

                    printer.scrollPaper(3);

                    //Toast de impressão
                    makeText(getApplicationContext(), "Imprimindo", Toast.LENGTH_SHORT).show();

                } catch (PrinterException e) {
                    throw new RuntimeException(e);
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        });

        //Botão de soltar papel
        Button btn_print_scroll = findViewById(R.id.btn_print_scroll);
        btn_print_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printer.scrollPaper(3);

                //Toast de impressão
                makeText(getApplicationContext(), "Papel liberado com sucesso", Toast.LENGTH_SHORT).show();
            }
        });

        //Botão de cortar papel
        Button btn_paper_cut = findViewById(R.id.btn_print_cut);
        btn_paper_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Corte total
                printer.cutPaper(CutType.PAPER_FULL_CUT);

                //Toast de corte
                makeText(getApplicationContext(), "Cortado com sucesso", Toast.LENGTH_SHORT).show();

            }
        });

        //Botão de inverter impressao
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch swich = findViewById(R.id.switchRotation);
        swich.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                printer.setPrinterOrientation(OrientationType.INVERTED);
            }else {
                printer.setPrinterOrientation(OrientationType.DEFAULT);
            }
        });
    }

    //Métododo para mensagem de erro da impressora

    public void onPrinterError(PrinterError printerError) {
        String message = String.format(Locale.US, "Id: [%d] | Cause: [\"%s\"]",
                printerError.getRequestId(), printerError.getCause());
        Log.d("[onPrinterError]", message);
    }

    //Métododo para mensagem de impressão com sucesso

    public void onPrinterSuccessful(int printerRequestId) {
        String message = String.format(Locale.US, "Id: [%d]", printerRequestId);
        Log.d("[onPrinterSuccessful]", message);
    }

    private void showReturn(int ret) {
        String message = String.format(Locale.US, "Return: [%d]", ret);
        Log.d("[Return]", message);
    }

    }
