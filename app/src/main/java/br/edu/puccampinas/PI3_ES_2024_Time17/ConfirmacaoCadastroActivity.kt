package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ConfirmacaoCadastroActivity : AppCompatActivity() {

    private lateinit var btnContinuarConfirmaCadastro: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacao_cadastro)

        btnContinuarConfirmaCadastro = findViewById(R.id.btnContinuarConfirmaCadastro)

        btnContinuarConfirmaCadastro.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
