package com.tuapp.aprendeapp.ui.principal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tuapp.aprendeapp.databinding.ItemRecursoBinding
import com.tuapp.aprendeapp.datos.modelo.Recurso

class AdaptadorRecursos(
    private var lista: List<Recurso>,
    private val alHacerClic: (Recurso) -> Unit
) : RecyclerView.Adapter<AdaptadorRecursos.RecursoViewHolder>() {

    fun actualizarLista(nuevaLista: List<Recurso>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecursoViewHolder {
        val binding = ItemRecursoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecursoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecursoViewHolder, position: Int) {
        val recurso = lista[position]
        holder.bind(recurso)
    }

    override fun getItemCount() = lista.size

    inner class RecursoViewHolder(private val binding: ItemRecursoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recurso: Recurso) {
            binding.textoTituloRecurso.text = recurso.titulo
            binding.textoTipoRecurso.text = "Tipo: ${recurso.tipo}"
            binding.textoDescripcionRecurso.text = recurso.descripcion

            if (recurso.votos > 0) {
                binding.textoPromedio.text = "⭐ ${String.format("%.1f", recurso.promedio)} (${recurso.votos} votos)"
            } else {
                binding.textoPromedio.text = "Sin calificación"
            }

            // Cargar imagen (usamos Glide)
            if (recurso.imagen.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(recurso.imagen)
                    .into(binding.imagenRecurso)
            } else {
                binding.imagenRecurso.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener { alHacerClic(recurso) }
        }
    }
}