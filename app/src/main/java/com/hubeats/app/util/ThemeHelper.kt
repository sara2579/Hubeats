package com.hubeats.app.util


import android.content.Context
import androidx.appcompat.app.AppCompatDelegate


object ThemeHelper {

    private const val PREFS_NAME = "hubeats_theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    fun applySavedTheme(context: Context) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode(context)) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun isDarkMode(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK_MODE, false)

    fun setDarkMode(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        applySavedTheme(context)
    }

    /** Flips the choice and returns the new value (true = dark). */
    fun toggle(context: Context): Boolean {
        val newValue = !isDarkMode(context)
        setDarkMode(context, newValue)
        return newValue
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}