package com.hubeats.app.viewmodel

import android.content.Context                                   // gives access to the app (needed to open the database)
import androidx.lifecycle.ViewModel                              // the base class all our ViewModels build on
import androidx.lifecycle.ViewModelProvider                      // Android's "ViewModel maker" interface
import com.hubeats.app.data.database.AppDatabase                 // Sarah's database
import com.hubeats.app.data.repository.HubEatsRepository         // Sarah's repository (the only door to the data)

/**
 * Builds every ViewModel and gives it the repository.
 *
 * Why we need this: our ViewModels need the repository passed in, e.g.
 * StudentOrdersViewModel(repository). Android builds ViewModels itself (so they survive rotation)
 * but Android can't guess the repo on its own,
 * so screens ask this factory, and the factory builds the right ViewModel.
 */


class ViewModelFactory(context: Context) : ViewModelProvider.Factory {

    // Create the repository ONCE here, then hand the same one to every ViewModel.
    // applicationContext = the whole app (not one screen), so the database isn't tied to a screen.
    private val repository =
        HubEatsRepository(AppDatabase.getDatabase(context.applicationContext))

    // Android calls create() when a screen asks for a ViewModel.
    // modelClass = WHICH ViewModel the screen wants.
    // "when" checks each case top to bottom and builds the matching one.
    @Suppress("UNCHECKED_CAST")   // hides a warning about "as T"; safe because we just checked the class
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {

        // Zuni: restaurant list + menu
        modelClass.isAssignableFrom(RestaurantViewModel::class.java) ->
            RestaurantViewModel(repository) as T

        // SALAM: add LoginViewModel and ManageMenuViewModel here
        // AMIRA: add EmployeeOrdersViewModel here

        // GAMAR: My Orders + Order Detail
        // "If the screen asked for StudentOrdersViewModel, build one and give it the repository."
        modelClass.isAssignableFrom(StudentOrdersViewModel::class.java) ->
            StudentOrdersViewModel(repository) as T

        // Zuni: cart
        modelClass.isAssignableFrom(CartViewModel::class.java) ->
            CartViewModel(repository) as T

        // if none of the above matched
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}