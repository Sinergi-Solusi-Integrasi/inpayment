//package com.s2i.inpayment.ui.components
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//
//@Composable
//fun ReusableButton(
//    buttonText: String,
//    backgroundColor: Color = Color(0xFF008080), // Default color
//    contentColor: Color = Color.White,
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        modifier = Modifier.fillMaxWidth(),
//        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
//    ) {
//        Text(
//            text = buttonText,
//            color = contentColor
//        )
//    }
//}