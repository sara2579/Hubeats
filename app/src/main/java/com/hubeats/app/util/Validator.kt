package com.hubeats.app.util

import com.google.android.material.textfield.TextInputLayout


object Validator {

    fun isBlank(text: String?): Boolean = text.isNullOrBlank()

    /** A price must be a number greater than 0. Accepts "12.5" or "12,5". */
    fun isValidPrice(text: String?): Boolean {
        val value = text?.trim()?.replace(',', '.')?.toDoubleOrNull() ?: return false
        return value.isFinite() && value > 0.0
    }

    fun requireText(layout: TextInputLayout, errorMessage: String): Boolean {
        val invalid = isBlank(layout.editText?.text?.toString())
        layout.error = if (invalid) errorMessage else null
        return !invalid
    }

    fun requirePrice(layout: TextInputLayout, errorMessage: String): Boolean {
        val invalid = !isValidPrice(layout.editText?.text?.toString())
        layout.error = if (invalid) errorMessage else null
        return !invalid
    }
}