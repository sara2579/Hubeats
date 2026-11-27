package com.hubeats.app.ui.student

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hubeats.app.R
import com.hubeats.app.data.entity.MenuItem

/** One menu item together with how many of it are in the cart. */
data class MenuRow(val item: MenuItem, val quantity: Int)

class MenuItemAdapter(
    private val onAdd: (MenuItem) -> Unit,
    private val onRemove: (MenuItem) -> Unit
) : ListAdapter<MenuRow, MenuItemAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val price: TextView = view.findViewById(R.id.textPrice)
        val quantity: TextView = view.findViewById(R.id.textQuantity)
        val minus: MaterialButton = view.findViewById(R.id.buttonMinus)
        val plus: MaterialButton = view.findViewById(R.id.buttonPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = getItem(position)
        holder.name.text = row.item.name
        holder.price.text = holder.itemView.context.getString(R.string.price_aed, row.item.price)
        holder.quantity.text = row.quantity.toString()
        holder.minus.isEnabled = row.quantity > 0
        holder.minus.setOnClickListener { onRemove(row.item) }
        holder.plus.setOnClickListener { onAdd(row.item) }
    }

    private object DiffCallback : DiffUtil.ItemCallback<MenuRow>() {
        override fun areItemsTheSame(old: MenuRow, new: MenuRow) = old.item.id == new.item.id
        override fun areContentsTheSame(old: MenuRow, new: MenuRow) = old == new
    }
}