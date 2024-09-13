package com.example.easylayer_217_sk210.tef;

import static br.com.gertec.easylayer.printer.Alignment.CENTER;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easylayer_217_sk210.R;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.gertec.easylayer.printer.CutType;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.easylayer.printer.PrinterException;
import br.com.gertec.easylayer.printer.TextFormat;

public class Tef extends AppCompatActivity  implements Printer.Listener{


    public static String acao = "venda";
    private AidlPrinter mPrinter; // Declaração do AidlPrinter

    // TODO IMPRESSAO GLASS private GertecPrinter gertecPrinter;
    // TODO IMPRESSAO GLASS private ConfigPrint configPrint = new ConfigPrint();
    /// Difines operação
    private Random r = new Random();
    private Date dt = new Date();
    private String op = String.valueOf(r.nextInt(99999));
    private String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
    private String currentDateTimeStringT = String.valueOf(dt.getHours()) + String.valueOf(dt.getMinutes()) + String.valueOf(dt.getSeconds());
    /// Fim Defines Operação

    private final static int VENDA = 1;
    private final static int CANCELAMENTO = 2;
    private final static int FUNCOES = 3;
    private final static int REIMPRESSAO = 4;

    private EditText txtValorOperacao;
    private EditText txtIpServidor;
    private EditText txtParcelas;

    private Button btnEnviarTransacao;
    private Button btnCancelarTransacao;
    private Button btnFuncoes;
    private Button btnReimpressao;

    private RadioButton rbTodos;
    private RadioButton rbCredito;
    private RadioButton rbDebito;
    private RadioButton rbLoja;
    private RadioButton rbAdm;

    private TextView txtCupom;

    MSitef mSitef;
    private Context mContext = Tef.this;
    volatile boolean mInPrinter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tef);

        //Inicialização da classe Printer
        Printer printer = Printer.getInstance( this, this);

        //Contexto
        Context context = this.getApplicationContext();


        // Inicializa todos os EditText
        txtCupom = findViewById(R.id.txtRetorno);
        txtValorOperacao = findViewById(R.id.txtValorOperacao);
        txtIpServidor = findViewById(R.id.txtIpServidor);
        txtParcelas = findViewById(R.id.txtParcelas);

        // Inicializa todos os Buttons
        btnEnviarTransacao = findViewById(R.id.btnEnviarTransacao);
        btnCancelarTransacao = findViewById(R.id.btnCancelarTransacao);
        btnFuncoes = findViewById(R.id.btnFuncoes);
        btnReimpressao = findViewById(R.id.btnReimpressao);

        // Inicializa todos os RadioButtons
        rbCredito = findViewById(R.id.rbCredito);
        rbDebito = findViewById(R.id.rbDebito);
        rbTodos = findViewById(R.id.rbTodos);
        rbLoja = findViewById(R.id.radioLoja);
        rbAdm = findViewById(R.id.radioAdm);

        maskTextEdits();
        txtValorOperacao.setHint("");
        txtValorOperacao.setText("1");
        txtIpServidor.setText("192.168.248.107");
        rbAdm.setChecked(true);
        txtIpServidor.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtIpServidor.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        rbDebito.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (rbTodos.isChecked() || rbDebito.isChecked()) {
                    txtParcelas.setText("1");
                    txtParcelas.setEnabled(false);
                } else {
                    txtParcelas.setEnabled(true);
                }
            }
        });

        rbTodos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (rbTodos.isChecked() || rbDebito.isChecked()) {
                    txtParcelas.setText("1");
                    txtParcelas.setEnabled(false);
                } else {
                    txtParcelas.setEnabled(true);
                }
            }
        });

        btnEnviarTransacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "venda";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    if (rbCredito.isChecked() && (txtParcelas.getText().toString().isEmpty() || txtParcelas.getText().toString().equals("0"))) {
                        dialogoErro("É necessário colocar o número de parcelas desejadas (obs.: Opção de compra por crédito marcada)");
                    } else {
                        executaTEF(VENDA);
                    }
                }
            }
        });

        btnCancelarTransacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "cancelamento";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    executaTEF(CANCELAMENTO);
                }
            }
        });

        btnFuncoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "funcoes";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    executaTEF(FUNCOES);
                }
            }
        });

        btnReimpressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acao = "reimpressao";
                if (Mask.unmask(txtValorOperacao.getText().toString()).equals("000")) {
                    dialogoErro("O valor de venda digitado deve ser maior que 0");
                } else if (validaIp(txtIpServidor.getText().toString()) == false) {
                    dialogoErro("Digite um IP válido");
                } else {
                    executaTEF(REIMPRESSAO);
                }
            }
        });
    }


    //------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == MSitef.REQ_CODE_MSITEF && data != null) {
                RetornoMSitef retornoSitef = mSitef.traduzRetornoMSitef(data);
                if (resultCode == RESULT_OK && retornoSitef != null) {
                    if ("0".equals(retornoSitef.getCodResp())) {
                        String impressao = "";
                        // Verifica se tem algo pra imprimir
                        if (!retornoSitef.textoImpressoCliente().isEmpty()) {
                            impressao += retornoSitef.textoImpressoCliente();
                        }
                        if (!retornoSitef.textoImpressoEstabelecimento().isEmpty()) {
                            impressao += "\n\n-----------------------------\n";
                            impressao += retornoSitef.textoImpressoEstabelecimento();
                        }

                        if (txtCupom != null) {
                            txtCupom.setText(impressao);
                        }

                        Printer printer = Printer.getInstance(this, this);
                        if (printer != null) {
                            TextFormat textFormat = new TextFormat();
                            textFormat.setUnderscore(true);
                            textFormat.setFontSize(20);
                            textFormat.setLineSpacing(6);
                            textFormat.setAlignment(CENTER);

                            // Impressão de texto
                            printer.printText(textFormat, impressao);

                            printer.scrollPaper(3);
                            //Corte total
                            printer.cutPaper(CutType.PAPER_FULL_CUT);
                        }
                    }
                    // Verifica se ocorreu um erro durante venda ou cancelamento
                    if ("venda".equals(acao) || "cancelamento".equals(acao)) {
                        if (retornoSitef.getCodResp().isEmpty() || !"0".equals(retornoSitef.getCodResp())) {
                            mSitef.dialodTransacaoNegadaMsitef(retornoSitef);
                        } else {
                            mSitef.dialodTransacaoAprovadaMsitef(retornoSitef);
                        }
                    }
                } else {
                    // Ocorreu um erro
                    if ("venda".equals(acao) || "cancelamento".equals(acao)) {
                        mSitef.dialodTransacaoNegadaMsitef(retornoSitef);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PrinterException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace(); // Captura qualquer outra exceção que possa ocorrer
        }
    }



//----------------------------------------------------------------------------------------------------------------


    boolean validaIp(String ipserver) {

        Pattern p = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        Matcher m = p.matcher(ipserver);
        boolean b = m.matches();
        return b;
    }

    private void maskTextEdits() {
        txtValorOperacao.addTextChangedListener(new MoneyTextWatcher(txtValorOperacao));
    }

    private void executaTEF(int operacao) {

        Bundle bundle = new Bundle();

        bundle.putCharSequence("empresaSitef", "00000000");
        bundle.putCharSequence("enderecoSitef", txtIpServidor.getText().toString().replaceAll("\\s+", ""));
        bundle.putCharSequence("operador", "0001");
        bundle.putCharSequence("data", "20200324");
        bundle.putCharSequence("hora", "130358");
        bundle.putCharSequence("numeroCupom", op);
        bundle.putCharSequence("comExterna", "0"); // 0 – Sem (apenas para SiTef dedicado)
        bundle.putCharSequence("CNPJ_CPF", "03654119000176"); // CNPJ ou CPF do estabelecimento.

        switch (operacao) {
            case VENDA:

                bundle.putCharSequence("valor", "001");  // Valor da operação

                if (rbCredito.isChecked()) {

                    bundle.putCharSequence("valor", "001");  // Valor da operação
                    bundle.putCharSequence("modalidade", "3");  // Funcionalidade da CliSiTef que deseja executar

                    if (txtParcelas.getText().toString().equals("0") || txtParcelas.getText().toString().equals("1")) {
                        bundle.putCharSequence("transacoesHabilitadas", "26");  // Opções de pagamento que serão habilitadas
                    } else if (rbLoja.isChecked()) {
                        bundle.putCharSequence("transacoesHabilitadas", "26");  // Opções de pagamento que serão habilitadas
                    } else if (rbAdm.isChecked()) {
                        bundle.putCharSequence("transacoesHabilitadas", "26");  // Opções de pagamento que serão habilitadas
                    }
                    bundle.putCharSequence("numParcelas", txtParcelas.getText().toString());  // Número de parcelas
                }

                if (rbDebito.isChecked()) {
                    bundle.putCharSequence("modalidade", "2");  // Funcionalidade da CliSiTef que deseja executar
                    bundle.putCharSequence("transacoesHabilitadas", "16");  // Funcionalidade da CliSiTef que deseja executar
                }

                if (rbTodos.isChecked()) {
                    bundle.putCharSequence("modalidade", "0");  // Funcionalidade da CliSiTef que deseja executar
                }
                break;

            case CANCELAMENTO:
                bundle.putCharSequence("modalidade", "200");  // Funcionalidade da CliSiTef que deseja executar
                break;
            case FUNCOES:
                bundle.putCharSequence("modalidade", "110");  // Funcionalidade da CliSiTef que deseja executar
                break;

            case REIMPRESSAO:
                bundle.putCharSequence("modalidade", "114");  // Funcionalidade da CliSiTef que deseja executar
                break;

        }

        bundle.putCharSequence("isDoubleValidation", "0");
        bundle.putCharSequence("caminhoCertificadoCA", "ca_cert_perm");
        bundle.putCharSequence("cnpj_automacao", "03654119000176");  // CNPJ da empresa que desenvolveu a automação comercial.

        mSitef = new MSitef(this);
        try {
            mSitef.ExecutaTEF(bundle);
        } catch (Exception e) {
            txtCupom.setText(e.getMessage());
        }

    }

    public void dialogoErro(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Erro ao executar função.");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Não existe nenhuma ação
            }
        });
        alertDialog.show();

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

    @Override
    public void onPrinterError(PrinterError printerError) {

    }

    @Override
    public void onPrinterSuccessful(int i) {

    }
}