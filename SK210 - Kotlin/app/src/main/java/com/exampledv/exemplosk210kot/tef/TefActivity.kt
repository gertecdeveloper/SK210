package com.exampledv.exemplosk210kot.tef

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.RemoteException
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.exampledv.exemplosk210kot.R
import com.exampledv.exemplosk210kot.util.DeviceServiceManager
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener
import com.topwise.cloudpos.aidl.printer.Align
import com.topwise.cloudpos.aidl.printer.PrintCuttingMode
import com.topwise.cloudpos.aidl.printer.PrintTemplate
import com.topwise.cloudpos.aidl.printer.TextUnit
import org.json.JSONException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class TefActivity : AppCompatActivity() {

    companion object {
        var acao = "venda"
        private const val VENDA = 1
        private const val CANCELAMENTO = 2
        private const val FUNCOES = 3
        private const val REIMPRESSAO = 4
    }

    private val r = Random()
    private val dt = Date()
    private val op = r.nextInt(99999).toString()
    private val currentDateTimeString = DateFormat.getDateInstance().format(Date())
    private val currentDateTimeStringT = "${dt.hours}${dt.minutes}${dt.seconds}"

    private lateinit var txtValorOperacao: EditText
    private lateinit var txtIpServidor: EditText
    private lateinit var txtParcelas: EditText

    private lateinit var btnEnviarTransacao: Button
    private lateinit var btnCancelarTransacao: Button
    private lateinit var btnFuncoes: Button
    private lateinit var btnReimpressao: Button

    private lateinit var rbTodos: RadioButton
    private lateinit var rbCredito: RadioButton
    private lateinit var rbDebito: RadioButton
    private lateinit var rbLoja: RadioButton
    private lateinit var rbAdm: RadioButton

    private lateinit var txtCupom: TextView

    private lateinit var mSitef: MSitef
    private val mContext: Context = this
    @Volatile
    private var mInPrinter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tef)


        // Inicializa todos os EditText
        txtCupom = findViewById(R.id.txtRetorno)
        txtValorOperacao = findViewById(R.id.txtValorOperacao)
        txtIpServidor = findViewById(R.id.txtIpServidor)
        txtParcelas = findViewById(R.id.txtParcelas)


        // Inicializa todos os Buttons
        btnEnviarTransacao = findViewById(R.id.btnEnviarTransacao)
        btnCancelarTransacao = findViewById(R.id.btnCancelarTransacao)
        btnFuncoes = findViewById(R.id.btnFuncoes)
        btnReimpressao = findViewById(R.id.btnReimpressao)


        // Inicializa todos os RadioButtons
        rbCredito = findViewById(R.id.rbCredito)
        rbDebito = findViewById(R.id.rbDebito)
        rbTodos = findViewById(R.id.rbTodos)
        rbLoja = findViewById(R.id.radioLoja)
        rbAdm = findViewById(R.id.radioAdm)

        maskTextEdits()
        txtValorOperacao.hint = ""
        txtValorOperacao.setText("1")
        txtIpServidor.setText("192.168.13.107")
        rbAdm.isChecked = true
        txtIpServidor.inputType = InputType.TYPE_CLASS_NUMBER
        txtIpServidor.keyListener = DigitsKeyListener.getInstance("0123456789.")
        rbDebito.setOnCheckedChangeListener { arg0, arg1 ->
            if (rbTodos.isChecked || rbDebito.isChecked) {
                txtParcelas.setText("1")
                txtParcelas.isEnabled = false
            } else {
                txtParcelas.isEnabled = true
            }
        }

        rbTodos.setOnCheckedChangeListener { arg0, arg1 ->
            if (rbTodos.isChecked || rbDebito.isChecked) {
                txtParcelas.setText("1")
                txtParcelas.isEnabled = false
            } else {
                txtParcelas.isEnabled = true
            }
        }

        btnEnviarTransacao.setOnClickListener {
            acao = "venda"
            if (Mask.unmask(txtValorOperacao.text.toString()).equals("000")) {
                dialogoErro("O valor de venda digitado deve ser maior que 0")
            } else if (validaIp(txtIpServidor.text.toString()) == false) {
                dialogoErro("Digite um IP válido")
            } else {
                if (rbCredito.isChecked && (txtParcelas.text.toString()
                        .isEmpty() || txtParcelas.text.toString() == "0")
                ) {
                    dialogoErro("É necessário colocar o número de parcelas desejadas (obs.: Opção de compra por crédito marcada)")
                } else {
                    executaTEF(VENDA)
                }
            }
        }

        btnCancelarTransacao.setOnClickListener {
            acao = "cancelamento"
            if (Mask.unmask(txtValorOperacao.text.toString()).equals("000")) {
                dialogoErro("O valor de venda digitado deve ser maior que 0")
            } else if (validaIp(txtIpServidor.text.toString()) == false) {
                dialogoErro("Digite um IP válido")
            } else {
                executaTEF(CANCELAMENTO)
            }
        }

        btnFuncoes.setOnClickListener {
            acao = "funcoes"
            if (Mask.unmask(txtValorOperacao.text.toString()).equals("000")) {
                dialogoErro("O valor de venda digitado deve ser maior que 0")
            } else if (validaIp(txtIpServidor.text.toString()) == false) {
                dialogoErro("Digite um IP válido")
            } else {
                executaTEF(FUNCOES)
            }
        }

        btnReimpressao.setOnClickListener {
            acao = "reimpressao"
            if (Mask.unmask(txtValorOperacao.text.toString()).equals("000")) {
                dialogoErro("O valor de venda digitado deve ser maior que 0")
            } else if (validaIp(txtIpServidor.text.toString()) == false) {
                dialogoErro("Digite um IP válido")
            } else {
                executaTEF(REIMPRESSAO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (requestCode == MSitef.REQ_CODE_MSITEF && data != null) {
                val retornoSitef = mSitef.traduzRetornoMSitef(data)
                if (resultCode == RESULT_OK) {
                    if (retornoSitef.codResp.equals("0")) {
                        var impressao: String? = ""
                        // Verifica se tem algo pra imprimir
                        if (!retornoSitef.textoImpressoCliente().isEmpty()) {
                            impressao += retornoSitef.textoImpressoCliente()
                        }
                        if (!retornoSitef.textoImpressoEstabelecimento().isEmpty()) {
                            impressao += "\n\n-----------------------------     \n"
                            impressao += retornoSitef.textoImpressoEstabelecimento()
                        }

                        txtCupom.text = impressao
                        val template = PrintTemplate.getInstance()
                        template.init(mContext)
                        template.clear()
                        template.add(
                            TextUnit(
                                impressao,
                                60,
                                Align.CENTER
                            )
                        ) // Adiciona o texto ao template
                        DeviceServiceManager.getInstance().printManager.addRuiImage(
                            rotateBitmap(
                                template.printBitmap,
                                180f
                            ), 0
                        )
                        DeviceServiceManager.getInstance().printManager.printRuiQueue(object :
                            AidlPrinterListener.Stub() {
                            @Throws(RemoteException::class)
                            override fun onError(i: Int) {
                                mInPrinter = false
                            }

                            @Throws(RemoteException::class)
                            override fun onPrintFinish() {
                                DeviceServiceManager.getInstance().printManager.cuttingPaper(
                                    PrintCuttingMode.CUTTING_MODE_HALT
                                )
                                mInPrinter = false
                            }
                        })
                    }
                    // Verifica se ocorreu um erro durante venda ou cancelamento
                    if (acao == "venda" || acao == "cancelamento") {
                        if (retornoSitef.codResp.isEmpty() || !retornoSitef.codResp.equals("0") || retornoSitef.codResp == null) {
                            mSitef.dialodTransacaoNegadaMsitef(retornoSitef)
                        } else {
                            mSitef.dialodTransacaoAprovadaMsitef(retornoSitef)
                        }
                    }
                } else {
                    // ocorreu um erro
                    if (acao == "venda" || acao == "cancelamento") {
                        mSitef.dialodTransacaoNegadaMsitef(retornoSitef)
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            throw RuntimeException(e)
        }
    }

    fun validaIp(ipserver: String?): Boolean {
        val p = Pattern.compile(
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
        )
        val m = p.matcher(ipserver)
        val b = m.matches()
        return b
    }

    private fun maskTextEdits() {
        txtValorOperacao.addTextChangedListener(MoneyTextWatcher(txtValorOperacao))
    }

    private fun executaTEF(operacao: Int) {
        val bundle = Bundle()

        bundle.putCharSequence("empresaSitef", "00000000")
        bundle.putCharSequence(
            "enderecoSitef",
            txtIpServidor.text.toString().replace("\\s+".toRegex(), "")
        )
        bundle.putCharSequence("operador", "0001")
        bundle.putCharSequence("data", "20200324")
        bundle.putCharSequence("hora", "130358")
        bundle.putCharSequence("numeroCupom", op)
        bundle.putCharSequence("comExterna", "0") // 0 – Sem (apenas para SiTef dedicado)
        bundle.putCharSequence("CNPJ_CPF", "03654119000176") // CNPJ ou CPF do estabelecimento.

        when (operacao) {
            VENDA -> {
                bundle.putCharSequence("valor", "001") // Valor da operação

                if (rbCredito.isChecked) {
                    bundle.putCharSequence("valor", "001") // Valor da operação
                    bundle.putCharSequence(
                        "modalidade",
                        "3"
                    ) // Funcionalidade da CliSiTef que deseja executar

                    if (txtParcelas.text.toString() == "0" || txtParcelas.text.toString() == "1") {
                        bundle.putCharSequence(
                            "transacoesHabilitadas",
                            "26"
                        ) // Opções de pagamento que serão habilitadas
                    } else if (rbLoja.isChecked) {
                        bundle.putCharSequence(
                            "transacoesHabilitadas",
                            "26"
                        ) // Opções de pagamento que serão habilitadas
                    } else if (rbAdm.isChecked) {
                        bundle.putCharSequence(
                            "transacoesHabilitadas",
                            "26"
                        ) // Opções de pagamento que serão habilitadas
                    }
                    bundle.putCharSequence(
                        "numParcelas",
                        txtParcelas.text.toString()
                    ) // Número de parcelas
                }

                if (rbDebito.isChecked) {
                    bundle.putCharSequence(
                        "modalidade",
                        "2"
                    ) // Funcionalidade da CliSiTef que deseja executar
                    bundle.putCharSequence(
                        "transacoesHabilitadas",
                        "16"
                    ) // Funcionalidade da CliSiTef que deseja executar
                }

                if (rbTodos.isChecked) {
                    bundle.putCharSequence(
                        "modalidade",
                        "0"
                    ) // Funcionalidade da CliSiTef que deseja executar
                }
            }

            CANCELAMENTO -> bundle.putCharSequence(
                "modalidade",
                "200"
            ) // Funcionalidade da CliSiTef que deseja executar
            FUNCOES -> bundle.putCharSequence(
                "modalidade",
                "110"
            ) // Funcionalidade da CliSiTef que deseja executar
            REIMPRESSAO -> bundle.putCharSequence(
                "modalidade",
                "114"
            ) // Funcionalidade da CliSiTef que deseja executar
        }
        bundle.putCharSequence("isDoubleValidation", "0")
        bundle.putCharSequence("caminhoCertificadoCA", "ca_cert_perm")
        bundle.putCharSequence(
            "cnpj_automacao",
            "03654119000176"
        ) // CNPJ da empresa que desenvolveu a automação comercial.

        mSitef = MSitef(this)
        try {
            mSitef.ExecutaTEF(bundle)
        } catch (e: Exception) {
            txtCupom.text = e.message
        }
    }

    fun dialogoErro(msg: String?) {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Erro ao executar função.")
        alertDialog.setMessage(msg)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "OK"
        ) { dialogInterface, i ->
            // Não existe nenhuma ação
        }
        alertDialog.show()
    }

    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(degrees)
        val rotatedBitmap =
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
        original.recycle()
        return rotatedBitmap
    }

    private fun getCurTime(): String {
        val date = Date(System.currentTimeMillis())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val time = format.format(date)
        return time
    }
}
