package com.s2i.inpayment.ui.components.shimmer.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun DetailTrxCardShimmer() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shimmer(shimmerInstance),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(
                modifier = Modifier
                    .height(24.dp)
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(
                modifier = Modifier
                    .height(28.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            repeat(6) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(
                        modifier = Modifier
                            .height(14.dp)
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.LightGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.4f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray)
                )
                Spacer(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth(0.4f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(
                modifier = Modifier
                    .height(36.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
        }
    }
}
