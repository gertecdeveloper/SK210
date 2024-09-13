package com.exampledv.exemplosk210kot.impressora

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Bundle
import android.os.RemoteException
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exampledv.exemplosk210kot.R
import com.exampledv.exemplosk210kot.util.DeviceServiceManager
import com.exampledv.exemplosk210kot.util.Oto
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.QRCodeWriter
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener
import com.topwise.cloudpos.aidl.printer.Align
import com.topwise.cloudpos.aidl.printer.ImageUnit
import com.topwise.cloudpos.aidl.printer.PrintCuttingMode
import com.topwise.cloudpos.aidl.printer.PrintTemplate
import com.topwise.cloudpos.aidl.printer.TextUnit
import java.text.SimpleDateFormat
import java.util.Date

class Printer : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var buttonText: Button
    private lateinit var btnImprimirImagem: Button
    private lateinit var btnImprimirFrase: Button
    private lateinit var btnQr: Button
    private lateinit var btnBarCode: Button
    private val mContext: Context = this@Printer

    @Volatile
    var mInPrinter: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer)

        buttonText = findViewById(R.id.btnImprimirTexto)
        editText = findViewById(R.id.editText)
        btnImprimirImagem = findViewById(R.id.btnImprimirImagem)
        btnImprimirFrase = findViewById(R.id.btnImprimirFrase)
        btnQr = findViewById(R.id.btnQr)
        btnBarCode = findViewById(R.id.btnBarCode)

        buttonText.setOnClickListener {
            val texto = editText.text.toString()
            if (texto.isEmpty()) {
                Toast.makeText(this, "Digite um texto", Toast.LENGTH_SHORT).show()
            } else {
                imprimirTexto()
            }
        }

        btnImprimirImagem.setOnClickListener(View.OnClickListener {
            imprimirImagem(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.park
                )
            )
        })
        btnImprimirFrase.setOnClickListener(View.OnClickListener { imprimirFrase() })

        btnQr.setOnClickListener(View.OnClickListener { imprimirQr() })

        btnBarCode.setOnClickListener(View.OnClickListener { imprimirCodigoDeBarras() })
    }

    fun imprimirTexto() {
        try {
            val startTime = getCurTime()
            val texto = editText.text.toString() // Obtém o texto do EditText

            val template = PrintTemplate.getInstance().apply {
                init(mContext)
                clear()
                add(TextUnit("\n\n"))
                add(TextUnit(texto, 60, Align.CENTER))
            }

            val rotatedBitmap = rotateBitmap(template.printBitmap, 180f)
            Oto.getInstance().printManager?.apply {
                addRuiImage(rotatedBitmap, 0)
                printRuiQueue(object : AidlPrinterListener.Stub() {
                    @Throws(RemoteException::class)
                    override fun onError(i: Int) {
                        mInPrinter = false
                    }

                    @Throws(RemoteException::class)
                    override fun onPrintFinish() {
                        cuttingPaper(PrintCuttingMode.CUTTING_MODE_FULL)
                        mInPrinter = false
                    }
                })
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun imprimirImagem(bitmap: Bitmap?) {
        try {
            val template = PrintTemplate.getInstance()
            template.init(mContext)
            template.clear()
            template.add(TextUnit("\n\n"))
            template.add(TextUnit("\n\n"))
            template.add(ImageUnit(bitmap, 400, 200))
            DeviceServiceManager.getInstance().getPrintManager()
                .addRuiImage(rotateBitmap(template.printBitmap, 180f), 0)
            DeviceServiceManager.getInstance().getPrintManager()
                .printRuiQueue(object : AidlPrinterListener.Stub() {
                    @Throws(RemoteException::class)
                    override fun onError(i: Int) {
                        mInPrinter = false
                    }

                    @Throws(RemoteException::class)
                    override fun onPrintFinish() {
                        DeviceServiceManager.getInstance().getPrintManager()
                            .cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT)
                        mInPrinter = false
                    }
                })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun imprimirFrase() {
        try {
            val startTime = getCurTime()
            val texto = """
            O Smart Kiosk SK210 é a solução ideal para quem busca
            inovar no atendimento com um baixo investimento e
            facilidade de instalação
            """.trimIndent() // Texto pré-definido
            val template = PrintTemplate.getInstance()
            template.init(mContext)
            template.clear()
            template.add(TextUnit("\n\n"))
            template.add(TextUnit("\n\n"))
            template.add(TextUnit(texto, 34, Align.CENTER)) // Adiciona o texto ao template
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
                    DeviceServiceManager.getInstance().printManager.cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT)
                    mInPrinter = false
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun generateQRCode(text: String?, width: Int, height: Int): Bitmap? {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height)
            val qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrBitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            return qrBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    fun imprimirQr() {
        try {
            val template = PrintTemplate.getInstance()
            template.init(mContext)
            template.clear()
            template.add(TextUnit("\n\n"))
            val qrBitmap = generateQRCode("http://www.skywings.com.br/", 350, 350)
            template.add(ImageUnit(qrBitmap, 350, 350)) // Adicionar o QR Code ao template

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
                    DeviceServiceManager.getInstance().printManager.cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT)
                    mInPrinter = false
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun generateBarcode(data: String?, format: BarcodeFormat?, width: Int, height: Int): Bitmap {
        val bitMatrix = Code128Writer().encode(data, format, width, height)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    fun imprimirCodigoDeBarras() {
        try {
            val template = PrintTemplate.getInstance()
            template.init(mContext)
            template.clear()
            template.add(TextUnit("\n\n"))
            template.add(TextUnit("\n\n"))

            // Gerar o código de barras
            val codigoDeBarras = "123456789012"
            val formatoCodigoDeBarras = BarcodeFormat.CODE_128
            val codigoDeBarrasBitmap =
                generateBarcode(codigoDeBarras, formatoCodigoDeBarras, 400, 100)
            template.add(
                ImageUnit(
                    codigoDeBarrasBitmap,
                    400,
                    100
                )
            ) // Adicionar o código de barras ao template

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
                    DeviceServiceManager.getInstance().printManager.cuttingPaper(PrintCuttingMode.CUTTING_MODE_HALT)
                    mInPrinter = false
                }
            })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { preRotate(degrees) }
        val rotatedBitmap =
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
        original.recycle()
        return rotatedBitmap
    }

    private fun getCurTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        return format.format(Date(System.currentTimeMillis()))
    }
}