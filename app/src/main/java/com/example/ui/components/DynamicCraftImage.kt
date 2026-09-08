package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.production.MediaRenderContext
import com.example.data.models.production.ProductMedia
import com.example.ui.theme.*

/**
 * =========================================================================
 * DYNAMIC CRAFT ASYNC IMAGE
 * Production-ready dynamic image loader backed by Coil and backend media URLs.
 * Supports progressive loading, context-based resolution (thumbnail, medium, large),
 * shimmer placeholders, and graceful offline/fallback visuals.
 * =========================================================================
 */
@Composable
fun DynamicCraftAsyncImage(
    media: ProductMedia?,
    modifier: Modifier = Modifier,
    context: MediaRenderContext = MediaRenderContext.HOME_GRID,
    fallbackHexColor: Long = 0xFF94442A,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imageUrl = media?.urls?.urlForContext(context)
    DynamicUrlImage(
        imageUrl = imageUrl,
        modifier = modifier,
        fallbackHexColor = fallbackHexColor,
        contentDescription = contentDescription ?: media?.altText ?: "Artisanal Craft",
        contentScale = contentScale
    )
}

@Composable
fun DynamicUrlImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    fallbackHexColor: Long = 0xFF94442A,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .background(Color(fallbackHexColor).copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank() && !isError) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(300)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                onLoading = { isLoading = true },
                onSuccess = {
                    isLoading = false
                    isError = false
                },
                onError = {
                    isLoading = false
                    isError = true
                }
            )
        }

        // Shimmer placeholder during active network load
        if (isLoading && !imageUrl.isNullOrBlank() && !isError) {
            ShimmerEffect(modifier = Modifier.fillMaxSize())
        }

        // Graceful thematic fallback when URL is empty or failed to load
        if (imageUrl.isNullOrBlank() || isError) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(fallbackHexColor).copy(alpha = 0.35f),
                                Color(fallbackHexColor).copy(alpha = 0.75f)
                            )
                        )
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MittiSoilEmblem(
                    modifier = Modifier.size(36.dp),
                    leafTint = MittiSageLight,
                    soilTint = MittiSoilTerracotta,
                    accentTint = MittiRoyalGold
                )
            }
        }
    }
}

@Composable
private fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_trans"
    )

    val shimmerColors = listOf(
        WarmClayNeutral.copy(alpha = 0.4f),
        JuteGoldLight.copy(alpha = 0.8f),
        WarmClayNeutral.copy(alpha = 0.4f)
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Spacer(modifier = modifier.background(brush))
}
