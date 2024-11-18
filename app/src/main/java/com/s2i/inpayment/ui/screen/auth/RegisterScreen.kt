package com.s2i.inpayment.ui.screen.auth

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var identityNumber by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isValidEmail by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }
    var isPasswordsMatch by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current

    val isFormValid = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            username.isNotEmpty() && isValidEmail && isValidPassword && isPasswordsMatch

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card for Upload Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialTheme.shapes.medium),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = {
                        navController.navigate("kyc_intro_screen") {
                        launchSingleTop = true
                    } }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Upload Icon",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // KTP/SIM/PASSPORT field
            OutlinedTextField(
                value = identityNumber,
                onValueChange = { identityNumber = it },
                label = { Text("KTP/SIM/PASSPORT Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isValidEmail = Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                label = { Text("Email Address") },
                isError = !isValidEmail,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = if (!isValidEmail && email.isNotEmpty()) {
                    { Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red) }
                } else null
            )
            if (!isValidEmail && email.isNotEmpty()) {
                Text("Please enter a valid email", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isValidPassword = it.length >= 8
                    isPasswordsMatch = password == confirmPassword
                },
                label = { Text("Create Password") },
                isError = !isValidPassword,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            if (!isValidPassword && password.isNotEmpty()) {
                Text("Password must be at least 8 characters", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isPasswordsMatch = it == password
                },
                label = { Text("Repeat Password") },
                isError = !isPasswordsMatch,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Default.Visibility
                    else Icons.Default.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            if (!isPasswordsMatch && confirmPassword.isNotEmpty()) {
                Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle registration */ },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Account")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen(){
    RegisterScreen(navController = NavController(context = LocalContext.current))
}
