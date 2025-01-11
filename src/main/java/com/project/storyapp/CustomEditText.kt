package com.project.storyapp

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var isPasswordField: Boolean = false
    var isEmailField: Boolean = false

    init {
        // Tambahkan listener untuk validasi
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()

                // Lakukan validasi jika input tidak kosong
                if (input.isNotEmpty()) {
                    if (isPasswordField && input.length < 8) {
                        error = context.getString(R.string.password_too_short)
                    } else if (isEmailField && !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                        error = context.getString(R.string.invalid_email)
                    } else {
                        error = null // Reset error jika valid
                    }
                }
            }
        })
    }

    // Fungsi untuk memvalidasi secara eksplisit
    fun isValid(): Boolean {
        val input = text.toString()

        if (input.isEmpty()) {
            error = context.getString(R.string.field_required)
            return false
        }

        if (isPasswordField && input.length < 8) {
            error = context.getString(R.string.password_too_short)
            return false
        }

        if (isEmailField && !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            error = context.getString(R.string.invalid_email)
            return false
        }

        return true
    }
}
