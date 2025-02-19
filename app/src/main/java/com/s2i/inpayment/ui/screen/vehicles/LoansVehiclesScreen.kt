package com.s2i.inpayment.ui.screen.vehicles

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.s2i.inpayment.ui.viewmodel.VehiclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansVehiclesScreen(
    navController: NavController,
    vehiclesViewModel: VehiclesViewModel
) {
    val loansVehiclesState by vehiclesViewModel.loansVehiclesState.collectAsState()
    var token by remember { mutableStateOf("") }
    var accountNumer by remember { mutableStateOf("") }
    var isTokenValid by remember { mutableStateOf(true) }
    val isFormValid = token.isNotEmpty()
    val context = LocalContext.current
    val isLoading by vehiclesViewModel.loading.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Loans Vehicles",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token") },
            isError = !isTokenValid,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (!isTokenValid && token.isNotEmpty()) {
                {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
        )
        if (!isTokenValid && token.isNotEmpty()) {
            Text(
                text = "Invalid Token",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                vehiclesViewModel.loansVehicles(token)
                Toast.makeText(context, "Vehicle Loans Successfully", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Submit",
                )
            }
        }
    }
}



