package br.edu.puccampinas.lockbox

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MenuActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nomeUsuario: TextView
    private lateinit var btnSair: Button
    private lateinit var btnAdicionarCartao: Button
    private lateinit var firestore: FirebaseFirestore

    class Desconectar : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage("Deseja desconectar?")
                .setPositiveButton("Desconectar") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    requireActivity().finish()
                }
                .setNegativeButton("Cancelar") { _, _ ->
                    // User cancelled the dialog.
                }
            return builder.create()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btnSair = findViewById(R.id.btnSair)
        btnAdicionarCartao = findViewById(R.id.btnAdicionarCartao)
        nomeUsuario = findViewById(R.id.nomeUsuario)


        val user = auth.currentUser
        user?.let { currentUser ->
            val userId = currentUser.uid
            val userDocumentRef = firestore.collection("Usuario").document(userId)

            userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nome = documentSnapshot.getString("nome")
                    nomeUsuario.text = nome

                } else {
                    Log.d(TAG, "Documento não existe")
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "erro ao pegar o documento", exception)
            }
        }

        btnSair.setOnClickListener {
            val dialog = Desconectar()
            dialog.show(supportFragmentManager, "DesconectarDialog")
        }

        btnAdicionarCartao.setOnClickListener {
            val iAdicionarCartao = Intent(this,AdicionarCartaoActivity::class.java)
            startActivity(iAdicionarCartao)
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            finish()
        }
    }
}
