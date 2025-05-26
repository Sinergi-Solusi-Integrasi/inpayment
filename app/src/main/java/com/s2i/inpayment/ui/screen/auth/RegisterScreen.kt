package com.s2i.inpayment.ui.screen.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.s2i.common.utils.convert.correctImageOrientation
import com.s2i.inpayment.R
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val canClick = rememberSingleClickHandler()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val imageFormat by authViewModel.imageFormat.collectAsState()
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }

    val scrollState = rememberScrollState()
    var isStartupLoading by remember { mutableStateOf(true) }
    val loadingState by authViewModel.loadingState.collectAsState()
    val focusManager = LocalFocusManager.current

    BackHandler(enabled = true) {
        if (canClick()) {
            scope.launch {
                navController.navigateUp()
            }
        }
    }

    // Initialize bitmap and loading
    LaunchedEffect(Unit) {
        delay(500)
        isStartupLoading = false
    }

    val bitmap = remember(filePath) {
        filePath?.let {
            BitmapFactory.decodeFile(it)
        }
    }

    val correctedBitmap = if (bitmap != null) {
        correctImageOrientation(context, filePath ?: "")
    } else {
        Log.d("RegisterScreen", "Bitmap is null")
        null
    }
    capturedPhoto = correctedBitmap

    // Form states
    var identityNumberState by remember { mutableStateOf(identityNumber ?: "") }
    var nameState by remember { mutableStateOf(name ?: "") }
    var identityError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validation states
    var isValidEmail by remember { mutableStateOf(true) }
    var isValidPassword by remember { mutableStateOf(true) }
    var isPasswordsMatch by remember { mutableStateOf(true) }
    var isUsernameValid by remember { mutableStateOf(true) }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            username.isNotEmpty() && isValidEmail && isValidPassword && isPasswordsMatch && isUsernameValid

    // Form validation
    LaunchedEffect(identityNumberState, nameState) {
        identityError = when {
            identityNumberState.isEmpty() -> null
            identityNumberState.all { it.isDigit() } && identityNumberState.length == 16 -> null
            else -> "KTP/SIM/PASSPORT Number must be 16 digits"
        }

        nameError = when {
            nameState.isEmpty() -> null
            nameState.all { it.isLetter() || it.isWhitespace() } -> null
            else -> "Full Name must only contain letters and spaces"
        }
    }

    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        registerState?.let {
            it.fold(
                onSuccess = {
                    authViewModel.clearIdentityData()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onFailure = { error ->
                    Log.e("RegisterScreen", "Registration error: ${error.message}")
                }
            )
        }
    }

    if (isStartupLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightTeal20),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Account",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (canClick()) {
                            scope.launch {
                                navController.navigateUp()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrightTeal20)
                .padding(paddingValues)
        ) {
            // Image section - Fixed at top
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .zIndex(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                if (capturedPhoto != null) {
                    capturedPhoto?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Identity Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_id_card),
                        contentDescription = "Identity Card Placeholder",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Form section - Scrollable below image with gap
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp) // Space for the fixed image + gap
            ) {
                // Form container with rounded top corners
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = BrightTeal20),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        if (loadingState) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Personal Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                OutlinedTextField(
                                    value = identityNumberState,
                                    onValueChange = { newValue ->
                                        identityNumberState = newValue
                                        if (newValue.all { it.isDigit() } && newValue.length == 16) {
                                            identityError = null
                                        } else if (newValue.isNotEmpty()) {
                                            identityError =
                                                "KTP/SIM/PASSPORT Number must be 16 digits"
                                        } else {
                                            identityError = null
                                        }
                                    },
                                    label = { Text("KTP/SIM/PASSPORT Number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = identityError != null,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Number
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                   shape = RoundedCornerShape(10.dp)
                                )
                                identityError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = nameState,
                                    onValueChange = { newValue ->
                                        nameState = newValue
                                        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
                                            nameError = null
                                        } else if (newValue.isNotEmpty()) {
                                            nameError =
                                                "Full Name must only contain letters and spaces"
                                        } else {
                                            nameError = null
                                        }
                                    },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = nameError != null,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                nameError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Account Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                OutlinedTextField(
                                    value = username,
                                    onValueChange = {
                                        username = it
                                        isUsernameValid = it.length >= 6
                                    },
                                    label = { Text("Username") },
                                    isError = !isUsernameValid,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = if (!isUsernameValid && username.isNotEmpty()) {
                                        {
                                            Icon(
                                                Icons.Default.Error,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    } else null,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                if (!isUsernameValid && username.isNotEmpty()) {
                                    Text(
                                        "Username must be at least 6 characters",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
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
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    } else null,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Email
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                if (!isValidEmail && email.isNotEmpty()) {
                                    Text(
                                        "Please enter a valid email (example@mail.com)",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text("Mobile Phone") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Phone
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Address") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "Security",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

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
                                        IconButton(onClick = {
                                            passwordVisible = !passwordVisible
                                        }) {
                                            Icon(
                                                imageVector = image,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next,
                                        keyboardType = KeyboardType.Password
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                if (!isValidPassword && password.isNotEmpty()) {
                                    Text(
                                        "Password must be at least 8 characters",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
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
                                            Icon(
                                                imageVector = image,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    },
                                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Password
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { keyboardController?.hide() }
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                if (!isPasswordsMatch && confirmPassword.isNotEmpty()) {
                                    Text(
                                        "Passwords do not match",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        capturedPhoto?.let { photo ->
                                            val selectedFormat =
                                                imageFormat ?: Bitmap.CompressFormat.JPEG
                                            authViewModel.register(
                                                name = nameState,
                                                username = username,
                                                password = password,
                                                email = email,
                                                mobileNumber = phoneNumber,
                                                address = address,
                                                identityNumber = identityNumberState,
                                                identityBitmap = photo,
                                                imageFormat = selectedFormat
                                            )
                                        }
                                    },
                                    enabled = isFormValid,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                       "Create account",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}