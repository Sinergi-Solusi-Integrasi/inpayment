package com.s2i.inpayment.ui.screen.vehicles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.date_time.DateTimeDialog
import com.maxkeppeler.sheets.date_time.models.DateTimeConfig
import com.maxkeppeler.sheets.date_time.models.DateTimeSelection
import com.s2i.common.utils.date.Dates
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendVehiclesScreen(
    navController: NavController,
    vehicleId: String,
    vehiclesViewModel: VehiclesViewModel
) {
    val lendVehiclesState by vehiclesViewModel.lendVehiclesState.collectAsState()
    var accountNumber by remember { mutableStateOf(TextFieldValue("")) }
    var dueDate by remember { mutableStateOf("") }
    var formattedDueDate by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var isNextClicked by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLoading by vehiclesViewModel.loading.collectAsState()

    val selectedDateTime = remember { mutableStateOf<LocalDateTime?>(null) }
    var showDateTimeDialog by remember { mutableStateOf(true) }
    val dialogState = rememberUseCaseState(visible = showDateTimeDialog)

    fun showDateTimePicker() {
        Log.d("DateTimeDialogDebug", "DateTimeDialog triggered")
        showDateTimeDialog = true
    }

    fun formatDisplayDate(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())
        return dateTime.format(formatter)
    }

    fun formatForServer(dateTime: LocalDateTime): String {
        val zonedDateTime = dateTime.atZone(TimeZone.getDefault().toZoneId())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    LaunchedEffect(lendVehiclesState) {
        lendVehiclesState?.data?.token?.let { responseToken ->
            token = responseToken.token
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lend Vehicles",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!isNextClicked) {
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Account Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
            )
        } else if (!isSubmitted) {
            OutlinedTextField(
                value = formattedDueDate,
                onValueChange = {},
                label = { Text("Due Date") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDateTimePicker()
                        Log.d("DateTimeDialogDebug", "OutlinedTextField clicked")
                        Toast.makeText(context, "TextField Clicked", Toast.LENGTH_SHORT).show()
                    }
            )
        } else {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Token",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = token,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Token Options",
                        modifier = Modifier.clickable {
                            val clipboardManager =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Token",
                                lendVehiclesState?.data?.token?.token ?: ""
                            )
                            clipboardManager.setPrimaryClip(clip)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    !isNextClicked -> {
                        showDateTimePicker()
                        isNextClicked = true
                    }
                    !isSubmitted -> {
                        vehiclesViewModel.lendVehicles(vehicleId, accountNumber.text, dueDate)
                        isSubmitted = true
                        Toast.makeText(context, "Vehicle Lent Successfully", Toast.LENGTH_SHORT).show()
                    }
                    else -> navController.navigateUp()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = when {
                    !isNextClicked -> "Next"
                    !isSubmitted -> "Submit"
                    else -> "Done"
                })
            }
        }
    }

    if (showDateTimeDialog) {
        Log.d("DateTimeDialogDebug", "DateTimeDialog is now visible")
        DateTimeDialog(
            state = dialogState,
            selection = DateTimeSelection.DateTime { newDateTime ->
                Log.d("DateTimeDialogDebug", "DateTime selected: $newDateTime")
                selectedDateTime.value = newDateTime
                formattedDueDate = formatDisplayDate(newDateTime)
                dueDate = formatForServer(newDateTime)
                dialogState.hide()
                Toast.makeText(context, "Selected: $formattedDueDate", Toast.LENGTH_SHORT).show()
            },
            config = DateTimeConfig()
        )
    }
}



