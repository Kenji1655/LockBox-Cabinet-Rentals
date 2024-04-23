package br.edu.puccampinas.PI3_ES_2024_Time17

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private fun preenchido(email: String,senha: String): Boolean{
        return email.isNotEmpty() && senha.isNotEmpty()
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var edtEmail: EditText
    private lateinit var edtSenha: EditText
    private lateinit var btnCriarConta: Button
    private lateinit var btnLogin: Button
    private lateinit var btnRecuperarSenha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        btnCriarConta = findViewById(R.id.btnCriarConta)
        btnLogin = findViewById(R.id.btnLogin)
        btnRecuperarSenha = findViewById(R.id.btnRecuperarSenha)

        edtEmail = findViewById(R.id.tvEmail)
        edtSenha = findViewById(R.id.tvSenha)

        btnCriarConta.setOnClickListener {
            val iCriarConta = Intent(this,CriarContaActivity::class.java)
            startActivity(iCriarConta)
        }
        btnRecuperarSenha.setOnClickListener {
            val iRecuperarSenha = Intent(this,RecuperarSenhaActivity::class.java)
            startActivity(iRecuperarSenha)
        }

        btnLogin.setOnClickListener {

            val email = edtEmail.text.toString()
            val senha = edtSenha.text.toString()

            if (!preenchido(email,senha)){
                Toast.makeText(
                    baseContext, "Por favor, preencha o email e senha corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Login realizado com sucesso")
                        val iMain = Intent(this, MenuActivity::class.java)
                        startActivity(iMain)
                    } else {
                        Log.w(TAG, "Erro ao logar:", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Erro ao logar: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val iMain = Intent(this, MenuActivity::class.java)
            startActivity(iMain)
        }
    }
}

