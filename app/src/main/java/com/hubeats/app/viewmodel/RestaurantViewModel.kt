package com.hubeats.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.entity.Restaurant
import com.hubeats.app.data.repository.HubEatsRepository

/** Data for the restaurant list and the menu screens. Only talks to the repository. */
class RestaurantViewModel(private val repository: HubEatsRepository) : ViewModel() {

    private val query = MutableLiveData("")

    /** All restaurants, or only the ones matching the search text. */
    val restaurants: LiveData<List<Restaurant>> = query.switchMap { text ->
        if (text.isBlank()) repository.getAllRestaurants()
        else repository.searchRestaurants(text.trim())
    }

    fun search(text: String) {
        query.value = text
    }

    fun getMenu(restaurantId: Int): LiveData<List<MenuItem>> =
        repository.getMenu(restaurantId)
}
