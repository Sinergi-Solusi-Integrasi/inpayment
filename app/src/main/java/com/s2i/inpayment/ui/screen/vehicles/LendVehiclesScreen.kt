package com.s2i.inpayment.ui.screen.vehicles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign
import java.time.Month
import java.time.format.TextStyle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.s2i.inpayment.ui.components.navigation.rememberSingleClickHandler
import com.s2i.inpayment.ui.theme.BrightTeal
import com.s2i.inpayment.ui.theme.BrightTeal20
import com.s2i.inpayment.ui.theme.DarkGreen
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendVehiclesScreen(
    navController: NavController,
    vehicleId: String,
    vehicleBrand: String,
    vehicleModel: String,
    vehiclePlateNumber: String,
    vehiclesViewModel: VehiclesViewModel,
    onDismissAll: (() -> Unit)? = null
) {
    LaunchedEffect(vehicleId) {
        // Check if we're navigating to a different vehicle than the last one
        vehiclesViewModel.handleVehiclesChange(vehicleId)
    }
    val lendVehiclesState by vehiclesViewModel.lendVehiclesState.collectAsState()
    var showBottomSheet by remember {mutableStateOf(false)}
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val errorState by vehiclesViewModel.error.collectAsState()
    val canClick = rememberSingleClickHandler()

    var accountNumber by remember { mutableStateOf(TextFieldValue("")) }
    var dueDate by remember { mutableStateOf("") }

    // Date picker state variables
    // Inisialisasi tanggal dengan waktu sekarang
    val today = LocalDate.now()
    var selectedStartDate by remember { mutableStateOf(today) }
    var selectedEndDate by remember { mutableStateOf(today.plusDays(6)) }
    var currentMonth by remember { mutableIntStateOf(today.monthValue) }
    var currentYear by remember { mutableIntStateOf(today.year) }

    // Define calendarHighlight color
    val calendarHighlight = DarkGreen.copy(alpha = 0.25f)

    // Calendar visibility state
    var showCalendar by remember { mutableStateOf(false) }

    fun formatDisplayDate(selectedDate: LocalDate): String{
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
        return if (selectedDate == today){
            selectedDate.format(formatter)
        } else {
            "${today.format(formatter)} - ${selectedDate.format(formatter)}"
        }
    }

    var formattedDueDate by remember { mutableStateOf(formatDisplayDate(today)) }   // Default value for demo
    var token by remember { mutableStateOf<String?>(vehiclesViewModel.getTokenForVehicles(vehicleId)) }
    var isTokenExpired by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf("") }
    val expiredAt = lendVehiclesState?.data?.token?.expiredAt
    fun calculateRemainingTime(expiryTimeStr: String?): String {
        if (expiryTimeStr == null) return "00:00:00"

        return try {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val expiryTime = OffsetDateTime.parse(expiryTimeStr, formatter)
            val now = OffsetDateTime.now()

            if (now.isAfter(expiryTime)) {
                isTokenExpired = true
                return "00:00:00"
            }

            val duration = Duration.between(now, expiryTime)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            val seconds = duration.seconds % 60

            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } catch(e: Exception) {
            "00:00:00"
        }
    }
    var screenState by remember(vehicleId) { mutableIntStateOf(if (token.isNullOrEmpty()) 0 else 1) }
    val context = LocalContext.current
    val isLoading by vehiclesViewModel.loading.collectAsState()
    val scope = rememberCoroutineScope()

    val isAccountValid = accountNumber.text.length == 10

    // Colors
    val darkGreen = Color(0xFF1B5E20)
    val lightGrayBackground = Color(0xFFCDCBCB)

    BackHandler(enabled = true) {
        if (screenState == 1 || token != null) {
            showBottomSheet = true
        } else {
            // Navigasi normal jika bukan di token screen
            if (canClick()) {
                scope.launch {
                    navController.navigateUp()
                }
            }
        }
    }

    fun formatForServer(date: LocalDate): String {
        val dateTime = date.atStartOfDay()
        val zonedDateTime = dateTime.atZone(TimeZone.getDefault().toZoneId())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    // Inisialisasi dueDate dengan nilai default
    LaunchedEffect(Unit) {
        dueDate = formatForServer(today)
    }

    // Generate calendar data for current month/years
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val lastDateOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)

    val firstDayOfWeek = when ( firstDayOfMonth.dayOfWeek.value){
        7 -> 0
        else -> firstDayOfMonth.dayOfWeek.value
    }

    // generate days list with nulls for padding
    val calendarDays = mutableListOf<LocalDate?>()
    // add padding at start
    repeat(firstDayOfWeek) {
        calendarDays.add(null)
    }
    // add days of month
//    for (day in firstDayOfMonth.dayOfMonth..lastDateOfMonth.dayOfMonth) {
//        calendarDays.add(LocalDate.of(currentYear, currentMonth, day))
//    }

    for (day in 1..lastDateOfMonth.dayOfMonth) {
        calendarDays.add(LocalDate.of(currentYear, currentMonth, day))
    }

    // Calculate How Much must added for complete last week
    val remainingCells = 7 - (calendarDays.size % 7)
    if (remainingCells < 7 ) {
        repeat(remainingCells){
            calendarDays.add(null)
        }
    }

    // calculate weeks (rows)
    val weeks = calendarDays.chunked(7)

    LaunchedEffect(lendVehiclesState, errorState) {
        lendVehiclesState?.data?.token?.let { responseToken ->
            if (errorState == null) {
                // Update token data
                token = vehiclesViewModel.getTokenForVehicles(vehicleId)

                // Check if token is expired
                isTokenExpired = vehiclesViewModel.isTokenExpired(expiredAt)

                screenState = 1 // Move to token screen
            }
        }

        errorState?.let { errorMessage ->
            Toast.makeText(
                context,
                "Oops! Sepertinya ada yang salah dengan account number-nya",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(token, screenState) {
        if (screenState == 1 && token != null && !isTokenExpired) {
            while (true) {
                lendVehiclesState?.data?.token?.expiredAt?.let { expiredAt ->
                    remainingTime = calculateRemainingTime(expiredAt)

                    // Stop countdown and update UI when token expires
                    if (remainingTime == "00:00:00") {
                        isTokenExpired = true
                    }
                }

                delay(1000) // Update every second
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lend Vehicles",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Fixed: Call handleDismiss function properly
                        if (screenState == 1 || token != null) {
                            showBottomSheet = true
                        } else {
                            // Navigasi normal jika bukan di token screen
                            if (canClick()) {
                                scope.launch {
                                    navController.navigateUp()
                                }
                            }
                        }

                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = DarkGreen
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (screenState == 1) {
                    // Header dengan Expired Token dan Timer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Expired Token",
                            style = MaterialTheme.typography.titleMedium,
                            color = DarkGreen,
                            fontWeight = FontWeight.Medium
                        )

                        // Timer badge dengan background kuning
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFD54F),  // Warna kuning
                            modifier = Modifier.height(32.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = remainingTime,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Vehicle info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$vehicleModel - $vehiclePlateNumber",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = DarkGreen
                            )

                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Account Input Screen
                if (screenState == 0) {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { newValue ->
                            if (newValue.text.length <= 10) {
                                accountNumber = newValue
                            }
                        },
                        placeholder = { Text("Account number") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(20.dp),

                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date picker field
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showCalendar = !showCalendar
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = lightGrayBackground,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = formattedDueDate,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.Gray
                            )
                        }
                    }

                    // Inline calendar view
                    AnimatedVisibility(
                        visible = showCalendar
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ){
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ){
                                // Month and Year with Navigation
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        if (currentMonth == 1) {
                                            currentMonth = 12
                                            currentYear--
                                        } else {
                                            currentMonth--
                                        }
                                    },enabled = !(currentMonth == today.monthValue && currentYear == today.year)) {
                                        Text(
                                            text = "<",
                                            color = if (currentMonth == today.monthValue && currentYear == today.year) Color.Gray.copy(alpha = 0.5f) else DarkGreen
                                        )
                                    }

                                    Text(
                                        text = "${
                                            Month.of(currentMonth)
                                                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                                        } $currentYear",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = DarkGreen
                                    )

                                    IconButton(
                                        onClick = {
                                            if (currentMonth == 12) {
                                                currentMonth = 1
                                                currentYear++
                                            } else {
                                                currentMonth++
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = ">",
                                            color = DarkGreen
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))

                                // Days of week header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (day in listOf("S", "M", "T", "W", "T", "F", "S")) {
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = day,
                                                textAlign = TextAlign.Center,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Calendar grid
                                weeks.forEach { week ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        for (i in 0 until 7) {
                                            val day = if (i < week.size) week[i] else null

                                            // Check if day is before today (in the past)
                                            val isPastDay = day?.isBefore(today) == true

                                            // check if the day is selected date
                                            val isSelectedDate = day?.isEqual(selectedStartDate) == true

                                            // Determine if this day is within the selection range
                                            // For today's selection, only highlight today itself
                                            // For future date selection, highlight the selected date and the next 6 days
                                            val isDayBefore = day != null &&
                                                    !isPastDay &&
                                                    !isSelectedDate &&
                                                    day.isBefore(selectedStartDate)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f) // Setiap kolom memiliki bobot yang sama
                                                    .aspectRatio(1f) // Memastikan kotak berbentuk persegi
                                                    .padding(4.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isSelectedDate -> darkGreen // Tanggal yang dipilih (hijau tua)
                                                            isDayBefore -> calendarHighlight // Tanggal sebelum tanggal yang dipilih
                                                            isPastDay -> Color.LightGray.copy(alpha = 0.3f) // Tanggal lampau (abu-abu)
                                                            else -> Color.Transparent
                                                        }
                                                    )
                                                    .clickable(enabled = day != null && !isPastDay) {
                                                        if (day != null) {
                                                            selectedStartDate = day
                                                            formattedDueDate = if (day == today) {
                                                                formatDisplayDate(selectedStartDate)
                                                            } else {
                                                                formatDisplayDate(selectedStartDate)
                                                            }
                                                            dueDate = formatForServer(selectedStartDate)
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (day != null) {
                                                    Text(
                                                        text = day.dayOfMonth.toString(),
                                                        color = when {
                                                            isSelectedDate-> Color.White
                                                            isPastDay -> Color.Gray
                                                            else -> Color.Black
                                                        },
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                // Token Screen
                else if (screenState == 1) {
                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { },
                        placeholder = { Text("Account number") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color.White,
                            disabledBorderColor = Color.LightGray,
                            disabledTextColor = Color.Gray
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = formattedDueDate,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))


                    // Vehicle info card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Text di tengah secara independen
                                Text(
                                    text = token ?: "", // Fallback to example
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
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
                                                "Token",
                                                lendVehiclesState?.data?.token?.token ?: ""
                                            )
                                            clipboardManager.setPrimaryClip(clip)
                                            Toast.makeText(
                                                context,
                                                "Token disalin!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                    tint = BrightTeal
                                )
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Don't forget copy token",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Button
                Button(
                    onClick = {
                        when (screenState) {
                            0 -> {
                                // Submit lending request
                                vehiclesViewModel.lendVehicles(vehicleId, accountNumber.text, dueDate)
                            }
                            1 -> {
                                // Done
                                if (canClick()) {
                                    scope.launch {
                                        navController.popBackStack()
                                        navController.navigate("intro_vehicle_screen"){
                                            popUpTo("lend_vehicle_screen") {
                                                inclusive = true
                                                saveState = false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    enabled = if (screenState == 0) isAccountValid else true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGreen,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = when (screenState) {
                                0 -> "Submit"
                                else -> "Continue"
                            },
                            fontSize = 16.sp
                        )
                    }
                }
            }

        }
        if(showBottomSheet){
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = bottomSheetState,
                containerColor = BrightTeal20,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(modifier = Modifier.height(16.dp))

                    // Judul
                    Text(
                        text = "Konfirmasi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Apakah Anda yakin ingin keluar? Token yang sudah didapatkan akan hilang",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = DarkGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Cancel
                    Button(
                        onClick = {
                            scope.launch {
                                showBottomSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkGreen
                        )
                    ) {
                        Text(
                            text = "Tidak, Tetap di Halaman Ini",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Confirm
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                showBottomSheet = false
                                navController.navigateUp()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, darkGreen),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = darkGreen
                        )
                    ) {
                        Text(
                            text = "Ya, Keluar",
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

            }
        }
    }
}