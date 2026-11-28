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
import com.hubeats.app.viewmodel.CartItem

class CartAdapter(
    private val onAdd: (MenuItem) -> Unit,
    private val onRemove: (MenuItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val lineTotal: TextView = view.findViewById(R.id.textLineTotal)
        val quantity: TextView = view.findViewById(R.id.textQuantity)
        val minus: MaterialButton = view.findViewById(R.id.buttonMinus)
        val plus: MaterialButton = view.findViewById(R.id.buttonPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = getItem(position)
        val item = cartItem.menuItem
        holder.name.text = item.name
        holder.lineTotal.text = holder.itemView.context
            .getString(R.string.price_aed, item.price * cartItem.quantity)
        holder.quantity.text = cartItem.quantity.toString()
        holder.minus.setOnClickListener { onRemove(item) }
        holder.plus.setOnClickListener { onAdd(item) }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(old: CartItem, new: CartItem) =
            old.menuItem.id == new.menuItem.id
        override fun areContentsTheSame(old: CartItem, new: CartItem) = old == new
    }
}