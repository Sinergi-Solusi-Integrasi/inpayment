package com.s2i.common.utils.convert

import java.text.NumberFormat
import java.util.Locale

object RupiahFormatter{
    fun formatToRupiah(amount: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 2 // menambahkan perak
        numberFormat.minimumFractionDigits = 2 // pastikan dua digit untuk perak
        return numberFormat.format(amount).replace("Rp", "Rp. ").replace(",00", "")

    }
}