package br.edu.puccampinas.PI3_ES_2024_Time17

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Confirmar : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Deseja finalizar a compra?")
            .setPositiveButton("Finalizar") { _, _ ->
                (activity as FinalizarPagamentoActivity).finalizarCompra()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                dismiss()
            }
        return builder.create()
    }
}

class FinalizarPagamentoActivity : AppCompatActivity() {

    private lateinit var custo: Number
    private lateinit var btnVoltar: Button
    private lateinit var btnFinalizar: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizar_pagamento)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        btnVoltar = findViewById(R.id.btnVoltar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.Pagamento1 -> {
                    custo = 30
                }
                R.id.Pagamento2 -> {
                    custo = 40
                }
                R.id.Pagamento3 -> {
                    custo = 60
                }
                R.id.Pagamento4 -> {
                    custo = 100
                }
                R.id.Pagamento5 -> {
                    custo = 180
                }
            }
        }

        btnVoltar.setOnClickListener {
            finish()
        }

        btnFinalizar.setOnClickListener {
            if (::custo.isInitialized) {
                val confirmarDialog = Confirmar()
                confirmarDialog.show(supportFragmentManager, "br.edu.puccampinas.PI3_ES_2024_Time17.Confirmar")
            } else {
                Toast.makeText(this, "Por favor, escolha uma opção de pagamento", Toast.LENGTH_SHORT).show()
            }
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun finalizarCompra() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            val usuarioId = usuarioAtual.uid
            val usuarioRef = firestore.collection("Usuario").document(usuarioId)
            usuarioRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val saldoAtual = document.getDouble("Saldo") ?: 0.0
                    val novoSaldo = saldoAtual - custo.toDouble()

                    if (novoSaldo >= 0) {
                        usuarioRef.update("Saldo", novoSaldo)
                            .addOnSuccessListener {
                                Log.d(TAG, "Saldo atualizado com sucesso. Novo saldo: $novoSaldo")
                                // Após atualizar o saldo, agora atualizamos o armário
                                atualizarArmario()
                                val iQrcode = Intent(this, QrcodeActivity::class.java)
                                startActivity(iQrcode)
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Erro ao atualizar o saldo: $e")
                            }
                    } else {
                        AlertDialog.Builder(this)
                            .setMessage("Saldo insuficiente para finalizar a compra.")
                            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            .show()
                    }
                } else {
                    Log.e(TAG, "Documento do usuário não encontrado.")
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Erro ao obter o documento do usuário: $e")
            }
        } else {
            Log.e(TAG, "Usuário não autenticado.")
        }
    }

    private fun atualizarArmario() {
        val usuarioAtual = firebaseAuth.currentUser
        val localArmario = intent.getStringExtra("local_armario")
        val idArmario = intent.getStringExtra("id_armario")

        if (usuarioAtual != null && localArmario != null && idArmario != null) {
            val usuarioId = usuarioAtual.uid
            val armarioRef = firestore.collection("Armario").document(localArmario)

            firestore.collection("Usuario").document(usuarioId)
                .get()
                .addOnSuccessListener { usuarioDocument ->
                    if (usuarioDocument.exists()) {
                        val usuarioNome = usuarioDocument.getString("nome")

                        if (!usuarioNome.isNullOrEmpty()) {
                            armarioRef.update(
                                "$idArmario.Disponibilidade", false,
                                "$idArmario.Locataria", usuarioNome
                            )
                                .addOnSuccessListener {
                                    Log.d(TAG, "Armário atualizado com sucesso. ID: $idArmario, Local: $localArmario")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Erro ao atualizar o armário: $e")
                                }
                        } else {
                            Log.e(TAG, "Nome do usuário não encontrado.")
                        }
                    } else {
                        Log.e(TAG, "Documento do usuário não encontrado.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Erro ao obter o documento do usuário: $e")
                }
        } else {
            Log.e(TAG, "Usuário, local do armário ou ID do armário não disponíveis.")
        }
    }
}
