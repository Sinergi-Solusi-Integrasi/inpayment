package com.s2i.inpayment.ui.components.shimmer.profile

import androidx.compose.foundation.background
import com.valentinilk.shimmer.shimmer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun ProfileCardShimmer() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shimmer(shimmerInstance),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(120.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(180.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(16.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kendaraan
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
