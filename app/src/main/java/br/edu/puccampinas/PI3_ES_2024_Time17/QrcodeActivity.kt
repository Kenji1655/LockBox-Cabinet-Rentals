package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.Hashtable

class QrcodeActivity : AppCompatActivity() {

    private lateinit var btnVoltar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance() // Inicialização da propriedade firestore

        btnVoltar = findViewById(R.id.btnVoltar)

        val user = auth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val userDocumentRef = firestore.collection("Usuario").document(userId)

            userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nome = documentSnapshot.getString("nome")
                    gerarEExibirQRCode(nome ?: "", 512, 512)
                } else {
                    Log.d(ContentValues.TAG, "Documento não existe")
                }
            }.addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "erro ao pegar o documento", exception)
            }
        }

        btnVoltar.setOnClickListener {
            val iMenu = Intent(this, MenuActivity::class.java)
            startActivity(iMenu)
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun gerarEExibirQRCode(nome: String, largura: Int, altura: Int) {
        val qrCode = gerarQRCode(nome, largura, altura)
        if (qrCode != null) {
            val imageViewQRCode = findViewById<ImageView>(R.id.QRcode)
            imageViewQRCode.setImageBitmap(qrCode)
        }
    }

    private fun gerarQRCode(texto: String, largura: Int, altura: Int): Bitmap? {
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
}
