package com.s2i.inpayment.ui.screen.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCarFilled
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.custome.CustomLinearProgressIndicator
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.VeryDarkBlue
import com.s2i.inpayment.ui.theme.White
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.UsersViewModel
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
)
 {
    val scope = rememberCoroutineScope()
    val usersState by usersViewModel.users.collectAsState()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by usersViewModel.loading.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val error by usersViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        if (loading && isStartupLoading) {
            isStartupLoading = true
        } else if (!loading) {
            usersViewModel.fetchUsers()
            isStartupLoading = false
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

     Box(
         modifier = Modifier
             .fillMaxSize()
     ) {
         Image(
             painter = painterResource(id = R.drawable.background1),
             contentDescription = null,
             contentScale = ContentScale.FillBounds,
             modifier = Modifier.matchParentSize()
         )

         Scaffold(
             topBar = {
                 TopAppBar(
                     title = { Text("Profile") },
                     navigationIcon = {
                         IconButton(onClick = { navController.navigateUp() }) {
                             Icon(
                                 imageVector = Icons.Filled.ArrowBackIos,
                                 contentDescription = "Back",
                                 tint = Color.White
                             )
                         }
                     },
                     colors = TopAppBarDefaults.topAppBarColors(
                         containerColor = Color.Transparent,
                         titleContentColor = White,
                         navigationIconContentColor = DarkTeal21
                     ),
                 )
             },
             modifier = Modifier.fillMaxSize(),
             containerColor = Color.Transparent // Supaya background dari Box tetap terlihat
         ) { paddingValues ->
             Column(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(paddingValues),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 ProfileCard(
                     navController = navController,
                     sessionManager = sessionManager,
                     scope = scope,
                     usersState = usersState
                 )
                 ProfileMenu(navController = navController)
                 ProfileFooter(
                     navController = navController,
                     authViewModel = authViewModel,
                     sessionManager = sessionManager,
                     scope = scope
                 )

//                 ProfileMenuItem(
//                     icon = Icons.Default.Settings,
//                     title = "Settings",
//                     onClick = {},
//                     modifier = Modifier.padding(end = 16.dp)
//                 )
//                 Spacer(modifier = Modifier.height(16.dp))
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
)
 {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_people), // Replace with profile image
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Hai, ${usersState?.name ?: "Guest"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Account: ${usersState?.accountNumber ?: "-"}",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            tint = Color.White,
                            contentDescription = "Account Number Options",
                            modifier = Modifier.clickable {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Account Number",
                                    usersState?.accountNumber ?: ""
                                )
                                clipboardManager.setPrimaryClip(clip)
                            }
                        )
                    }
                    Text(
                        text = usersState?.mobileNumber ?: "No Phone Number",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "RFID: ${usersState?.selectVehicle?.rfid ?: ""} ",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Pastikan jika kendaraan ada, tampilkan detail kendaraan
            usersState?.selectVehicle?.let { vehicle ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Brand: ${vehicle.brand} Model: ${vehicle.model}   ${vehicle.group}   Plate Number: ${vehicle.plateNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle edit profile */ },
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Edit profile")
                }
                Button(
                    onClick = { /* Handle data account */ },
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Data account")
                }
            }
        }
    }
}

///
@Composable
fun ProfileMenu(
    navController: NavController
)
 {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProfileMenuItem(
            icon = Icons.Default.DirectionsCarFilled,
            title = "Vehicles",
            onClick = {// Navigasi ke HomeScreen (atau layar yang sesuai)
                navController.navigate("vehicles_screen") {
                    // Pop up semua screen yang ada di atas HomeScreen (termasuk profile_screen)
                    popUpTo("profile_screen") { inclusive = false }
                }
            })
        ProfileMenuItem(
            icon = R.drawable.ic_riwayat,
            title = "All Transactions",
            onClick = {}
        )
        ProfileMenuItem(icon = R.drawable.ic_help,
            title = "Help and Support",
            onClick = {}
        )
    }
}

@Composable
fun ProfileFooter(
    navController: NavController,
    authViewModel: AuthViewModel,
    sessionManager: SessionManager,
    scope: CoroutineScope
)
 {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProfileMenuItem(icon = R.drawable.ic_about,
            title = "About Us",
            onClick =  {

            }
        )
        ProfileMenuItem(icon = R.drawable.ic_terms,
            title = "Terms and Conditions",
            onClick =  {

            }
        )
        ProfileMenuItem(icon = R.drawable.ic_feedback,
            title = "Feedback",
            onClick =  {

            }
        )

        // Tombol Logout
        ProfileMenuItem(
            icon = R.drawable.ic_logout, // Icon logout
            title = "Logout",
            onClick = {
                scope.launch {
                    // Logout dari session
                    authViewModel.logout()

                    sessionManager.isLoggedOut = true

                    // Navigasi ke HomeScreen (atau layar yang sesuai)
                    navController.navigate("login_screen") {
                        // Pop up semua screen yang ada di atas HomeScreen (termasuk profile_screen)
                        popUpTo("profile_screen") { inclusive = true }
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: Any,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
)
 {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            is Int -> {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
