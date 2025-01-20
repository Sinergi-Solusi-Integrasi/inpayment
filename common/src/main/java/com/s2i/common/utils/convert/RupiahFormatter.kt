package com.s2i.common.utils.convert

import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

object RupiahFormatter{
    fun formatToRupiah(amount: Int): String {
//        val localeID = Locale("in", "ID")
//        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
//        numberFormat.maximumFractionDigits = 2 // menambahkan perak
//        numberFormat.minimumFractionDigits = 2 // pastikan dua digit untuk perak
//        return numberFormat.format(amount).replace("Rp", "Rp. ").replace(",00", "")
        return formatToRupiah(amount.toLong())

    }

    fun formatToRupiah(amount: Long): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0 // tidak ada digit desimal
        numberFormat.minimumFractionDigits = 0 // pastikan dua digit untuk perak
        return numberFormat.format(amount).replace("Rp", "Rp. ").replace(",00", "")
    }

    fun formatToRupiah(amount: BigInteger): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0 // tidak ada digit desimal
        numberFormat.minimumFractionDigits = 0 // pastikan dua digit untuk perak
        return numberFormat.format(amount).replace("Rp", "Rp. ").replace(",00", "")
    }

}