package com.hubeats.app.ui.student

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hubeats.app.R
import com.hubeats.app.util.SessionManager
import com.hubeats.app.viewmodel.CartViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

/** The cart: chosen items, total in AED, and the Place Order button. */
class CartFragment : Fragment(R.layout.fragment_cart) {

    private val cartViewModel: CartViewModel by activityViewModels {
        ViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCart)
        val textEmpty = view.findViewById<TextView>(R.id.textEmpty)
        val textTotal = view.findViewById<TextView>(R.id.textTotal)
        val buttonPlaceOrder = view.findViewById<Button>(R.id.buttonPlaceOrder)

        val adapter = CartAdapter(
            onAdd = { cartViewModel.add(it) },
            onRemove = { cartViewModel.remove(it) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        cartViewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            // Empty cart: the button is disabled
            buttonPlaceOrder.isEnabled = list.isNotEmpty()
        }
        cartViewModel.total.observe(viewLifecycleOwner) { total ->
            textTotal.text = getString(R.string.cart_total, total)
        }

        buttonPlaceOrder.setOnClickListener {
            val session = SessionManager(requireContext())
            if (session.getUserId() == -1) {
                Toast.makeText(requireContext(), R.string.login_again, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buttonPlaceOrder.isEnabled = false // no double taps
            cartViewModel.placeOrder(session.getUserId(), session.getFullName().orEmpty())
        }

        cartViewModel.placedOrderId.observe(viewLifecycleOwner) { orderId ->
            if (orderId != null) showOrderPlacedDialog(orderId)
        }
    }

    private fun showOrderPlacedDialog(orderId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.order_placed_title)
            .setMessage(getString(R.string.order_placed_message, orderId))
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                cartViewModel.orderPlacedHandled()
                // Go to My Orders (Gamar) and drop menu + cart from the back stack
                findNavController().navigate(
                    R.id.myOrdersFragment,
                    null,
                    navOptions { popUpTo(R.id.menuFragment) { inclusive = true } }
                )
            }
            .show()
    }
}