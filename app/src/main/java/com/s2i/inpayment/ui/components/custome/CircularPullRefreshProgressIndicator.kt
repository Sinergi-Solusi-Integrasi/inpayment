package com.s2i.inpayment.ui.components.custome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.ui.theme.BrightYellow40
import com.s2i.inpayment.ui.theme.DarkTeal21

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CircularPullRefreshProgressIndicator(
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {

    val animatedProgress by animateFloatAsState(
        targetValue = when {
            isRefreshing -> 1f
            else -> pullRefreshState.progress
        },
        animationSpec = tween(durationMillis = 500, easing = LinearEasing), label = "circularProgress"
    )

    Box(
        modifier = Modifier
            .padding(16.dp)
            .width(50.dp)
            .height(50.dp)
    ) {
        CircularProgressIndicator(
            progress = { animatedProgress },
            color = BrightYellow40,
            trackColor = DarkTeal21,
            strokeWidth = 4.dp
        )
    }

}