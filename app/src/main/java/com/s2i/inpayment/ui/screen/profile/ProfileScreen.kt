package com.s2i.inpayment.ui.screen.profile

import android.content.ClipData
import android.content.ClipboardManager
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.data.local.auth.SessionManager
import com.s2i.domain.entity.model.users.ProfileModel
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.shimmer.profile.ProfileCardShimmer
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.theme.Gagal
import com.s2i.inpayment.ui.theme.Gagal1
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
) {
    // Your existing variables remain the same
    val scope = rememberCoroutineScope()
    val usersState by usersViewModel.users.collectAsState()
    var isNavigating by remember { mutableStateOf(false) }
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by usersViewModel.loading.collectAsState()
    val loadingLogout by authViewModel.loadingState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val error by usersViewModel.error.collectAsState()

    // Your existing LaunchedEffect and BackHandler remain the same
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
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                    )
                },
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
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightTeal20) // Background color for the entire screen
        ) {
            // This is the new green background element with rounded bottom corners
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Adjust this height based on your profile card size
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .background(DarkTeal40) // Replace with your green color
            )

            // The rest of your content goes here, with padding values
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Item 1: Profile Card
                item {
                    if (loading && usersState == null) {
                        ProfileCardShimmer()
                    } else if (usersState != null) {
                        ProfileCard(navController, sessionManager, scope, usersState)
                    } else {
                        ProfileCardShimmer()
                    }
                }

                // The rest of your items remain the same
                if (!loading || usersState != null) {
                    // Item 2: First menu group
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                ProfileMenuItem(
                                    icon = Icons.Default.CarRental,
                                    title = "Vehicles",
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
                            }
                        }
                    }

                    // Item 4: Logout button
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            ProfileMenuItem(
                                icon = R.drawable.ic_logout,
                                iconColor = Gagal1,
                                title = "Logout",
                                textColor = Gagal,
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

                    // Item 5: Bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Your overlay loading remains the same
        if (loadingLogout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header row with profile info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile picture
                    Image(
                        painter = painterResource(id = R.drawable.ic_people),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF2F2F7))
                            .border(1.dp, Color(0xFFE5E5EA), CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // User info
                    Column {
                        // User name
                        Text(
                            text = "${usersState?.name ?: "Guest"}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color(0xFF000000),
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Account number with copy
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Account: ${usersState?.accountNumber ?: "-"}",
                                fontSize = 15.sp,
                                color = Color(0xFF8E8E93),
                                letterSpacing = (-0.24).sp
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Account Number",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        val clipboardManager =
                                            context.getSystemService("clipboard") as ClipboardManager
                                        val clip = ClipData.newPlainText(
                                            "Account Number",
                                            usersState?.accountNumber ?: ""
                                        )
                                        clipboardManager.setPrimaryClip(clip)
                                    },
                                tint = Color.Gray
                            )
                        }
                    }
                }

                Divider(
                    color = Color(0xFFE5E5EA),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Location
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "${usersState?.mobileNumber ?: " "}",
                        fontSize = 17.sp,
                        color = Color(0xFF000000),
                        letterSpacing = (-0.24).sp
                    )
                }

                // RFID info
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "RFID",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "RFID: ${usersState?.selectVehicle?.rfid ?: " - "}",
                        fontSize = 17.sp,
                        color = Color(0xFF000000),
                        letterSpacing = (-0.24).sp
                    )
                }

                // Vehicle information
                usersState?.selectVehicle?.let { vehicle ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Vehicle",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "${vehicle.brand} ${vehicle.model} • ${vehicle.plateNumber}",
                            fontSize = 17.sp,
                            color = Color(0xFF000000),
                            letterSpacing = (-0.24).sp
                        )
                    }
                }
            }
        }
    }
}

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