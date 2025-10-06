package com.example.bruno.ui.shoppinglist.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno.databinding.ItemProductBinding
import com.example.bruno.model.Item

// adapter pra mostrar os itens dentro de uma lista de compras
class ItemAdapter(
    // funcao quando marcar/desmarcar o check
    private val onCheck: (Item) -> Unit,
    // funcao quando clicar pra deletar
    private val onDelete: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ViewHolder>(DiffCallback()) {

    // criar o viewholder (inflar o layout de cada item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    // ligar os dados do item no viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.txtName.text = item.name
            binding.txtQuantity.text = "${item.quantity} ${item.unit}"
            binding.txtCategory.text = item.category.displayName

            binding.checkBought.isChecked = item.isBought


            binding.checkBought.setOnCheckedChangeListener { _, isChecked ->
                item.isBought = isChecked
                onCheck(item)
            }


            binding.btnDeleteItem.setOnClickListener {
                onDelete(item)
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
            oldItem == newItem
    }
}
