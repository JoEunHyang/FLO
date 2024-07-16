package com.example.flo.ui.song

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.example.flo.R

class CustomToast(context: Context, message: String) : Toast(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.toast_like, null)
        view.findViewById<TextView>(R.id.tvSample).apply {
            text = message
        }
        setView(view)
        duration = Toast.LENGTH_SHORT
    }
}