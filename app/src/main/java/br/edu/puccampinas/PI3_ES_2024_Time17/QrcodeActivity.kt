package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Button
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.Hashtable


fun gerarQRCode(texto: String, largura: Int, altura: Int): Bitmap? {
    val hints: Hashtable<EncodeHintType, Any> = Hashtable()
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
    try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(texto, BarcodeFormat.QR_CODE, largura, altura, hints)
        val bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.RGB_565)
        for (x in 0 until largura) {
            for (y in 0 until altura) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
    }
    return null
}

class QrcodeActivity : AppCompatActivity() {

    private lateinit var btnVoltar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        btnVoltar = findViewById(R.id.btnVoltar)

        val qrCode = gerarQRCode("Texto que você quer codificar", 512, 512)
        if (qrCode != null) {
            val imageViewQRCode = findViewById<ImageView>(R.id.QRcode)
            imageViewQRCode.setImageBitmap(qrCode)
        }

        btnVoltar.setOnClickListener {
            val iMenu = Intent(this,MenuActivity::class.java)
            startActivity(iMenu)
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
