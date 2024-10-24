//package com.s2i.inpayment.ui.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReusableBottomSheet(
//    sheetContent: @Composable () -> Unit,
//    modifier: Modifier = Modifier,
//    sheetPeekHeight: Dp = 56.dp,
//    content: @Composable () -> Unit
//) {
//    val bottomSheetState = rememberBottomSheetScaffoldState(
//        bottomSheetState = BottomSheetState(BottomSheetValue.Hidden)
//    )
//    val coroutineScope = rememberCoroutineScope()
//
//    BottomSheetScaffold(
//        scaffoldState = bottomSheetState,
//        sheetContent = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//                    .padding(16.dp)
//            ) {
//                sheetContent()
//            }
//        },
//        sheetPeekHeight = sheetPeekHeight,
//        modifier = modifier,
//        sheetBackgroundColor = MaterialTheme.colorScheme.surface
//    ) {
//        content()
//
//        Button(
//            onClick = {
//                coroutineScope.launch {
//                    if (bottomSheetState.bottomSheetState.isCollapsed) {
//                        bottomSheetState.bottomSheetState.expand()
//                    } else {
//                        bottomSheetState.bottomSheetState.collapse()
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(text = "Toggle Bottom Sheet")
//        }
//    }
//}
