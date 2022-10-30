package com.dedek.myapplication.Helper

import android.content.Context
import android.widget.Toast
import java.text.NumberFormat
import java.util.*

class MyUtilities {

    companion object{
        fun numberToCurrency(number: Int): String {
            return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(number).toString()
                .removeSuffix(",00").replace("Rp", "Rp. ")
        }

        fun toast(context: Context, message: String) {
            try {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

}