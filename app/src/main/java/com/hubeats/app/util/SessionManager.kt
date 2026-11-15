package com.hubeats.app.util

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(
        "HubEatsSession",
        Context.MODE_PRIVATE
    )

    // saved the logged-in user's information
    fun saveUser(
        id: Int,
        fullName: String,
        role: String,
        restaurantId: Int?
    ) {
        prefs.edit()
            .putInt("userId",id)
            .putString("fullName", fullName)
            .putString("role", role)
            .putInt("restaurantId", restaurantId ?: -1)
            .apply()
    }

    // retrieves user information
    fun getUserId(): Int = prefs.getInt("userId", -1)
    fun getFullName(): String? = prefs.getString("fullName", "")
    fun getRestaurantId(): Int? {
        val id = prefs.getInt("restaurantId", -1)
        return if(id == -1) null else id
    }

    // clears saved session information
    fun logout() {
        prefs.edit().clear().apply()
    }
}