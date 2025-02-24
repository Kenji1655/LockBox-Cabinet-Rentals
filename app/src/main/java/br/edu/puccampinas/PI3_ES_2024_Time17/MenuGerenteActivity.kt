package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuGerenteActivity : AppCompatActivity() {

    private lateinit var btnDesconectar: Button
    private lateinit var btnQrcode: Button
    private lateinit var btnAbrir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_gerente)

        btnDesconectar = findViewById(R.id.btnDesconectar)
        btnQrcode = findViewById(R.id.btnLerQR)

        btnDesconectar.setOnClickListener {
            val dialogo = MenuActivity.Desconectar()
            dialogo.show(supportFragmentManager, "DesconectarDialog")
        }

        btnQrcode.setOnClickListener {
            val iQrcode = Intent(this, ScannerActivity::class.java)
            startActivity(iQrcode)
        }

        btnAbrir.setOnClickListener {
            val iAbrir = Intent(this,VerificarActivity::class.java)
            startActivity(iAbrir)
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}