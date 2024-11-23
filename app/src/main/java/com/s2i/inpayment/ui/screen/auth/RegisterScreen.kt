package com.s2i.inpayment.ui.screen.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.asImageBitmap
import com.s2i.common.utils.convert.decodeBase64ToBitmap
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.ReusableBottomSheet
import com.s2i.inpayment.ui.components.ReusableBottomSheetScaffold
import com.s2i.inpayment.ui.theme.White30
import com.s2i.inpayment.ui.theme.White40
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    navController: NavController,
    identityNumber: String? = null,
    name: String? = null,
    filePath: String? = null
) {
    // Collecting states from ViewModel
    val base64s by authViewModel.base64Data.collectAsState()
    val identityBitmap by authViewModel.identityBitmap.collectAsState()
    val imageFormat by authViewModel.imageFormat.collectAsState()
    val mimeType by authViewModel.mimeType.collectAsState()
    val ext by authViewModel.ext.collectAsState()


    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberScrollState()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loading by authViewModel.loadingState.observeAsState(false)


    // Use LaunchedEffect to control startup loading and fetch identityBitmap
    // Use LaunchedEffect to control startup loading and fetch identityBitmap
    LaunchedEffect(Unit) {
        // Simulate fetching bitmap
        if (loading && isStartupLoading) {
            isStartupLoading = true
        } else if (!loading) {
            delay(500) // Optional delay to keep the loading indicator visible for a short time
            isStartupLoading = false
        }
    }

    val bitmap = remember(filePath) {
        filePath?.let {
            BitmapFactory.decodeFile(it)
        }
    }

    LaunchedEffect(identityBitmap) {
        identityBitmap?.let {
            Log.d("RegisterScreen", "New identity bitmap received, updating UI")
        }
    }


// Logging for debugging
    LaunchedEffect(identityBitmap, imageFormat, mimeType, ext) {
        Log.d("RegisterScreen", "base64: $base64s")
        Log.d("RegisterScreen", "identityBitmap: $identityBitmap")
        Log.d("RegisterScreen", "imageFormat: $imageFormat")
        Log.d("RegisterScreen", "mimeType: $mimeType")
        Log.d("RegisterScreen", "ext: $ext")
    }

    var identityNumberState by remember { mutableStateOf(identityNumber?: "") }
    var nameState by remember { mutableStateOf(name?: "") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isValidEmail by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }
    var isPasswordsMatch by remember { mutableStateOf(true) }
    val isFormValid = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            username.isNotEmpty() && isValidEmail && isValidPassword && isPasswordsMatch


//    val bitmap = remember { decodeBase64ToBitmap(base64Image) }
    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        registerState?.let {
            it.fold(
                onSuccess = {
                    authViewModel.clearIdentityData() // Bersihkan data setelah digunakan
                    navController.navigate("login_screen")
                },
                onFailure = { error ->
                    Log.e("RegisterScreen", "Registration error: ${error.message}")
                }
            )
        }
    }

    // Warna untuk TopAppBar
    val containerColor = White30
    val titleContentColor = MaterialTheme.colorScheme.primary

    // Sinkronisasi animasi antara BottomSheet dan TopAppBar
//    LaunchedEffect(sheetState) {
//        snapshotFlow { sheetState.currentValue }.collect { currentValue ->
//            if (currentValue == SheetValue.Expanded) {
//                scrollBehavior.state.heightOffset = -200f // Menyesuaikan ketinggian saat animasi
//            } else {
//                scrollBehavior.state.heightOffset = 0f
//            }
//        }
//    }
    val appBarColor by animateColorAsState(
        targetValue = if (sheetState.currentValue == SheetValue.Expanded) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.primary
        }
    )


    // Offset animasi berdasarkan currentValue
    val animatedOffset by animateFloatAsState(
        targetValue = if (sheetState.currentValue == SheetValue.Expanded) {
            -200f
        } else {
            0f
        }
    )


    val nestedScrollConnection = scrollBehavior.nestedScrollConnection
    // Jika masih dalam loading, tampilkan indikator loading
    if (isStartupLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(White30),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    } else {
        ReusableBottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = 300.dp,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .nestedScroll(nestedScrollConnection)
                        .fillMaxHeight(0.89f)
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    OutlinedTextField(
                        value = identityNumberState,
                        onValueChange = { identityNumberState = it },
                        label = { Text("KTP/SIM/PASSPORT Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameState,
                        onValueChange = { nameState = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

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
                            {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        } else null
                    )
                    if (!isValidEmail && email.isNotEmpty()) {
                        Text("Please enter a valid email", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Mobile Phone") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

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
                        Text(
                            "Password must be at least 8 characters",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

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
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    )
                    if (!isPasswordsMatch && confirmPassword.isNotEmpty()) {
                        Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            bitmap?.let { bitmap ->
                                authViewModel.register(
                                    name = nameState,
                                    username = username,
                                    password = password,
                                    email = email,
                                    address = address,
                                    identityNumber = identityNumberState,
                                    mobileNumber = phoneNumber,
                                    identityBitmap = bitmap,
                                    imageFormat = imageFormat
                                        ?: Bitmap.CompressFormat.JPEG // Default jika null
                                )
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Account")
                    }
                }
            },
            topBar = {
                MediumTopAppBar(
                    modifier = Modifier.offset { IntOffset(0, animatedOffset.toInt()) },
                    title = {
                        Text(
                            "Add your card or account to the wallet",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = containerColor,
                        titleContentColor = titleContentColor
                    ),
                    scrollBehavior = scrollBehavior
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(White30)
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Displaying the identity bitmap image
                    bitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen(){
    RegisterScreen(
        navController = NavController(context = LocalContext.current),
        identityNumber = "1234567890123456",
        name = "John Doe",
    )
}