package com.lesleytech.notespro

import android.content.Context
import android.widget.Toast

class Utility {
    companion object {
        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}