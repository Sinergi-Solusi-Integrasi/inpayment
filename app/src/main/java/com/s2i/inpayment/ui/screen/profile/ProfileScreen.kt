package com.s2i.inpayment.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.DarkTeal40
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        /* Handle back navigation */
                        navController.navigateUp()
                    }
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = DarkTeal40,
                    navigationIconContentColor = DarkTeal21
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            ProfileCard( navController,
                authViewModel,
                scope)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileMenu()
            Spacer(modifier = Modifier.height(16.dp))
            ProfileFooter()
        }
    }
}

@Composable
fun ProfileCard(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    scope: CoroutineScope
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                        text = "Hai, Bambang Prasongko",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "0817-0777-696",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "RFID: 9360181806061997",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Handle logout */
                    scope.launch {
                        // clear the session
                        authViewModel.logout()
                        // navigate to login screen
                        navController.navigate("login_screen") {
                            launchSingleTop = true
                        }
                    }
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sedan Merah   GOLONGAN 1   Plat Hitam",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle edit profile */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Edit Profile")
                }
                Button(
                    onClick = { /* Handle data account */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Text("Data Account")
                }
            }
        }
    }
}

@Composable
fun ProfileMenu() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileMenuItem(icon = R.drawable.ic_riwayat, title = "All Transactions")
        ProfileMenuItem(icon = R.drawable.ic_help, title = "Help and Support")
    }
}

@Composable
fun ProfileFooter() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfileMenuItem(icon = R.drawable.ic_about, title = "About Us")
        ProfileMenuItem(icon = R.drawable.ic_terms, title = "Terms and Conditions")
        ProfileMenuItem(icon = R.drawable.ic_feedback, title = "Feedback")
    }
}

@Composable
fun ProfileMenuItem(icon: Int, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    ProfileScreen(navController = NavController(LocalContext.current), authViewModel = AuthViewModel(LocalContext.current))
//}
