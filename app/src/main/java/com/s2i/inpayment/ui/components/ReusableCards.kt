//package com.s2i.inpayment.ui.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.material3.Card
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@Composable
//fun ReusableCard(
//    title: String,
//    content: String,
//    backgroundColor: Color = Color(0xFF008080),  // Default color
//    contentColor: Color = Color.White,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        backgroundColor = backgroundColor,
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .height(150.dp),
//        elevation = 4.dp
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.h6.copy(fontSize = 20.sp),
//                color = contentColor
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = content,
//                style = MaterialTheme.typography.h4.copy(fontSize = 30.sp),
//                color = contentColor
//            )
//        }
//    }
//}