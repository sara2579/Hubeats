package com.hubeats.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hubeats.app.data.database.AppDatabase
import com.hubeats.app.data.repository.HubEatsRepository

/** Builds every ViewModel and gives it the repository. */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val repository =
        HubEatsRepository(AppDatabase.getDatabase(context.applicationContext))

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(RestaurantViewModel::class.java) ->
            RestaurantViewModel(repository) as T
        // SALAM: add LoginViewModel and ManageMenuViewModel here
        // AMIRA: add EmployeeOrdersViewModel here
        // GAMAR: add StudentOrdersViewModel here
        // ZUNAIRAH: CartViewModel will be added here
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}