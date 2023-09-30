package curso_dio.eletriccarapp.presentations.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import curso_dio.eletriccarapp.R
import curso_dio.eletriccarapp.domain.Carro

class Car_Adapter(private val carros: List<Carro>) :
    RecyclerView.Adapter<Car_Adapter.ViewHolder>() {

    var carItemLister : (Carro) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.carro_item, parent, false)
        return ViewHolder(view)
    }
    //pega conteudo da view e troca pela informação de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.preco.text = carros[position].preco
        holder.bateria.text = carros[position].bateria
        holder.potencia.text = carros[position].potencia
        holder.recarga.text = carros[position].recarga
        holder.favorito.setOnClickListener{
            val carro = carros[position]
            carItemLister(carros[position])
            setupFavorite(carro, holder)
        }

    }

    private fun setupFavorite(
        carro: Carro,
        holder: ViewHolder
    ) {
        carro.isFavorite = !carro.isFavorite
        if (carro.isFavorite)
            holder.favorito.setImageResource(R.drawable.ic_star)
        else
            holder.favorito.setImageResource(R.drawable.ic_star)
    }

    override fun getItemCount(): Int = 0

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val preco: TextView
        val bateria: TextView
        val potencia: TextView
        val recarga: TextView
        val favorito: ImageView

        init {

            view.apply {

                preco = findViewById(R.id.tv_preço_value)
                bateria = findViewById(R.id.tv_bateria_value)
                potencia = findViewById(R.id.tv_potencia_value)
                recarga = findViewById(R.id.tv_recarga_value)
                favorito = findViewById(R.id.iv_favorite)

            }

        }
    }
}


