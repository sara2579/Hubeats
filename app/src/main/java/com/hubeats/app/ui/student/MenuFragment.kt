package com.hubeats.app.ui.student

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hubeats.app.R
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.viewmodel.CartViewModel
import com.hubeats.app.viewmodel.RestaurantViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

/** "Menu @ Restaurant": each item with − quantity +, and a cart button with the item count. */
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val restaurantViewModel: RestaurantViewModel by viewModels {
        ViewModelFactory(requireContext())
    }
    private val cartViewModel: CartViewModel by activityViewModels {
        ViewModelFactory(requireContext())
    }

    private var menuItems: List<MenuItem> = emptyList()
    private lateinit var adapter: MenuItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restaurantId = arguments?.getInt("restaurantId", -1) ?: -1
        val restaurantName = arguments?.getString("restaurantName").orEmpty()
        cartViewModel.startRestaurant(restaurantId, restaurantName)

        view.findViewById<TextView>(R.id.textMenuTitle).text =
            getString(R.string.menu_at, restaurantName)
        val buttonCart = view.findViewById<MaterialButton>(R.id.buttonCart)
        val textEmpty = view.findViewById<TextView>(R.id.textEmpty)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMenu)

        adapter = MenuItemAdapter(
            onAdd = { cartViewModel.add(it) },
            onRemove = { cartViewModel.remove(it) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        buttonCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        restaurantViewModel.getMenu(restaurantId).observe(viewLifecycleOwner) { items ->
            menuItems = items
            textEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            showRows()
        }
        cartViewModel.items.observe(viewLifecycleOwner) { showRows() }
        cartViewModel.itemCount.observe(viewLifecycleOwner) { count ->
            buttonCart.text = getString(R.string.cart_button, count)
        }
    }

    /** Combines the menu with the cart quantities. */
    private fun showRows() {
        adapter.submitList(menuItems.map { MenuRow(it, cartViewModel.quantityOf(it.id)) })
    }
}