package com.s2i.inpayment.ui.components.custome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogoWithBeam(
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display the uploaded logo image
        Image(
            painter = painterResource(id = R.drawable.logo), // replace `logo_image` with the actual file name
            contentDescription = "Logo with Beam",
            modifier = Modifier.size(80.dp) // Adjust size as needed
        )
    }
}
