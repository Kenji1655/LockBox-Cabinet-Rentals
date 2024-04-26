package br.edu.puccampinas.PI3_ES_2024_Time17

import ArmarioAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

data class Armario(
    val disponibilidade: Boolean = false,
)


class EscolherArmarioActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listarArmario: RecyclerView
    private lateinit var btnVoltar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escolher_armario)

        listarArmario = findViewById(R.id.teste)
        firestore = FirebaseFirestore.getInstance()
        btnVoltar = findViewById(R.id.btnVoltar)

        prepararReclycleView()

        carregarArmarios()

        btnVoltar.setOnClickListener {
            finish()
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun prepararReclycleView() {
        listarArmario.layoutManager = LinearLayoutManager(this)
        listarArmario.setHasFixedSize(true)
    }
    private fun carregarArmarios(){
        val lugar = intent.getStringExtra("lugar")

        val listaDeArmarios = mutableListOf<Armario>()

        val colecaoArmarios = firestore.collection("Armario")

        if (lugar != null) {
            colecaoArmarios.document(lugar).get()
                .addOnSuccessListener { documento ->
                    var i = 1
                    while (true) {
                        val armarioData = documento.get("Armario $i") as? Map<*, *> ?: break
                        val disponibilidade = armarioData["Disponibilidade"] as? Boolean ?: false
                        Log.d("EscolherArmarioActivity", "Disponibilidade do Armário $i: $disponibilidade")
                        val armario = Armario(disponibilidade)
                        listaDeArmarios.add(armario)
                        Log.d("EscolherArmarioActivity", "Armário adicionado: $armario")
                        i++
                    }
                    val adapter = ArmarioAdapter(listaDeArmarios)
                    listarArmario.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.e("EscolherArmarioActivity", "Erro ao carregar armários", exception)
                }
        }
    }
}