package com.hubeats.app.ui.student


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.hubeats.app.R
import com.hubeats.app.viewmodel.RestaurantViewModel
import com.hubeats.app.viewmodel.ViewModelFactory

/** "Pick your restaurant": search box + list. Tapping a restaurant opens its menu. */
class RestaurantListFragment : Fragment(R.layout.fragment_restaurant_list) {

    private val viewModel: RestaurantViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerRestaurants)
        val emptyText = view.findViewById<TextView>(R.id.textEmpty)
        val searchBox = view.findViewById<TextInputEditText>(R.id.editSearch)

        val adapter = RestaurantAdapter { restaurant ->
            findNavController().navigate(
                R.id.menuFragment,
                bundleOf(
                    "restaurantId" to restaurant.id,
                    "restaurantName" to restaurant.name
                )
            )
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.restaurants.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        searchBox.doAfterTextChanged { text ->
            viewModel.search(text?.toString().orEmpty())
        }
    }
}