import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.puccampinas.PI3_ES_2024_Time17.Armario
import br.edu.puccampinas.PI3_ES_2024_Time17.FinalizarPagamentoActivity
import br.edu.puccampinas.PI3_ES_2024_Time17.R // Certifique-se de importar corretamente o R

class ArmarioAdapter(private val listaDeArmarios: List<Armario>) : RecyclerView.Adapter<ArmarioAdapter.ArmarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArmarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.armario_lista, parent, false)
        return ArmarioViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArmarioViewHolder, position: Int) {
        val currentItem = listaDeArmarios[position]
        holder.armarioNumero.text = "Armário ${position + 1}"
        holder.disponibilidade.text = if (currentItem.disponibilidade) "Disponivel" else "Ocupado"
        holder.alocar.isEnabled = currentItem.disponibilidade
        holder.alocar.setOnClickListener {
            val numeroArmario = position + 1
            val context = holder.itemView.context
            val intent = Intent(context, FinalizarPagamentoActivity::class.java)
            intent.putExtra("numero_armario", numeroArmario)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = listaDeArmarios.size

    class ArmarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alocar: Button = itemView.findViewById(R.id.irPagamento)
        val armarioNumero: TextView = itemView.findViewById(R.id.armarioNumero)
        val disponibilidade: TextView = itemView.findViewById(R.id.disponibilidade)
    }
}
