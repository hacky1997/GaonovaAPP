package com.example.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.ui.unit.dp

/**
 * Standardized Motion System Tokens for Gaonova.
 * Defines predictable timing, easing curves, and spatial animation behaviors.
 */
object MotionTokens {
    // Duration Tokens
    const val DurationMicro = 150       // 120-180ms: Micro-interactions, icon scale, button ripples
    const val DurationStandard = 240    // 200-280ms: Navigation tabs, indicators, list item reveal
    const val DurationEmphasis = 380    // 300-450ms: Modal sheets, drawer reveals, wallet cards
    const val DurationHero = 550        // 450-700ms: Hero carousel, story expansion, splash

    // Easing Curves
    val StandardEasing = FastOutSlowInEasing
    val DecelerateEasing = LinearOutSlowInEasing
    val AccelerateEasing = FastOutLinearInEasing
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val OrganicEasing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)

    // Animation Specs
    val microSpec = tween<Float>(durationMillis = DurationMicro, easing = StandardEasing)
    val standardSpec = tween<Float>(durationMillis = DurationStandard, easing = StandardEasing)
    val emphasisSpec = tween<Float>(durationMillis = DurationEmphasis, easing = EmphasizedEasing)
    val heroSpec = tween<Float>(durationMillis = DurationHero, easing = DecelerateEasing)

    val microDpSpec = tween<androidx.compose.ui.unit.Dp>(durationMillis = DurationMicro, easing = StandardEasing)
    val standardDpSpec = tween<androidx.compose.ui.unit.Dp>(durationMillis = DurationStandard, easing = StandardEasing)
    val emphasisDpSpec = tween<androidx.compose.ui.unit.Dp>(durationMillis = DurationEmphasis, easing = EmphasizedEasing)

    val standardColorSpec = tween<androidx.compose.ui.graphics.Color>(durationMillis = DurationStandard, easing = StandardEasing)

    // Gentle Spring for Tactile Responses
    val tactileSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
