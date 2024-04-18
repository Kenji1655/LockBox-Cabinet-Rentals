package br.edu.puccampinas.lockbox

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var btnRecuperarSenha: Button
    private lateinit var btnVoltar: Button
    private lateinit var edtEmail: EditText


    private fun preenchido(email: String): Boolean{
        return email.isNotEmpty()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnRecuperarSenha = findViewById(R.id.btnRecoveryPassword)
        btnVoltar = findViewById(R.id.voltar)
        edtEmail = findViewById(R.id.tvEmail)

        btnVoltar.setOnClickListener {
            finish()
        }
        btnRecuperarSenha.setOnClickListener {
            val email = edtEmail.text.toString()

            if (!preenchido(email)){
                Toast.makeText(
                    baseContext, "Por favor, preencha o email corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Email sent.")
                    }
                }
        }
    }
}