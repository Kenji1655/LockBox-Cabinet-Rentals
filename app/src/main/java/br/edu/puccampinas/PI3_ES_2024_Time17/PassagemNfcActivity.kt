package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PassagemNfcActivity : AppCompatActivity() {
    private lateinit var btnVoltar: Button
    private var qrCodeValue: String? = null
    private var photoFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        btnVoltar = findViewById(R.id.btnVoltar)

        btnVoltar.setOnClickListener {
            val iMenu = Intent(this,MenuGerenteActivity::class.java)
            startActivity(iMenu)
        }

        setContentView(R.layout.activity_adicionar_nfc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        qrCodeValue = intent.getStringExtra("QRCodeValue")
        photoFileName = intent.getStringExtra("PhotoFileName")

    }
}
