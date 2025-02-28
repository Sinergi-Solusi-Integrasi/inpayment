package com.s2i.inpayment.ui.components.custome

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.ui.components.custome.effectcolor.RaysColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LogoIndicator(
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    val textMeasurer = rememberTextMeasurer()
    val animatedOffset by animateDpAsState(
        targetValue = when {
            isRefreshing -> 200.dp
            pullRefreshState.progress in 0f..1f -> (pullRefreshState.progress * 200).dp
            pullRefreshState.progress > 1f -> (200 + (((pullRefreshState.progress - 1f) * .1f) * 200)).dp
            else -> 0.dp
        }, label = "animatedOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .offset(y = (-200).dp)
            .offset{ IntOffset(0, animatedOffset.roundToPx()) }
    ) {
        LogoWithBeam(isRefreshing, pullRefreshState)
        RaysColor(isRefreshing)
    }

    // willrefresh
    val willRefresh by remember {
        derivedStateOf {
            pullRefreshState.progress > 1f
        }
    }

    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(willRefresh) {
        when {
            willRefresh -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(70)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(100)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            !isRefreshing && pullRefreshState.progress > 0f -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }
}
