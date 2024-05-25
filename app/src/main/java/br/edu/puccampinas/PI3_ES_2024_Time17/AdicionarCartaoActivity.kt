package br.edu.puccampinas.PI3_ES_2024_Time17

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class AdicionarCartaoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var edtNumero: EditText
    private lateinit var edtValidade: TextInputEditText
    private lateinit var edtCvv: EditText
    private lateinit var btnAdicionarCartao: Button
    private lateinit var btnVoltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_cartao)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        edtNumero = findViewById(R.id.tvNumerodoCartão)
        edtValidade = findViewById(R.id.edtDataDeValidade)
        edtCvv = findViewById(R.id.tvCvv)
        btnVoltar = findViewById(R.id.btnVoltar)
        btnAdicionarCartao = findViewById(R.id.btnAdicionarCartao)

        edtValidade.setOnClickListener {
            exibirDatePicker()
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        btnAdicionarCartao.setOnClickListener {
            val numero = edtNumero.text.toString()
            val validade = edtValidade.text.toString()
            val cvv = edtCvv.text.toString()

            val user = auth.currentUser
            user?.uid?.let { uid ->
                val db = FirebaseFirestore.getInstance()
                val adicionarCartao = hashMapOf(
                    "Uid" to uid,
                    "Numero" to numero,
                    "Validade" to validade,
                    "Cvv" to cvv
                )

                db.collection("Cartao").document(uid)
                    .set(adicionarCartao)
                    .addOnSuccessListener {
                        Log.d(
                            ContentValues.TAG,
                            "Dados do cartão adicionados ao Firestore com sucesso"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Erro ao adicionar dados do cartão ao Firestore", e)
                    }
            }
        }
    }
    private fun exibirDatePicker() {
        val calendar = Calendar.getInstance()
        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                edtValidade.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            },
            ano,
            mes,
            dia
        )
        datePickerDialog.show()
    }
}