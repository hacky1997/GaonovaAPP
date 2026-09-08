package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * High-fidelity artisan visual renderer that authentically represents each uploaded product
 * with custom crafted vector/canvas illustrations and ambient lighting.
 */
@Composable
fun CraftArtworkView(
    productId: String,
    modifier: Modifier = Modifier,
    isHero: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            productId.contains("lantern", ignoreCase = true) -> {
                PatachitraLanternCanvas(modifier = Modifier.fillMaxSize(), isHero = isHero)
            }
            productId.contains("bottle", ignoreCase = true) -> {
                PaintedSteelBottlesCanvas(modifier = Modifier.fillMaxSize(), isHero = isHero)
            }
            productId.contains("owl", ignoreCase = true) || productId.contains("pecha", ignoreCase = true) -> {
                NatungramWoodenOwlCanvas(modifier = Modifier.fillMaxSize(), isHero = isHero)
            }
            productId.contains("dokra", ignoreCase = true) || productId.contains("durga", ignoreCase = true) -> {
                DokraDurgaIdolCanvas(modifier = Modifier.fillMaxSize(), isHero = isHero)
            }
            productId.contains("plate", ignoreCase = true) || productId.contains("shorai", ignoreCase = true) -> {
                PatachitraWallPlatesCanvas(modifier = Modifier.fillMaxSize(), isHero = isHero)
            }
            productId.contains("kantha", ignoreCase = true) -> {
                KanthaWeaveCanvas(modifier = Modifier.fillMaxSize())
            }
            productId.contains("pottery", ignoreCase = true) -> {
                BluePotteryCanvas(modifier = Modifier.fillMaxSize())
            }
            productId.contains("attar", ignoreCase = true) -> {
                MittiAttarCanvas(modifier = Modifier.fillMaxSize())
            }
            productId.contains("horse", ignoreCase = true) -> {
                BankuraHorseCanvas(modifier = Modifier.fillMaxSize())
            }
            else -> {
                GenericArtisanCanvas(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

/** 1. Hand-Painted Bengali Patachitra Metal Lantern (Pink Enamel + Painted Glass) */
@Composable
private fun PatachitraLanternCanvas(modifier: Modifier = Modifier, isHero: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "lanternGlow")
    val candleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "candleFlicker"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Ambient background glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = 0.35f * candleAlpha),
                    Color(0xFFE91E63).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy * 0.95f),
                radius = h * 0.55f
            )
        )

        // Carrying Ring Handle on Top
        val ringRadius = h * 0.11f
        drawCircle(
            color = Color(0xFFD81B60),
            radius = ringRadius,
            center = Offset(cx, h * 0.16f),
            style = Stroke(width = 5f)
        )

        // Chimney Top Dome & Vent
        val domeWidth = w * 0.32f
        val domeHeight = h * 0.10f
        drawRoundRect(
            color = Color(0xFFAD1457),
            topLeft = Offset(cx - domeWidth / 2f, h * 0.22f),
            size = Size(domeWidth, domeHeight),
            cornerRadius = CornerRadius(14f, 14f)
        )
        // Vent slits
        drawLine(
            color = Color(0xFFFF80AB),
            start = Offset(cx - domeWidth * 0.3f, h * 0.27f),
            end = Offset(cx + domeWidth * 0.3f, h * 0.27f),
            strokeWidth = 3f
        )

        // Main Lantern Body Hood (Vibrant Magenta Pink)
        val hoodPath = Path().apply {
            moveTo(cx - w * 0.26f, h * 0.36f)
            lineTo(cx + w * 0.26f, h * 0.36f)
            lineTo(cx + w * 0.20f, h * 0.31f)
            lineTo(cx - w * 0.20f, h * 0.31f)
            close()
        }
        drawPath(hoodPath, color = Color(0xFFC2185B))

        // Side Wire Guard Struts (Curved outer cage wires)
        drawArc(
            color = Color(0xFFE91E63),
            startAngle = 100f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.38f, h * 0.33f),
            size = Size(w * 0.76f, h * 0.44f),
            style = Stroke(width = 4f)
        )

        // Glass Chamber (Illuminated Central Window)
        val glassLeft = cx - w * 0.20f
        val glassTop = h * 0.36f
        val glassW = w * 0.40f
        val glassH = h * 0.38f

        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(
                    Color(0xFFFFF9C4).copy(alpha = 0.9f),
                    Color(0xFFFFECB3).copy(alpha = 0.95f),
                    Color(0xFFFFE082).copy(alpha = 0.9f)
                )
            ),
            topLeft = Offset(glassLeft, glassTop),
            size = Size(glassW, glassH),
            cornerRadius = CornerRadius(12f, 12f)
        )
        drawRoundRect(
            color = Color(0xFFD81B60),
            topLeft = Offset(glassLeft, glassTop),
            size = Size(glassW, glassH),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(width = 3.5f)
        )

        // Hand-painted Patachitra Figure Silhouettes on the Glass (Radha-Krishna / Folk motif)
        // Figure 1 (Krishna in Blue/Teal)
        drawCircle(
            color = Color(0xFF0288D1),
            radius = glassW * 0.12f,
            center = Offset(cx - glassW * 0.22f, glassTop + glassH * 0.30f)
        )
        // Peacock feather crown
        drawLine(
            color = Color(0xFF00796B),
            start = Offset(cx - glassW * 0.22f, glassTop + glassH * 0.20f),
            end = Offset(cx - glassW * 0.30f, glassTop + glassH * 0.12f),
            strokeWidth = 3f
        )
        // Flute
        drawLine(
            color = Color(0xFFFFD600),
            start = Offset(cx - glassW * 0.35f, glassTop + glassH * 0.38f),
            end = Offset(cx + glassW * 0.05f, glassTop + glassH * 0.32f),
            strokeWidth = 3f
        )

        // Figure 2 (Radha in Red/Yellow)
        drawCircle(
            color = Color(0xFFD32F2F),
            radius = glassW * 0.11f,
            center = Offset(cx + glassW * 0.20f, glassTop + glassH * 0.32f)
        )
        // Dupatta arch
        drawArc(
            color = Color(0xFFFFC107),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx + glassW * 0.08f, glassTop + glassH * 0.18f),
            size = Size(glassW * 0.26f, glassH * 0.22f),
            style = Stroke(width = 3f)
        )

        // Folk vine floral border inside glass
        drawCircle(color = Color(0xFFE91E63), radius = 4f, center = Offset(cx, glassTop + glassH * 0.65f))
        drawCircle(color = Color(0xFF4CAF50), radius = 3.5f, center = Offset(cx - 10f, glassTop + glassH * 0.67f))
        drawCircle(color = Color(0xFF4CAF50), radius = 3.5f, center = Offset(cx + 10f, glassTop + glassH * 0.67f))

        // Candle flame inside
        drawOval(
            color = Color(0xFFFF6F00).copy(alpha = candleAlpha),
            topLeft = Offset(cx - 5f, glassTop + glassH * 0.72f),
            size = Size(10f, 18f)
        )

        // Sturdy Lantern Base (Pink Enamel)
        val baseW = w * 0.48f
        val baseH = h * 0.14f
        drawRoundRect(
            color = Color(0xFF880E4F),
            topLeft = Offset(cx - baseW / 2f, h * 0.74f),
            size = Size(baseW, baseH),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Base decorative rim
        drawLine(
            color = Color(0xFFFF4081),
            start = Offset(cx - baseW * 0.45f, h * 0.79f),
            end = Offset(cx + baseW * 0.45f, h * 0.79f),
            strokeWidth = 3f
        )
    }
}

/** 2. Hand-Painted Patachitra Stainless Steel Water Bottles (Set of 4) */
@Composable
private fun PaintedSteelBottlesCanvas(modifier: Modifier = Modifier, isHero: Boolean) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cy = h / 2f

        // Coaster / Wooden base tray
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF8D6E63), Color(0xFF5D4037), Color(0xFF8D6E63))
            ),
            topLeft = Offset(w * 0.08f, h * 0.82f),
            size = Size(w * 0.84f, h * 0.10f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // 4 Bottles in Array: Colors: Green (Tree of Life), Crimson (Mayur), Deep Blue (Fish), Ochre (Folk Dancer)
        val bottleWidth = w * 0.17f
        val bottleHeight = h * 0.58f
        val startX = w * 0.12f
        val gap = w * 0.19f

        val bottleConfigs = listOf(
            Triple(Color(0xFF2E7D32), Color(0xFF81C784), "TREE"),
            Triple(Color(0xFFC2185B), Color(0xFFFF80AB), "MAYUR"),
            Triple(Color(0xFF0D47A1), Color(0xFF64B5F6), "FISH"),
            Triple(Color(0xFFE65100), Color(0xFFFFB74D), "DANCE")
        )

        bottleConfigs.forEachIndexed { index, (primaryColor, accentColor, motif) ->
            val bx = startX + index * gap
            val by = h * 0.24f

            // Steel Cap & Neck
            drawRoundRect(
                color = Color(0xFFCFD8DC),
                topLeft = Offset(bx + bottleWidth * 0.28f, by - h * 0.06f),
                size = Size(bottleWidth * 0.44f, h * 0.07f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Bottle Body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(primaryColor, primaryColor.copy(alpha = 0.85f))
                ),
                topLeft = Offset(bx, by),
                size = Size(bottleWidth, bottleHeight),
                cornerRadius = CornerRadius(10f, 10f)
            )

            // Steel Rim Accent at Top & Bottom
            drawLine(
                color = Color(0xFFECEFF1),
                start = Offset(bx + 4f, by + 4f),
                end = Offset(bx + bottleWidth - 4f, by + 4f),
                strokeWidth = 2.5f
            )

            // Hand-Painted Folk Motif Detail
            val mcx = bx + bottleWidth / 2f
            val mcy = by + bottleHeight * 0.45f

            when (motif) {
                "TREE" -> {
                    // Tree of life trunk & leaves
                    drawLine(
                        color = Color(0xFFFFD54F),
                        start = Offset(mcx, mcy + 16f),
                        end = Offset(mcx, mcy - 16f),
                        strokeWidth = 3f
                    )
                    drawCircle(color = accentColor, radius = 5f, center = Offset(mcx - 6f, mcy - 8f))
                    drawCircle(color = accentColor, radius = 5f, center = Offset(mcx + 6f, mcy - 8f))
                    drawCircle(color = Color(0xFFFFD54F), radius = 4f, center = Offset(mcx, mcy - 18f))
                }
                "MAYUR" -> {
                    // Peacock head & fan
                    drawCircle(color = Color(0xFF00E5FF), radius = 5f, center = Offset(mcx, mcy - 10f))
                    drawArc(
                        color = accentColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(mcx - 10f, mcy - 18f),
                        size = Size(20f, 16f)
                    )
                }
                "FISH" -> {
                    // Bengal sacred fish
                    drawOval(
                        color = Color(0xFFFFD54F),
                        topLeft = Offset(mcx - 10f, mcy - 6f),
                        size = Size(20f, 12f)
                    )
                    drawCircle(color = Color.Black, radius = 2f, center = Offset(mcx - 5f, mcy))
                    // Fin
                    drawLine(
                        color = accentColor,
                        start = Offset(mcx + 10f, mcy),
                        end = Offset(mcx + 15f, mcy - 4f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = accentColor,
                        start = Offset(mcx + 10f, mcy),
                        end = Offset(mcx + 15f, mcy + 4f),
                        strokeWidth = 2f
                    )
                }
                "DANCE" -> {
                    // Santhal folk dancer
                    drawCircle(color = Color(0xFFFFEB3B), radius = 4.5f, center = Offset(mcx, mcy - 12f))
                    drawLine(
                        color = Color.White,
                        start = Offset(mcx, mcy - 6f),
                        end = Offset(mcx, mcy + 10f),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(mcx - 8f, mcy),
                        end = Offset(mcx + 8f, mcy - 4f),
                        strokeWidth = 2f
                    )
                }
            }

            // Decorative bottom alpana dots
            drawCircle(color = Color.White, radius = 2f, center = Offset(mcx - 6f, by + bottleHeight - 12f))
            drawCircle(color = Color.White, radius = 2f, center = Offset(mcx, by + bottleHeight - 12f))
            drawCircle(color = Color.White, radius = 2f, center = Offset(mcx + 6f, by + bottleHeight - 12f))
        }
    }
}

/** 3. Natungram Hand-Carved Wooden Owl (Lokkhi Pecha) */
@Composable
private fun NatungramWoodenOwlCanvas(modifier: Modifier = Modifier, isHero: Boolean) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Warm timber aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFB300).copy(alpha = 0.28f),
                    Color(0xFFE65100).copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = h * 0.5f
            )
        )

        // Carved Wood Stand Base
        drawRoundRect(
            color = Color(0xFF3E2723),
            topLeft = Offset(cx - w * 0.28f, h * 0.80f),
            size = Size(w * 0.56f, h * 0.10f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Main Carved Wood Owl Body Silhouette (Gamhar Wood with Orange-Red Ochre)
        val bodyW = w * 0.46f
        val bodyH = h * 0.60f
        val bodyTop = h * 0.22f

        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFF57C00), Color(0xFFD84315), Color(0xFFBF360C))
            ),
            topLeft = Offset(cx - bodyW / 2f, bodyTop),
            size = Size(bodyW, bodyH),
            cornerRadius = CornerRadius(bodyW * 0.35f, bodyW * 0.35f)
        )

        // Top Crown / Ears of the Owl
        val leftEar = Path().apply {
            moveTo(cx - bodyW * 0.42f, bodyTop + 10f)
            lineTo(cx - bodyW * 0.32f, bodyTop - h * 0.08f)
            lineTo(cx - bodyW * 0.16f, bodyTop)
            close()
        }
        drawPath(leftEar, color = Color(0xFFD84315))

        val rightEar = Path().apply {
            moveTo(cx + bodyW * 0.42f, bodyTop + 10f)
            lineTo(cx + bodyW * 0.32f, bodyTop - h * 0.08f)
            lineTo(cx + bodyW * 0.16f, bodyTop)
            close()
        }
        drawPath(rightEar, color = Color(0xFFD84315))

        // Iconic Concentric White-Black-Red Owl Eyes
        val eyeRadius = bodyW * 0.20f
        val eyeY = bodyTop + bodyH * 0.30f
        val leftEyeX = cx - bodyW * 0.22f
        val rightEyeX = cx + bodyW * 0.22f

        // Left Eye
        drawCircle(color = Color.White, radius = eyeRadius, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color(0xFFD32F2F), radius = eyeRadius * 0.78f, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color(0xFFFFEB3B), radius = eyeRadius * 0.55f, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color.Black, radius = eyeRadius * 0.32f, center = Offset(leftEyeX, eyeY))

        // Right Eye
        drawCircle(color = Color.White, radius = eyeRadius, center = Offset(rightEyeX, eyeY))
        drawCircle(color = Color(0xFFD32F2F), radius = eyeRadius * 0.78f, center = Offset(rightEyeX, eyeY))
        drawCircle(color = Color(0xFFFFEB3B), radius = eyeRadius * 0.55f, center = Offset(rightEyeX, eyeY))
        drawCircle(color = Color.Black, radius = eyeRadius * 0.32f, center = Offset(rightEyeX, eyeY))

        // Hooked Golden-Yellow Beak
        val beakPath = Path().apply {
            moveTo(cx - 10f, eyeY + 2f)
            lineTo(cx + 10f, eyeY + 2f)
            lineTo(cx, eyeY + bodyH * 0.18f)
            close()
        }
        drawPath(beakPath, color = Color(0xFFFFD600))
        drawPath(beakPath, color = Color(0xFFF57F17), style = Stroke(width = 2f))

        // Chest Feather Chisel Markings (Bengali Folk Alpana Lines)
        val chestStartY = eyeY + bodyH * 0.22f
        val lineCount = 5
        for (i in 0 until lineCount) {
            val ly = chestStartY + i * (bodyH * 0.08f)
            val spread = (bodyW * 0.30f) * (1f - (i * 0.1f))
            drawArc(
                color = if (i % 2 == 0) Color(0xFFFFEB3B) else Color.White,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - spread, ly - 8f),
                size = Size(spread * 2f, 16f),
                style = Stroke(width = 3f)
            )
        }

        // Claws on stand
        drawCircle(color = Color(0xFFFFD600), radius = 5f, center = Offset(cx - 16f, h * 0.81f))
        drawCircle(color = Color(0xFFFFD600), radius = 5f, center = Offset(cx - 8f, h * 0.81f))
        drawCircle(color = Color(0xFFFFD600), radius = 5f, center = Offset(cx + 8f, h * 0.81f))
        drawCircle(color = Color(0xFFFFD600), radius = 5f, center = Offset(cx + 16f, h * 0.81f))
    }
}

/** 4. Bikna Dokra Brass Lost-Wax Durga Parivar Idol */
@Composable
private fun DokraDurgaIdolCanvas(modifier: Modifier = Modifier, isHero: Boolean) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Antique Brass Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = 0.32f),
                    Color(0xFF8D6E63).copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = h * 0.52f
            )
        )

        // Ornate Dokra Filigree Arch (Chala / Prabhavali)
        val archW = w * 0.72f
        val archH = h * 0.65f
        val archTop = h * 0.14f

        drawArc(
            color = Color(0xFFC5A059),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - archW / 2f, archTop),
            size = Size(archW, archH),
            style = Stroke(width = 6f)
        )
        // Outer beaded brass dots along arch
        val dotCount = 13
        for (i in 0..dotCount) {
            val angle = Math.PI + (Math.PI * i / dotCount)
            val rx = cx + (archW / 2f + 8f) * Math.cos(angle).toFloat()
            val ry = (archTop + archH / 2f) + (archH / 2f + 8f) * Math.sin(angle).toFloat()
            drawCircle(color = Color(0xFFD4AF37), radius = 4f, center = Offset(rx, ry))
        }

        // Heavy Stepped Dokra Pedestal Base
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(Color(0xFF5D4037), Color(0xFFBCAAA4), Color(0xFF5D4037))
            ),
            topLeft = Offset(cx - w * 0.34f, h * 0.76f),
            size = Size(w * 0.68f, h * 0.12f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Central Goddess Durga Figure (10 Arms & Divine Crown)
        val durgaHeadY = h * 0.30f
        // Crown (Mukut)
        val mukutPath = Path().apply {
            moveTo(cx - 14f, durgaHeadY)
            lineTo(cx, durgaHeadY - h * 0.08f)
            lineTo(cx + 14f, durgaHeadY)
            close()
        }
        drawPath(mukutPath, color = Color(0xFFD4AF37))

        // Divine Face & Third Eye
        drawCircle(color = Color(0xFFE6C687), radius = 12f, center = Offset(cx, durgaHeadY + 12f))
        drawCircle(color = Color(0xFF8D6E63), radius = 2.5f, center = Offset(cx, durgaHeadY + 8f)) // Trinetra

        // 10 Radial Brass Arms Holding Celestial Weapons
        val armAngles = listOf(-75, -55, -35, -15, 15, 35, 55, 75)
        armAngles.forEach { deg ->
            val rad = Math.toRadians(deg.toDouble() - 90.0)
            val armLen = w * 0.24f
            val ex = cx + (armLen * Math.cos(rad)).toFloat()
            val ey = (durgaHeadY + 24f) + (armLen * Math.sin(rad)).toFloat()
            drawLine(
                color = Color(0xFFC5A059),
                start = Offset(cx, durgaHeadY + 24f),
                end = Offset(ex, ey),
                strokeWidth = 3.5f
            )
            // Weapon / Mudra tip
            drawCircle(color = Color(0xFFFFD54F), radius = 3.5f, center = Offset(ex, ey))
        }

        // Sacred Trishula (Trident Piercing Mahishasura)
        drawLine(
            color = Color(0xFFFFE082),
            start = Offset(cx + 8f, durgaHeadY - 10f),
            end = Offset(cx + 24f, h * 0.74f),
            strokeWidth = 4f
        )
        // Trident head
        drawCircle(color = Color(0xFFFFD54F), radius = 6f, center = Offset(cx + 8f, durgaHeadY - 10f))

        // Dokra Lion (Simha) at Left
        drawCircle(color = Color(0xFFBCAAA4), radius = 10f, center = Offset(cx - w * 0.18f, h * 0.65f))
        drawLine(
            color = Color(0xFFC5A059),
            start = Offset(cx - w * 0.18f, h * 0.65f),
            end = Offset(cx - w * 0.08f, h * 0.70f),
            strokeWidth = 4f
        )

        // Mahishasura at Right (Vanquished Demon)
        drawCircle(color = Color(0xFF8D6E63), radius = 8f, center = Offset(cx + w * 0.18f, h * 0.68f))
        drawLine(
            color = Color(0xFF5D4037),
            start = Offset(cx + w * 0.18f, h * 0.68f),
            end = Offset(cx + w * 0.24f, h * 0.75f),
            strokeWidth = 3f
        )
    }
}

/** 5. Bengal Patachitra Hand-Painted Wall Display Plates (Shorai Set) */
@Composable
private fun PatachitraWallPlatesCanvas(modifier: Modifier = Modifier, isHero: Boolean) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Central Large Shorai Wall Disc
        val discRadius = h * 0.38f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFD32F2F), Color(0xFF880E4F), Color(0xFF311B92)),
                center = Offset(cx, cy),
                radius = discRadius
            ),
            radius = discRadius,
            center = Offset(cx, cy)
        )
        // Outer decorative rim
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = discRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 5f)
        )

        // Concentric alpana border dots
        val rimDots = 20
        for (i in 0 until rimDots) {
            val angle = 2.0 * Math.PI * i / rimDots
            val dotX = cx + (discRadius - 10f) * Math.cos(angle).toFloat()
            val dotY = cy + (discRadius - 10f) * Math.sin(angle).toFloat()
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(dotX, dotY))
        }

        // Inner Circle with Maa Durga / Radha Mukha
        val innerRadius = discRadius * 0.62f
        drawCircle(
            color = Color(0xFFFFECB3),
            radius = innerRadius,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0xFFC2185B),
            radius = innerRadius,
            center = Offset(cx, cy),
            style = Stroke(width = 3f)
        )

        // Durga Mukha / Expressive Bengali Folk Eyes
        val eyeW = innerRadius * 0.40f
        // Left Eye (Almond shaped bold black linework)
        drawOval(
            color = Color.White,
            topLeft = Offset(cx - eyeW - 6f, cy - 8f),
            size = Size(eyeW, 16f)
        )
        drawCircle(color = Color.Black, radius = 4f, center = Offset(cx - eyeW / 2f - 6f, cy))
        drawCircle(color = Color(0xFFD32F2F), radius = 2f, center = Offset(cx - eyeW - 3f, cy))

        // Right Eye
        drawOval(
            color = Color.White,
            topLeft = Offset(cx + 6f, cy - 8f),
            size = Size(eyeW, 16f)
        )
        drawCircle(color = Color.Black, radius = 4f, center = Offset(cx + eyeW / 2f + 6f, cy))
        drawCircle(color = Color(0xFFD32F2F), radius = 2f, center = Offset(cx + eyeW + 3f, cy))

        // Third Eye / Bindi
        drawOval(
            color = Color(0xFFD32F2F),
            topLeft = Offset(cx - 4f, cy - 22f),
            size = Size(8f, 14f)
        )
        // Red Nose Ring (Noth)
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 6f,
            center = Offset(cx + 8f, cy + 12f),
            style = Stroke(width = 2f)
        )

        // Accompaniment: Earthen Painted Matka Vase at Bottom Left
        drawOval(
            brush = Brush.verticalGradient(listOf(Color(0xFFE64A19), Color(0xFFBF360C))),
            topLeft = Offset(w * 0.12f, h * 0.62f),
            size = Size(w * 0.22f, h * 0.26f)
        )
        // Matka neck
        drawRect(
            color = Color(0xFFFFB74D),
            topLeft = Offset(w * 0.17f, h * 0.58f),
            size = Size(w * 0.12f, h * 0.05f)
        )

        // Hanging Wall Loop on Top
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = 8f,
            center = Offset(cx, cy - discRadius - 6f),
            style = Stroke(width = 3f)
        )
    }
}

/** Fallback Craft Renderers */
@Composable
private fun KanthaWeaveCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Running stitch grid
        for (y in 20 until h.toInt() step 24) {
            for (x in 20 until w.toInt() step 20) {
                drawLine(
                    color = Color(0xFFFFD54F).copy(alpha = 0.6f),
                    start = Offset(x.toFloat(), y.toFloat()),
                    end = Offset(x + 12f, y.toFloat()),
                    strokeWidth = 2.5f
                )
            }
        }
        // Central kalka motif
        drawCircle(color = Color(0xFFD81B60).copy(alpha = 0.35f), radius = h * 0.28f, center = Offset(w / 2f, h / 2f))
    }
}

@Composable
private fun BluePotteryCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFF00ACC1), Color(0xFF006064))),
            radius = h * 0.35f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}

@Composable
private fun MittiAttarCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFB74D), Color(0xFF8D6E63))),
            radius = h * 0.35f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}

@Composable
private fun BankuraHorseCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFE64A19), Color(0xFF4E342E))),
            radius = h * 0.35f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}

@Composable
private fun GenericArtisanCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFAB91), Color(0xFF5D4037))),
            radius = h * 0.32f,
            center = Offset(w / 2f, h / 2f)
        )
    }
}
