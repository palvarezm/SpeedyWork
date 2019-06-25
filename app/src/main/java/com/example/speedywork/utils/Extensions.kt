package com.example.speedywork.utils

import android.content.Context
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

internal inline fun TextInputEditText.validate(validator: TextView.()-> Boolean, textInputLayout: TextInputLayout, message: String) : Boolean{
    textInputLayout.error = if (validator()) null else message
    return validator()
}