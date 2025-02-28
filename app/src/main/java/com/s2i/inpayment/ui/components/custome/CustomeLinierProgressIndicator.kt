package com.s2i.inpayment.ui.components.custome

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.s2i.inpayment.ui.theme.BrightYellow40
import com.s2i.inpayment.ui.theme.DarkTeal21
import com.s2i.inpayment.ui.theme.gradientBrush
import com.s2i.inpayment.ui.theme.triGradientBrush

@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progressBrush: Brush = triGradientBrush(),
    backgroundColor: Color = DarkTeal21,
    clipShape: Shape = RoundedCornerShape(16.dp),
    animationDuration: Int = 1500
) {
    // Animate the progress to create a looping effect
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Progressbar"
    )

    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressBrush)
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
        )
    }
}

@Preview
@Composable
fun CustomLinearProgressIndicatorPreview(){
    CustomLinearProgressIndicator()
}