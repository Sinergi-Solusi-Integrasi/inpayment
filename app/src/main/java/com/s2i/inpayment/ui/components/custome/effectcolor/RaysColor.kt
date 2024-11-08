package com.s2i.inpayment.ui.components.custome.effectcolor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.s2i.inpayment.ui.theme.White40
import com.s2i.inpayment.ui.theme.gradientBrush

@Composable
fun RaysColor(
    isRefreshing: Boolean,
) {
    val rayLength by animateFloatAsState(
        targetValue = when {
            isRefreshing -> 1f
            else -> 0f
        },
        visibilityThreshold = .000001f,
        animationSpec = when {
            isRefreshing -> tween(2_00, easing = LinearEasing)
            else -> tween(300, easing = LinearEasing)
        }, label = "rayLenght"
    )

    val phase = remember { Animatable(0f) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            var target = 1
            while (true) {
                phase.animateTo(
                    target.toFloat(),
                    animationSpec = tween(3_000, easing = LinearEasing)
                )
                target++
            }
        } else{
            phase.animateTo(0f)
        }
    }

    var canvasSize by remember { mutableStateOf(Size.Zero) }

    val rays by remember {
        derivedStateOf {
            val rayMeasure = PathMeasure()
            buildList {
                for (i in 1..7) {
                    val ray = Path()
                    ray.moveTo(
                        canvasSize.center.x, canvasSize.center.y + (5f * i) - 10f
                    )
                    ray.lineTo(
                        canvasSize.width * .2f, canvasSize.center.y + (10f * i) - 20f
                    )
                    ray.relativeLineTo(
                        canvasSize.width * -0.4f, (100f * (i - 4))
                    )
                    rayMeasure.setPath(ray, false)
                    add(Pair(ray, rayMeasure.length))
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        canvasSize = size
        rays.forEachIndexed { index, (ray, length) ->
            drawPath(
                path = ray,
                color = White40,
                style = Stroke(
                    width = 10f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.chainPathEffect(
                        PathEffect.dashPathEffect(
                            intervals = floatArrayOf(20f, 30f),
                            phase = length * -phase.value
                        ),
                        PathEffect.chainPathEffect(
                            PathEffect.dashPathEffect(
                                intervals = floatArrayOf(length * rayLength, length)
                            ),
                            PathEffect.cornerPathEffect(200f),
                        )
                    )
                )
            )
        }
    }
}