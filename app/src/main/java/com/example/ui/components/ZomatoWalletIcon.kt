package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.WalletState

/**
 * Zomato-Style Custom Vector Wallet Icon
 * Features the signature bi-fold wallet design with gold card/coin peeking out,
 * metallic clasp detail, and refined shadows.
 */
@Composable
fun ZomatoWalletIcon(
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFFE23744), // Iconic Zomato crimson or theme tone
    accentColor: Color = Color(0xFFFFC107),  // Gold card / coin
    hasCashback: Boolean = true
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // 1. Peeking Credit Card / Cash Notes at the Top Fold
        val cardW = w * 0.55f
        val cardH = h * 0.28f
        val cardLeft = cx - cardW * 0.45f
        val cardTop = h * 0.12f

        // Second peeking card (emerald / gold)
        drawRoundRect(
            color = Color(0xFF2E7D32),
            topLeft = Offset(cardLeft + 4f, cardTop - 3f),
            size = Size(cardW * 0.9f, cardH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Primary gold card
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
            ),
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Card chip/line
        drawLine(
            color = Color(0xFFFFE082),
            start = Offset(cardLeft + 4f, cardTop + cardH * 0.4f),
            end = Offset(cardLeft + cardW - 4f, cardTop + cardH * 0.4f),
            strokeWidth = 1.5f
        )

        // 2. Main Wallet Body (Smooth Rounded Rectangle)
        val bodyW = w * 0.76f
        val bodyH = h * 0.62f
        val bodyLeft = cx - bodyW / 2f
        val bodyTop = h * 0.28f

        // Wallet Back/Front Leather
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(primaryColor, primaryColor.copy(alpha = 0.88f))
            ),
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(7f, 7f)
        )

        // Subtle wallet stitching line
        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(bodyLeft + 2.5f, bodyTop + 2.5f),
            size = Size(bodyW - 5f, bodyH - 5f),
            cornerRadius = CornerRadius(5f, 5f),
            style = Stroke(width = 1f)
        )

        // 3. Wallet Flap / Pocket Fold (Horizontal seam)
        val seamY = bodyTop + bodyH * 0.32f
        drawLine(
            color = Color.Black.copy(alpha = 0.20f),
            start = Offset(bodyLeft, seamY),
            end = Offset(bodyLeft + bodyW, seamY),
            strokeWidth = 2f
        )

        // 4. Clasp Tab on Right
        val claspW = w * 0.22f
        val claspH = h * 0.26f
        val claspLeft = bodyLeft + bodyW - claspW * 0.65f
        val claspTop = bodyTop + bodyH * 0.34f

        drawRoundRect(
            color = Color(0xFFB71C1C), // Deep leather contrast for flap
            topLeft = Offset(claspLeft, claspTop),
            size = Size(claspW, claspH),
            cornerRadius = CornerRadius(5f, 5f)
        )

        // Gold Metallic Snap Button / Coin Stud
        val studCenter = Offset(claspLeft + claspW * 0.45f, claspTop + claspH / 2f)
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 3.5f,
            center = studCenter
        )
        drawCircle(
            color = Color(0xFFF57F17),
            radius = 2f,
            center = studCenter
        )

        // Optional micro Rupee / Dot Indicator on Left
        if (hasCashback) {
            drawCircle(
                color = Color(0xFF4CAF50),
                radius = 2.5f,
                center = Offset(bodyLeft + 6f, bodyTop + bodyH - 6f)
            )
        }
    }
}

/**
 * Sleek & Minimal Zomato-Style Header Wallet Icon Button
 * Pure icon with transparent outer area and inherited gradient styling.
 */
@Composable
fun ZomatoStyleWalletHeaderButton(
    onClick: () -> Unit,
    gradientStart: Color = Color(0xFFC2185B),
    gradientEnd: Color = Color(0xFFE91E63),
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                listOf(
                    gradientStart.copy(alpha = 0.70f),
                    gradientEnd.copy(alpha = 0.45f)
                )
            )
        ),
        shadowElevation = 0.dp,
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable {
                hapticFeedback.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onClick()
            }
            .testTag("top_bar_wallet_button")
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Sleek Zomato-style Wallet Vector Icon
            ZomatoWalletIcon(
                modifier = Modifier.size(24.dp),
                primaryColor = gradientStart, // Inherits vibrant gradient theme tone
                accentColor = Color(0xFFFFC107),
                hasCashback = true
            )
        }
    }
}
