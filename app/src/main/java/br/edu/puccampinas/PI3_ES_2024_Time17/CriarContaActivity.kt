package br.edu.puccampinas.PI3_ES_2024_Time17

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.text.InputFilter
import android.text.Spanned
import android.widget.DatePicker
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputEditText


class CriarContaActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var edtNome: EditText
    private lateinit var edtSenha: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtdataNascimento: TextInputEditText
    private lateinit var edtcpf: EditText
    private lateinit var btnSair: ImageButton
    private lateinit var btnCriarConta: Button

    class Conta(val nome: String, val email: String, val dataNascimento: String, val senha: String, val cpf: String) {
        class Validator(private val conta: Conta) {

            private fun isDataNascimentoValida(): Boolean {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formato.isLenient = false
                return try {
                    val dataNascimento = formato.parse(conta.dataNascimento)
                    val calDataNascimento = Calendar.getInstance()
                    calDataNascimento.time = dataNascimento

                    val calDataLimite = Calendar.getInstance()
                    calDataLimite.add(Calendar.YEAR, -14)

                    calDataNascimento.before(calDataLimite)
                } catch (e: Exception) {
                    false
                }
            }

            private fun preenchido(): Boolean {
                return conta.nome.isNotEmpty() &&
                        conta.email.isNotEmpty() &&
                        conta.senha.isNotEmpty() &&
                        conta.cpf.isNotEmpty()
            }

            fun validar(): Boolean {
                return preenchido() && isDataNascimentoValida()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        auth = FirebaseAuth.getInstance()

        edtNome = findViewById(R.id.tvNomeCompleto)
        edtSenha = findViewById(R.id.tvSenha)
        edtEmail = findViewById(R.id.tvEmail)
        edtdataNascimento = findViewById(R.id.edtDataNascimento)
        edtcpf = findViewById(R.id.tvcpf)
        btnCriarConta = findViewById(R.id.btnCriarConta)

        edtNome.filters = arrayOf<InputFilter>(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                if (source != null && source.toString().matches("[a-zA-Z ]+".toRegex())) {
                    return source
                }
                return ""
            }
        })

        edtdataNascimento.setOnClickListener {
            exibirDatePicker()
        }

        btnCriarConta.setOnClickListener {
            val nome = edtNome.text.toString().uppercase()
            val email = edtEmail.text.toString()
            val senha = edtSenha.text.toString()
            val dataNascimento = edtdataNascimento.text.toString()
            val cpf = edtcpf.text.toString()


            val conta = Conta(nome, email, dataNascimento, senha, cpf)
            val validator = Conta.Validator(conta)

            if (!validator.validar()) {
                Toast.makeText(
                    baseContext, "Por favor, preencha todos os campos corretamente.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Usuário criado com sucesso")
                        Toast.makeText(
                            baseContext,
                            "Usuário criado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                        val user = auth.currentUser
                        user?.uid?.let { uid ->
                            val db = FirebaseFirestore.getInstance()
                            val contaData = hashMapOf(
                                "nome" to nome,
                                "email" to email,
                                "dataNascimento" to dataNascimento,
                                "CPF" to cpf,
                                "Saldo" to 300
                            )

                            db.collection("Usuario").document(uid)
                                .set(contaData)
                                .addOnSuccessListener {
                                    Log.d(
                                        TAG,
                                        "Dados da conta adicionados ao Firestore com sucesso"
                                    )

                                    // AQUI ABRE A OUTRA ACTIVITY
                                    val intent = Intent(this, ConfirmacaoCadastroActivity::class.java)
                                    startActivity(intent)

                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Erro ao adicionar dados da conta ao Firestore", e)
                                }
                        }
                    } else {
                        erro(task.exception)
                    }
                }
        }

        btnSair = findViewById(R.id.btnSair)
        btnSair.setOnClickListener {
            finish()
        }
    }

    private fun erro(exception: Exception?) {
        Log.w(TAG, "createUserWithEmail:failure", exception)
        val message = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Senha fraca. A senha deve conter pelo menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "E-mail inválido."
            is FirebaseAuthUserCollisionException -> "E-mail já em uso. Por favor, use outro e-mail."
            else -> "Erro ao criar conta. Por favor, tente novamente mais tarde."
        }
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun exibirDatePicker() {
        val calendar = Calendar.getInstance()
        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                edtdataNascimento.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            },
            ano,
            mes,
            dia
        )
        datePickerDialog.show()
    }
}
