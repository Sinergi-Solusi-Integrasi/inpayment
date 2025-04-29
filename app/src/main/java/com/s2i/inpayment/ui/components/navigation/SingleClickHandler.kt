package com.s2i.inpayment.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberSingleClickHandler(): () -> Boolean {
    val isClicked = remember { mutableStateOf(false) }
    return {
        if (!isClicked.value) {
            isClicked.value = true
            true
        } else {
            false
        }
    }
}
