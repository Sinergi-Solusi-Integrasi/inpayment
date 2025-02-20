package com.s2i.inpayment.ui.components.custome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.triGradientBrush

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LinierPullRefreshProgressIndicator(
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState,
) {

    // Progress Animation
    val animatedProgress by animateFloatAsState(
        targetValue = when {
            isRefreshing -> 1f
            else -> pullRefreshState.progress
        },
        animationSpec = tween(durationMillis = 500, easing = LinearEasing), label = "animatedProgress"
    )

    Box (
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkTeal21)
            .height(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(triGradientBrush())
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
        )
    }
}