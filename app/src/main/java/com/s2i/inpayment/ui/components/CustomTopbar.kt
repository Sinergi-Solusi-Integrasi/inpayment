package com.s2i.inpayment.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    leftIcon: ImageVector,
    title: String,
    rightIcon: ImageVector,
    onLeftIconClick: () -> Unit,
    onRightIconClick: () -> Unit,
    navController: NavController
) {
    TopAppBar(
        title = {
            Text(text = title, fontSize = 18.sp, color = Color.White)
        },
        navigationIcon = {
            IconButton(onClick = onLeftIconClick) {
                Icon(imageVector = leftIcon, contentDescription = "Left Icon", tint = Color.White)
            }
        },
        actions = {
            // Right Icon (e.g., Notification)
            IconButton(onClick = onRightIconClick) {
                Icon(imageVector = rightIcon, contentDescription = "Right Icon", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Profile Picture (optional)
            Image(
                painter = painterResource(id = R.drawable.ic_people), // Replace with your profile image
                contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Gray, shape = CircleShape)
                    .clickable {
                        navController.navigate("profile_screen") {
                            popUpTo("home_screen") { inclusive = false }
                        }
                    }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
