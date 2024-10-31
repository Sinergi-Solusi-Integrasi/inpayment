package com.s2i.inpayment.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = koinViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginState.observeAsState()
    val loadingState by authViewModel.loadingState.observeAsState(false)
    var passwordVisible by remember { mutableStateOf(false) }
    var isValidUsername by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }

    // define username regex
    val usernamePatterns = "^\\S{4,20}$".toRegex()

    val isFormValid =
        username.isNotEmpty() && password.isNotEmpty() && isValidUsername && isValidPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        //logic loading
        if (loadingState) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                isValidUsername = usernamePatterns.matches(it)
            },
            label = { Text("Username") },
            isError = !isValidUsername,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (!isValidUsername && username.isNotEmpty()) {
                { Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red) }
            } else null
        )
        if (!isValidUsername && username.isNotEmpty()) {
            Text("Username minimu 4 characters", color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isValidPassword = it.length >= 8
            },
            label = { Text("Password") },
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

        Button(
            onClick = {
                authViewModel.login(
                    username,
                    password
                )
            },
            enabled = !loadingState && isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("register_screen") {
                    popUpTo("login_screen") {
                        inclusive = true
                    }
                }
            },
            enabled = !loadingState,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("oops, belum punya akun? daftar sekarang")
        }

        // Handle login state to show error/success
        loginState?.let {
            it.fold(
                onSuccess = {

                },
                onFailure = { error ->
                    Text(text = error.message ?: "Unknown error")
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewLoginScreen(){
//    LoginScreen()
//}

