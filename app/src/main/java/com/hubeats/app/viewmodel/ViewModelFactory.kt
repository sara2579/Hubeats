package com.hubeats.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hubeats.app.data.database.AppDatabase
import com.hubeats.app.data.repository.HubEatsRepository
import com.hubeats.app.viewmodel.LoginViewModel

/** Builds every ViewModel and gives it the repository. */
class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val repository =
        HubEatsRepository(AppDatabase.getDatabase(context.applicationContext))

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {

        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(repository) as T

        modelClass.isAssignableFrom(RestaurantViewModel::class.java) ->
            RestaurantViewModel(repository) as T

        modelClass.isAssignableFrom(ManageMenuViewModel::class.java) ->
            ManageMenuViewModel(repository) as T

        // AMIRA: add EmployeeOrdersViewModel here
        // GAMAR: add StudentOrdersViewModel here

        modelClass.isAssignableFrom(CartViewModel::class.java) ->
            CartViewModel(repository) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}