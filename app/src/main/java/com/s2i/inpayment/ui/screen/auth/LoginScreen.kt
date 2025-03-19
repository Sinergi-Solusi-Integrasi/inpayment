package com.s2i.inpayment.ui.screen.auth

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.s2i.data.local.auth.SessionManager
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.permission.hasAllPermissions
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import com.s2i.inpayment.ui.viewmodel.NotificationsViewModel
import com.s2i.inpayment.ui.viewmodel.ServicesViewModel
import com.s2i.inpayment.utils.helper.handleLoginFailer
import com.s2i.inpayment.utils.helper.handleSessionLogout
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel(),
    notificationViewModel: NotificationsViewModel = koinViewModel(),
    servicesViewModel: ServicesViewModel = koinViewModel(),
    sessionManager: SessionManager,
    showExtraInfo: Boolean = true
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager  = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val loginState by authViewModel.loginState.collectAsState()
    val loadingState by authViewModel.loadingState.collectAsState()
    val isLoggedIn = authViewModel.isLoggedIn()
    val bindingState by servicesViewModel.bindingState.collectAsState()
    val errorState by servicesViewModel.errorState.collectAsState()
    var hasNavigated by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var isValidUsername by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }
    var showErrorSheet by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()


    val usernamePatterns = "^\\S{4,20}$".toRegex()
    val isFormValid =
        username.isNotEmpty() && password.isNotEmpty() && isValidUsername && isValidPassword
    Log.d("LoginScreen", "Rendering LoginScreen")
    val context = LocalContext.current

    // Fungsi untuk mengirim token ke server
    fun sendTokenToServer(token: String) {
        val brand = Build.BRAND
        val model = Build.MODEL ?: "Unknown"
        val osType = Build.VERSION.RELEASE ?: "Unknown"
        val platform = "Android"
        val sdkVersion = "Android API ${Build.VERSION.SDK_INT}"
        Log.d("DeviceInfoActivity", "Device Info - Brand: $brand, Model: $model, OS Type: $osType, Platform: $platform, SDK Version: $sdkVersion, Token: $token")

        // Simpan ke SharedPreferences melalui SessionManager

        notificationViewModel.registerDevices(
            brand = brand,
            model = model,
            osType = osType,
            platform = platform,
            sdkVersion = sdkVersion,
            tokenFirebase = token
        )
    }

    // Dapatkan token Firebase dan simpan
    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("LoginScreen", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("LoginScreen", "FCM Registration Token: $token")
            sendTokenToServer(token)
        }
    }

    LaunchedEffect(sessionManager.isLoggedOut) {
        Log.d("LoginScreen", "LaunchedEffect triggered: isLoggedOut=${sessionManager.isLoggedOut}, showErrorSheet=$showErrorSheet")
        if (sessionManager.isLoggedOut && !showErrorSheet) {
            Log.d("LoginScreen", "User is logged out. Waiting for re-login.")
            // Tampilkan sheet tanpa reset navigasi secara langsung
            authViewModel.resetLoginState()
//            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("LoginScreen", "Fetching FCM registration token failed", task.exception)
//                    return@addOnCompleteListener
//                }
//                val token = task.result
//                Log.d("LoginScreen", "FCM Registration Token: $token")
//                sendTokenToServer(token)
//            }
        }
    }

    // Menggunakan snapshotFlow untuk mengelola showErrorSheet
    LaunchedEffect(loginState, sessionManager.isLoggedOut) {
        loginState?.let {
            it.onSuccess {
                if (!hasNavigated) {
                    hasNavigated = true
                    sessionManager.isLoggedOut = false
                    authViewModel.resetLoginState()
                    val allPermissionsGranted = hasAllPermissions(context)
                    when {
                        !allPermissionsGranted -> {
                            // Izin tidak lengkap, navigasi ke permission_screen
                            Log.d("LoginScreen", "Permissions not granted. Navigating to permission screen.")
                            navController.navigate("permission_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        }
                        else -> {
                            // Semua izin sudah diberikan, navigasi ke home_screen
                            Log.d("LoginScreen", "Permissions granted. Navigating to home screen.")
                            sessionManager.isLoggedOut = false
                            navController.navigate("home_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        }
                    }
                }
            }.onFailure { error ->
                handleLoginFailer(
                    error = error,
                    showErrorSheet = showErrorSheet,
                    setShowErrorSheet = { showErrorSheet = it },
                    setErrorMessage = { errorMessage = it }
                )
                authViewModel.resetLoginState()
                Log.d("onFailure", "Login failed. Showing error sheet: ${authViewModel.resetLoginState()}")
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            if (loadingState) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    isValidUsername = usernamePatterns.matches(it)
                },
                label = { Text("Username") },
                isError = !isValidUsername,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                trailingIcon = if (!isValidUsername && username.isNotEmpty()) {
                    { Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red) }
                } else null
            )
            if (!isValidUsername && username.isNotEmpty()) {
                Text("Username minimal 4 karakter", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isValidPassword = it.length >= 6
                },
                label = { Text("Password") },
                isError = !isValidPassword,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
            )
            if (!isValidPassword && password.isNotEmpty()) {
                Text("Password minimal 8 karakter", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    authViewModel.login(username, password)
                },
                enabled = !loadingState && isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Forgot Password
            TextButton(
                onClick = {

                }
            ) {
                Text(
                    "Forgotten Password ?",
                    color = Color.Blue,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("kyc_intro_screen") {
                        popUpTo("login_screen") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                enabled = !loadingState,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // Hindari ketutupan navigation bar
                    .imePadding() // Hindari ketutupan keyboard
            ) {
                Text("Create new account")
            }
            if (showExtraInfo) {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Text("Powered by", fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.intracts_logo), // Ganti dengan logo yang sesuai
                        contentDescription = "Intracs Logo",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    if (showErrorSheet) {
        Log.d("ReusableBottomSheet", "Displaying bottom sheet with message: $errorMessage")
        ReusableBottomSheet(
            imageRes = R.drawable.ic_errors,
            message = errorMessage,
            sheetState = sheetState,
            onDismiss = {
                Log.d("ReusableBottomSheet", "Bottom sheet dismissed")
                coroutineScope.launch {
                    sheetState.hide()
                    showErrorSheet = false
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen(
){
    val navController = rememberNavController()
    val sessionManager = SessionManager(LocalContext.current)
    LoginScreen(navController = navController, sessionManager = sessionManager)
}

