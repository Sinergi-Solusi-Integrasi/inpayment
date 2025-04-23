package com.s2i.inpayment.ui.screen.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.RoundedCorner
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.components.shimmer.profile.ProfileCardShimmer
import com.s2i.inpayment.ui.theme.BrightTeal
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.Red500
import com.s2i.inpayment.ui.theme.linearGradientBackground
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.UsersViewModel
import com.s2i.inpayment.utils.helper.workmanager.TokenWorkManagerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sessionManager: SessionManager,
    authViewModel: AuthViewModel = koinViewModel(),
    usersViewModel: UsersViewModel = koinViewModel()
) {

    val scope = rememberCoroutineScope()
    val usersState by usersViewModel.users.collectAsState()
    var isNavigating by remember { mutableStateOf(false) }
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by usersViewModel.loading.collectAsState()
    val loadingLogout by authViewModel.loadingState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val error by usersViewModel.error.collectAsState()

    BackHandler(enabled = !loadingLogout && !isNavigating) {
        isNavigating = true
        scope.launch {
            navController.popBackStack()
        }
    }


    LaunchedEffect(Unit) {
        if (!loading) {
            isStartupLoading = true
            usersViewModel.fetchUsers()
        }
    }


    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isStartupLoading = false
                isRefreshing = true
                usersViewModel.fetchUsers()
                delay(2000) // simulate refresh delay
                isRefreshing = false
            }
        }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        /* Handle back navigation */
                        if (!loadingLogout && !isNavigating) {
                            isNavigating = true
                            scope.launch {
                                navController.popBackStack()
                            }
                        }
                    }
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BrightTeal20,
                    titleContentColor = DarkTeal40,
                    navigationIconContentColor = DarkTeal21
                ),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightTeal20) // Light gray background
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (loading && usersState == null) {
                    ProfileCardShimmer()
                } else {
                    // Profile card with gradient background
                    if (usersState != null) {
                        ProfileCard(navController, sessionManager, scope, usersState)
                    } else {
                        ProfileCardShimmer()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // First menu group (with white background card)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            ProfileMenuItem(
                                icon = Icons.Default.DirectionsCarFilled,
                                title = buildString {
        append("Vehicles")
    },
                                onClick = {
                                    navController.navigate("vehicles_screen") {
                                        popUpTo("profile_screen") { inclusive = false }
                                    }
                                }
                            )
                            ProfileMenuItem(
                                icon = Icons.Default.Receipt,
                                title = "All transactions",
                                onClick = {
                                    navController.navigate("history_screen") {
                                        popUpTo("profile_screen") { inclusive = false }
                                    }
                                }
                            )
                            ProfileMenuItem(
                                icon = R.drawable.ic_help,
                                title = "Help and support",
                                onClick = { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Second menu group (with white background card)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            ProfileMenuItem(
                                icon = R.drawable.ic_about,
                                title = "About us",
                                onClick = { }
                            )
                            ProfileMenuItem(
                                icon = R.drawable.ic_terms,
                                title = "Terms and conditions",
                                onClick = { }
                            )
                            ProfileMenuItem(
                                icon = R.drawable.ic_feedback,
                                title = "Feedback",
                                onClick = { }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout button (separate card with white background)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        ProfileMenuItem(
                            icon = R.drawable.ic_logout,
                            iconColor = Red500,
                            title = "Logout",
                            textColor = Color.Red,
                            onClick = {
                                if (!loadingLogout) {
                                    scope.launch {
                                        authViewModel.logout()
                                        navController.navigate("login_screen") {
                                            popUpTo("profile_screen") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Overlay Loading
        if (loadingLogout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ){
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
                    modifier = Modifier.size(120.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    navController: NavController,
    sessionManager: SessionManager,
    scope: CoroutineScope,
    usersState: ProfileModel?
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gradient background - lebih smooth dan konsisten
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .linearGradientBackground(
                        colors = listOf(
                            Color(0xFF1F4B36), // Dark green (atas)
                            Color(0xFF52B788)  // Light green (bawah)
                        )
                    )
            )

            // Foto profil - Posisi sesuai dengan gambar pertama
            Image(
                painter = painterResource(id = R.drawable.ic_people),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = 16.dp, y = 95.dp)
                    .clip(CircleShape)
                    .align(Alignment.TopStart)
            )

            // Konten putih di bawah foto profil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 96.dp, top = 80.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Hai, ${usersState?.name ?: "Guest"}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrightTeal20
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Account: ${usersState?.accountNumber ?: "-"}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Account Number",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Account Number",
                                    usersState?.accountNumber ?: ""
                                )
                                clipboardManager.setPrimaryClip(clip)
                            },
                        tint = BrightTeal
                    )
                }

                Text(
                    text = "${usersState?.mobileNumber ?: "No Phone Number"}",
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Text(
                    text = "RFID: ${usersState?.selectVehicle?.rfid ?: " - "}",
                    fontSize = 14.sp,
                    color = Color.Black
                )

                // Informasi kendaraan di kanan bawah
                usersState?.selectVehicle?.let { vehicle ->
                    Log.d("ProfileCard", "Vehicle data: brand=${vehicle.brand}, model=${vehicle.model}, plateNumber=${vehicle.plateNumber}")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.TopEnd),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "${vehicle.brand} ${vehicle.model}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                            Text(
                                text = "${vehicle.plateNumber}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                }
            }


        }
    }
}


//@Composable
//fun ProfileMenu(
//    navController: NavController
//) {
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        ProfileMenuItem(
//            icon = Icons.Default.DirectionsCarFilled,
//            title = "Vehicles",
//            onClick = {// Navigasi ke HomeScreen (atau layar yang sesuai)
//                navController.navigate("vehicles_screen") {
//                    // Pop up semua screen yang ada di atas HomeScreen (termasuk profile_screen)
//                    popUpTo("profile_screen") { inclusive = false }
//                }
//            })
//        ProfileMenuItem(icon = Icons.Default.Receipt, title = "All Transactions", onClick = {
//            navController.navigate("history_screen") {
//                popUpTo("profile_screen") { inclusive = false}
//            }
//        })
//        ProfileMenuItem(icon = R.drawable.ic_help, title = "Help and Support", onClick = {})
//    }
//}
//
//@Composable
//fun ProfileFooter(
//    navController: NavController,
//    authViewModel: AuthViewModel,
//    sessionManager: SessionManager,
//    scope: CoroutineScope
//) {
//    val logoutLoading by authViewModel.loadingState.collectAsState()
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        ProfileMenuItem(icon = R.drawable.ic_about, title = "About Us", {})
//        ProfileMenuItem(icon = R.drawable.ic_terms, title = "Terms and Conditions", {})
//        ProfileMenuItem(icon = R.drawable.ic_feedback, title = "Feedback", {})
//        // Tombol Logout
//        Spacer(modifier = Modifier.height(16.dp))
//        ProfileMenuItem(
//            icon = R.drawable.ic_logout, // Icon logout
//            title = "Logout",
//            onClick = {
//                if (!logoutLoading) {
//                    scope.launch {
//                        // Logout dari session
//                        authViewModel.logout()
//                        // Navigasi ke HomeScreen (atau layar yang sesuai)
//                        navController.navigate("login_screen") {
//                            // Pop up semua screen yang ada di atas HomeScreen (termasuk profile_screen)
//                            popUpTo("profile_screen") { inclusive = true }
//                        }
//                    }
//                }
//            }
//        )
//    }
//}

@Composable
fun ProfileMenuItem(
    icon: Any,
    title: String,
    textColor: Color = Color.DarkGray,
    iconColor: Color = Color.Gray,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }

            is Int -> {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    ProfileScreen(navController = NavController(LocalContext.current), authViewModel = AuthViewModel(LocalContext.current))
//}
