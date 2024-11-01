package com.s2i.inpayment.ui.components

import androidx.annotation.DrawableRes
import com.s2i.inpayment.R

sealed class Onboard(
    @DrawableRes val image: Int,
    val title: String,
    val desc: String,
) {
    data object FirstPages : Onboard(
        image = R.drawable.vector_car,
        title = "Selamat Datang",
        desc =  "di inpayment"
    )

    data object SecondPages : Onboard(
        image = R.drawable.vector_car,
        title = "Fast in, Fast Go",
        desc =  "rasakan sensasi cepatnya"
    )
}