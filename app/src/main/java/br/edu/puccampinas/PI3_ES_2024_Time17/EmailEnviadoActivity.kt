package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EmailEnviadoActivity : AppCompatActivity() {

    private lateinit var btnContinuarRecSenha: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_enviado)

        btnContinuarRecSenha = findViewById(R.id.btnContinuarRecSenha)

        btnContinuarRecSenha.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
