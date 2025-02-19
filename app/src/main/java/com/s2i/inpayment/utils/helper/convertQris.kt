package com.s2i.inpayment.utils.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// generate for trxId
fun generateTrxId(): String {
    val date = SimpleDateFormat("yyMMdd", Locale.ENGLISH).format(Date())
    val random = (0..9).shuffled().take(4).joinToString("")
    return "MBL-$date-$random"
}

// generate for waktu
fun generateCurrentTime(): String {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    return timeFormat.format(Date())
}

// Generate Signatures
fun generateSignature(mid: String, tid: String, waktu: String, clientsecret: String): String {
    val input = "$mid$tid$waktu$clientsecret" // Gabungkan tanpa pemisah
    // Tambahkan log untuk input signature
    Log.d("SignatureDebug", "Signature Input: $input")

    val md = MessageDigest.getInstance("MD5")
    val hashBytes = md.digest(input.toByteArray())
    val signature = hashBytes.joinToString("") { "%02x".format(it) } // Output dalam huruf kecil

    // Tambahkan log untuk hasil signature
    Log.d("SignatureDebug", "Generated Signature: $signature")

    return signature
}


// generaete qrcode
fun generateQRCode(content: String): Bitmap {
    val qrCodeWriter = QRCodeWriter()
    return  try {
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: WriterException) {
        throw RuntimeException("Error generating QR code", e)
    }
}