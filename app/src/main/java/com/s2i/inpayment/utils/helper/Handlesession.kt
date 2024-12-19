package com.s2i.inpayment.utils.helper

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//handle session logout
fun handleSessionLogout(
    coroutineScope: CoroutineScope,
    showErrorSheet: Boolean,
    setShowErrorSheet: (Boolean) -> Unit,
    setErrorMessage: (String) -> Unit
) {
    if (!showErrorSheet) {
        coroutineScope.launch {
            setErrorMessage("Session has expired. Please login again.")
            setShowErrorSheet(true)
        }
    }
}

// handle error

fun handleLoginFailer(
    error: Throwable,
    showErrorSheet: Boolean,
    setShowErrorSheet: (Boolean) -> Unit,
    setErrorMessage: (String) -> Unit
){
    if(!showErrorSheet){
        val errorMessage = when {
            error.message?.contains("400", ignoreCase = true) == true -> "Username atau password salah"
            error.message?.contains("404", ignoreCase = true) == true -> "Oops, sepertinya Anda belum mendaftar. Silahkan mendaftar dulu ya!"
            error.message?.contains("502", ignoreCase = true) == true -> "Mohon maaf, Saat ini kami mengalami masalah teknis. Silakan coba lagi nanti.."
            error.message?.contains("500", ignoreCase = true) == true -> "Mohon maaf ada kesalahan pada server. Silakan coba lagi nanti."
            error.message != null -> error.message!!
            else -> "Terjadi kesalahan, silakan coba lagi"
        }
        setShowErrorSheet(true)
        setErrorMessage(errorMessage)
        Log.d("SessionUtils", "Login failed. Showing error sheet: $errorMessage")
    }

}