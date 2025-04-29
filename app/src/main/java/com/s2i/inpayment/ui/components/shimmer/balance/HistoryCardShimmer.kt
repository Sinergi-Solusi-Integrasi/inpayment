package com.s2i.inpayment.ui.components.shimmer.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun HistoryCardShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.3f)
                .height(20.dp)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Color.White,
                        shape = MaterialTheme.shapes.small.copy(all = CornerSize(10.dp))
                    )
                    .padding(16.dp)
            ) {
                repeat(3) {
                    ShimmerTransactionItem()
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ShimmerTransactionItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(12.dp)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(12.dp)
                .shimmerEffect()
        )
    }
}

// Extension modifier to apply shimmer
@Composable
fun Modifier.shimmerEffect(): Modifier {
    return this
        .shimmer()
        .background(
            brush = Brush.linearGradient(
                listOf(
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.Gray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 0.6f)
                )
            ),
            shape = RoundedCornerShape(8.dp)
        )
}