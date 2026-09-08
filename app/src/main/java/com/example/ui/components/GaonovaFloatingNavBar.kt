package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CartItem
import com.example.data.models.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenDestination

/**
 * Premium Floating Navigation Bar for Gaonova.
 * Features:
 * - Unified traveling active pill indicator with smooth 200ms easing
 * - Tailored micro-motion per tab (Upward Home, Compass Explore, Awakening Mitti, Expanding Cart, Elevated Account)
 * - Refined warm-white floating pill surface with subtle shadow & natural hairline border
 * - Haptic feedback integration
 */
@Composable
fun GaonovaFloatingNavBar(
    currentScreen: ScreenDestination,
    cartItems: List<CartItem>,
    isLoggedIn: Boolean,
    onNavigate: (ScreenDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    // Active Tab Index (0: Home, 1: Explore, 2: Mitti, 3: Cart, 4: Account)
    val selectedIndex = when (currentScreen) {
        is ScreenDestination.Home -> 0
        is ScreenDestination.Explore -> 1
        is ScreenDestination.Mitti -> 2
        is ScreenDestination.Cart -> 3
        is ScreenDestination.Account -> 4
        else -> -1
    }

    // Don't render floating bar if on deep sub-screens
    if (selectedIndex == -1) return

    val isMittiSelected = selectedIndex == 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = if (isMittiSelected) 5.dp else 4.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = if (isMittiSelected) MittiRoyalGreen.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.08f),
                    ambientColor = Color.Black.copy(alpha = 0.04f)
                )
                .testTag("bottom_navigation_bar"),
            shape = RoundedCornerShape(26.dp),
            color = if (isMittiSelected) MittiWarmIvory else Color.White,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isMittiSelected) MittiSageLight else Color(0xFFEAE7DE)
                )
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                val totalWidth = maxWidth
                val tabWidth = totalWidth / 5

                // Smooth Traveling Indicator Pill
                val indicatorOffset by animateDpAsState(
                    targetValue = tabWidth * selectedIndex,
                    animationSpec = tween(
                        durationMillis = MotionTokens.DurationStandard,
                        easing = MotionTokens.StandardEasing
                    ),
                    label = "traveling_nav_indicator_offset"
                )

                // Indicator Background Pill
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isMittiSelected) MittiGreenContainer.copy(alpha = 0.85f)
                            else Color(0xFFF5F3ED)
                        )
                )

                // Nav Items Row
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. HOME
                    NavTabItem(
                        modifier = Modifier.weight(1f),
                        isSelected = selectedIndex == 0,
                        label = "Home",
                        testTag = "nav_home",
                        onClick = {
                            if (selectedIndex != 0) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onNavigate(ScreenDestination.Home)
                        }
                    ) { isSel ->
                        val offsetY by animateDpAsState(
                            targetValue = if (isSel) (-2.5).dp else 0.dp,
                            animationSpec = tween(MotionTokens.DurationMicro, easing = MotionTokens.StandardEasing),
                            label = "home_offset"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSel) 1.08f else 1.0f,
                            animationSpec = tween(MotionTokens.DurationMicro),
                            label = "home_scale"
                        )
                        Icon(
                            imageVector = if (isSel) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home",
                            tint = if (isSel) TextPrimary else TextSecondary,
                            modifier = Modifier
                                .offset(y = offsetY)
                                .scale(scale)
                                .size(21.dp)
                        )
                    }

                    // 2. EXPLORE
                    NavTabItem(
                        modifier = Modifier.weight(1f),
                        isSelected = selectedIndex == 1,
                        label = "Explore",
                        testTag = "nav_explore",
                        onClick = {
                            if (selectedIndex != 1) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onNavigate(ScreenDestination.Explore)
                        }
                    ) { isSel ->
                        val rotation by animateFloatAsState(
                            targetValue = if (isSel) 15f else 0f,
                            animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing),
                            label = "explore_rotation"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSel) 1.08f else 1.0f,
                            animationSpec = tween(MotionTokens.DurationMicro),
                            label = "explore_scale"
                        )
                        Icon(
                            imageVector = if (isSel) Icons.Filled.GridView else Icons.Outlined.GridView,
                            contentDescription = "Explore",
                            tint = if (isSel) TextPrimary else TextSecondary,
                            modifier = Modifier
                                .rotate(rotation)
                                .scale(scale)
                                .size(21.dp)
                        )
                    }

                    // 3. MITTI (Organic Earth / Plant Awakening & Sprouting Motion with Distinctive Grounded Haptic Pulse)
                    NavTabItem(
                        modifier = Modifier.weight(1f),
                        isSelected = selectedIndex == 2,
                        label = "Mitti",
                        testTag = "nav_mitti",
                        selectedLabelColor = MittiRoyalGreen,
                        onClick = {
                            if (selectedIndex != 2) {
                                // Distinctive rich/grounding haptic response on entering Mitti
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            onNavigate(ScreenDestination.Mitti)
                        }
                    ) { isSel ->
                        // Organic Sprouting Motion Animation
                        val sproutProgress by animateFloatAsState(
                            targetValue = if (isSel) 1.0f else 0.18f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "mitti_sprout_progress"
                        )
                        val animatedLeafColor by animateColorAsState(
                            targetValue = if (isSel) MittiRoyalGreen else TextSecondary,
                            animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing),
                            label = "mitti_leaf_color"
                        )
                        val animatedSoilColor by animateColorAsState(
                            targetValue = if (isSel) MittiSoilTerracotta else Color(0xFF888480),
                            animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing),
                            label = "mitti_soil_color"
                        )
                        val animatedAccentColor by animateColorAsState(
                            targetValue = if (isSel) MittiRoyalGold else Color.Transparent,
                            animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing),
                            label = "mitti_accent_color"
                        )
                        val scale by animateFloatAsState(
                            targetValue = if (isSel) 1.15f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "mitti_scale"
                        )
                        val offsetY by animateDpAsState(
                            targetValue = if (isSel) (-2.5).dp else 0.dp,
                            animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing),
                            label = "mitti_offset"
                        )

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            // Subtle organic aura glow when active
                            if (isSel) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(MittiGreenContainer.copy(alpha = 0.5f))
                                )
                            }

                            MittiSoilEmblem(
                                modifier = Modifier
                                    .offset(y = offsetY)
                                    .scale(scale)
                                    .size(23.dp),
                                leafTint = animatedLeafColor,
                                soilTint = animatedSoilColor,
                                accentTint = animatedAccentColor,
                                sproutProgress = sproutProgress
                            )
                        }
                    }

                    // 4. CART
                    NavTabItem(
                        modifier = Modifier.weight(1f),
                        isSelected = selectedIndex == 3,
                        label = "Cart",
                        testTag = "nav_cart",
                        onClick = {
                            if (selectedIndex != 3) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onNavigate(ScreenDestination.Cart)
                        }
                    ) { isSel ->
                        val scale by animateFloatAsState(
                            targetValue = if (isSel) 1.08f else 1.0f,
                            animationSpec = tween(MotionTokens.DurationMicro),
                            label = "cart_scale"
                        )
                        val count = cartItems.sumOf { it.quantity }
                        BadgedBox(
                            badge = {
                                if (count > 0) {
                                    Badge(
                                        containerColor = TerracottaPrimary,
                                        contentColor = Color.White,
                                        modifier = Modifier.scale(0.85f)
                                    ) {
                                        Text(
                                            text = if (count > 99) "99+" else "$count",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSel) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                                contentDescription = "Cart",
                                tint = if (isSel) TextPrimary else TextSecondary,
                                modifier = Modifier
                                    .scale(scale)
                                    .size(21.dp)
                            )
                        }
                    }

                    // 5. ACCOUNT / YOU
                    val profileLabel = if (isLoggedIn) "You" else "Account"
                    NavTabItem(
                        modifier = Modifier.weight(1f),
                        isSelected = selectedIndex == 4,
                        label = profileLabel,
                        testTag = "nav_account",
                        onClick = {
                            if (selectedIndex != 4) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                            onNavigate(ScreenDestination.Account)
                        }
                    ) { isSel ->
                        val scale by animateFloatAsState(
                            targetValue = if (isSel) 1.08f else 1.0f,
                            animationSpec = tween(MotionTokens.DurationMicro),
                            label = "account_scale"
                        )
                        Icon(
                            imageVector = if (isSel) Icons.Filled.Person else Icons.Outlined.PersonOutline,
                            contentDescription = profileLabel,
                            tint = if (isSel) TextPrimary else TextSecondary,
                            modifier = Modifier
                                .scale(scale)
                                .size(21.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    label: String,
    testTag: String,
    selectedLabelColor: Color = TextPrimary,
    onClick: () -> Unit,
    iconContent: @Composable (isSelected: Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        iconContent(isSelected)

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 10.5.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) selectedLabelColor else TextSecondary,
            letterSpacing = 0.1.sp,
            maxLines = 1
        )
    }
}
