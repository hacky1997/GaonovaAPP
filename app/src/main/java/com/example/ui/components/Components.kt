package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.*

// =========================================================================
// MITTI SOIL & NATURE EMBLEM (Custom Vector / Canvas Icon)
// Symbolizes: A little green plant / seedling sprouting and growing from fertile soil
// =========================================================================

@Composable
fun MittiSoilEmblem(
    modifier: Modifier = Modifier,
    leafTint: Color = MittiRoyalGreen,
    soilTint: Color = MittiSoilTerracotta,
    accentTint: Color = MittiRoyalGold,
    sproutProgress: Float = 1f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val progress = sproutProgress.coerceIn(0f, 1f)

        // 1. Fertile Soil Mound at the base (solid earth foundation)
        val soilMound = Path().apply {
            moveTo(w * 0.10f, h * 0.80f)
            cubicTo(w * 0.25f, h * 0.68f, w * 0.75f, h * 0.68f, w * 0.90f, h * 0.80f)
            cubicTo(w * 0.82f, h * 0.95f, w * 0.18f, h * 0.95f, w * 0.10f, h * 0.80f)
            close()
        }
        drawPath(soilMound, color = soilTint)

        // Lower soil shadow layer
        val soilShadow = Path().apply {
            moveTo(w * 0.18f, h * 0.85f)
            quadraticTo(w * 0.50f, h * 0.95f, w * 0.82f, h * 0.85f)
            quadraticTo(w * 0.50f, h * 0.90f, w * 0.18f, h * 0.85f)
            close()
        }
        drawPath(soilShadow, color = soilTint.copy(alpha = 0.6f))

        // Tiny fertile soil pebbles / specks
        drawCircle(color = soilTint.copy(alpha = 0.7f), radius = w * 0.035f, center = Offset(w * 0.30f, h * 0.80f))
        drawCircle(color = soilTint.copy(alpha = 0.7f), radius = w * 0.03f, center = Offset(w * 0.70f, h * 0.82f))
        drawCircle(color = soilTint.copy(alpha = 0.5f), radius = w * 0.025f, center = Offset(w * 0.50f, h * 0.86f))

        // 2. Tender Young Stem growing upward from soil center
        val stemStartY = h * 0.75f
        val stemEndY = stemStartY - (h * 0.53f * progress)
        val stemControlY = stemStartY - (h * 0.30f * progress)

        val stemPath = Path().apply {
            moveTo(w * 0.50f, stemStartY)
            cubicTo(w * 0.49f, stemControlY, w * 0.48f, stemControlY - (h * 0.05f * progress), w * 0.50f, stemEndY)
        }
        drawPath(
            path = stemPath,
            color = leafTint,
            style = Stroke(
                width = w * (0.055f + 0.025f * progress),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )

        // Only draw unfolding leaves and bud as the seedling sprouts
        if (progress > 0.1f) {
            val leafGrowth = ((progress - 0.1f) / 0.9f).coerceIn(0f, 1f)

            // 3. Left Sprout Leaf (Unfurls outward and upward from stem)
            val leftLeafOriginY = stemStartY - (h * 0.23f * progress)
            val leftLeafReachX = w * 0.50f - (w * 0.36f * leafGrowth)
            val leftLeafTipY = leftLeafOriginY - (h * 0.22f * leafGrowth)

            val leftLeaf = Path().apply {
                moveTo(w * 0.49f, leftLeafOriginY)
                cubicTo(
                    w * 0.50f - (w * 0.18f * leafGrowth), leftLeafOriginY + (h * 0.02f * leafGrowth),
                    leftLeafReachX, leftLeafOriginY - (h * 0.08f * leafGrowth),
                    leftLeafReachX + (w * 0.02f * leafGrowth), leftLeafTipY
                )
                cubicTo(
                    leftLeafReachX + (w * 0.14f * leafGrowth), leftLeafTipY - (h * 0.06f * leafGrowth),
                    w * 0.45f, leftLeafTipY + (h * 0.06f * leafGrowth),
                    w * 0.49f, leftLeafOriginY - (h * 0.08f * leafGrowth)
                )
                close()
            }
            drawPath(leftLeaf, color = leafTint)

            // Left leaf vein highlight
            val leftVein = Path().apply {
                moveTo(w * 0.48f, leftLeafOriginY - (h * 0.02f * leafGrowth))
                quadraticTo(
                    w * 0.50f - (w * 0.18f * leafGrowth), leftLeafOriginY - (h * 0.08f * leafGrowth),
                    leftLeafReachX + (w * 0.04f * leafGrowth), leftLeafTipY + (h * 0.03f * leafGrowth)
                )
            }
            drawPath(
                path = leftVein,
                color = Color.White.copy(alpha = 0.35f * leafGrowth),
                style = Stroke(width = w * 0.022f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // 4. Right Sprout Leaf (Reaches gracefully toward the light)
            val rightLeafOriginY = stemStartY - (h * 0.31f * progress)
            val rightLeafReachX = w * 0.50f + (w * 0.36f * leafGrowth)
            val rightLeafTipY = rightLeafOriginY - (h * 0.22f * leafGrowth)

            val rightLeaf = Path().apply {
                moveTo(w * 0.51f, rightLeafOriginY)
                cubicTo(
                    w * 0.50f + (w * 0.17f * leafGrowth), rightLeafOriginY + (h * 0.02f * leafGrowth),
                    rightLeafReachX, rightLeafOriginY - (h * 0.08f * leafGrowth),
                    rightLeafReachX - (w * 0.02f * leafGrowth), rightLeafTipY
                )
                cubicTo(
                    rightLeafReachX - (w * 0.12f * leafGrowth), rightLeafTipY - (h * 0.06f * leafGrowth),
                    w * 0.55f, rightLeafTipY + (h * 0.06f * leafGrowth),
                    w * 0.51f, rightLeafOriginY - (h * 0.08f * leafGrowth)
                )
                close()
            }
            drawPath(rightLeaf, color = leafTint)

            // Right leaf vein highlight
            val rightVein = Path().apply {
                moveTo(w * 0.52f, rightLeafOriginY - (h * 0.02f * leafGrowth))
                quadraticTo(
                    w * 0.50f + (w * 0.18f * leafGrowth), rightLeafOriginY - (h * 0.08f * leafGrowth),
                    rightLeafReachX - (w * 0.04f * leafGrowth), rightLeafTipY + (h * 0.03f * leafGrowth)
                )
            }
            drawPath(
                path = rightVein,
                color = Color.White.copy(alpha = 0.35f * leafGrowth),
                style = Stroke(width = w * 0.022f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // 5. Apical tender bud at the stem apex
            if (progress > 0.4f) {
                val budProgress = ((progress - 0.4f) / 0.6f).coerceIn(0f, 1f)
                val topBud = Path().apply {
                    moveTo(w * 0.50f, stemEndY)
                    cubicTo(
                        w * 0.44f, stemEndY - (h * 0.06f * budProgress),
                        w * 0.50f, stemEndY - (h * 0.14f * budProgress),
                        w * 0.50f, stemEndY - (h * 0.16f * budProgress)
                    )
                    cubicTo(
                        w * 0.56f, stemEndY - (h * 0.14f * budProgress),
                        w * 0.56f, stemEndY - (h * 0.06f * budProgress),
                        w * 0.50f, stemEndY
                    )
                    close()
                }
                drawPath(topBud, color = leafTint)

                // 6. Sparkling golden dew / morning sunlight droplet
                if (accentTint != Color.Transparent && budProgress > 0.3f) {
                    val dewProgress = ((budProgress - 0.3f) / 0.7f).coerceIn(0f, 1f)
                    // Radiant aura
                    drawCircle(
                        color = accentTint.copy(alpha = 0.25f * dewProgress),
                        radius = w * 0.085f * dewProgress,
                        center = Offset(w * 0.50f, stemEndY - (h * 0.15f * budProgress))
                    )
                    // Core dewdrop
                    drawCircle(
                        color = accentTint.copy(alpha = dewProgress),
                        radius = w * 0.045f * dewProgress,
                        center = Offset(w * 0.50f, stemEndY - (h * 0.15f * budProgress))
                    )
                }
            }
        }
    }
}

@Composable
fun MittiAvatar(
    modifier: Modifier = Modifier,
    sizeDp: Int = 36
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(sizeDp.dp / 3))
            .background(MittiGreenSurface)
            .border(1.dp, MittiSageGreen.copy(alpha = 0.3f), RoundedCornerShape(sizeDp.dp / 3)),
        contentAlignment = Alignment.Center
    ) {
        MittiSoilEmblem(
            modifier = Modifier.size((sizeDp * 0.65).dp),
            leafTint = MittiRoyalGreen,
            soilTint = MittiSoilTerracotta
        )
    }
}

@Composable
fun GaonovaTopBar(
    currentLocation: String,
    onLocationClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit,
    walletBalance: Double = 2850.0,
    onWalletClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Header with Cultural Seal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSearchClick
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(TerracottaPrimary, SaffronGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gaonova Cultural Emblem",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "GAONOVA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.4.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SaffronGoldLight.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "गाव",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TerracottaDark,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "Authentic Indian Village Crafts",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Actions: Wallet, Location Selector & Cart
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Swadeshi Wallet Quick Access
                    Surface(
                        onClick = onWalletClick,
                        shape = RoundedCornerShape(18.dp),
                        color = SaffronGoldLight,
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(SaffronGold.copy(alpha = 0.6f), SaffronGold.copy(alpha = 0.2f))
                            )
                        ),
                        modifier = Modifier
                            .testTag("top_bar_wallet_button")
                            .height(34.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Patron Wallet",
                                tint = TerracottaDark,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "₹${walletBalance.toInt()}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = TerracottaDark,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        onClick = onLocationClick,
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            )
                        ),
                        modifier = Modifier
                            .testTag("location_selector_chip")
                            .height(34.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Region Filter",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLocation,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("top_bar_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = TerracottaPrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text(text = cartItemCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = "Shopping Cart with $cartItemCount items",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Micro-motion bounce for wishlist heart
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isWishlisted) {
        if (isWishlisted) {
            scaleAnim.animateTo(
                targetValue = 1.35f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
            scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy))
        }
    }

    var isPressed by remember { mutableStateOf(false) }
    val cardPressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "product_card_press_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardPressScale)
            .testTag("product_card_${product.id}")
            .clickable(
                onClick = onProductClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.dp else 3.dp),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
                    TerracottaPrimary.copy(alpha = 0.08f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual header container with authentic craft artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(product.primaryColorHex).copy(alpha = 0.90f),
                                Color(product.primaryColorHex).copy(alpha = 0.60f),
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    )
                    .padding(8.dp)
            ) {
                // High-fidelity custom craft artwork rendering
                CraftArtworkView(
                    productId = product.id,
                    modifier = Modifier.fillMaxSize()
                )

                // Top row badges: Luminous GI / Heritage Foil Stamp and Wishlist
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.giCertified) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF144533).copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.6f)),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Authentic Verified GI Craft",
                                    tint = SaffronGoldLight,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "GI TAGGED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.55f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Handmade Craft",
                                    tint = SaffronGoldLight,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "HERITAGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Interactive Wishlist Button with Minimum 48dp target & Micro-bounce
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.95f))
                            .border(1.dp, Color.White, CircleShape)
                            .clickable(onClick = onWishlistToggle)
                            .testTag("wishlist_toggle_${product.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isWishlisted) "Remove from wishlist" else "Save to wishlist",
                            tint = if (isWishlisted) PriceDropRed else TextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .scale(if (isWishlisted) scaleAnim.value else 1f)
                        )
                    }
                }

                // Origin Stamp at Bottom with Glassmorphic Backdrop
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.55f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = SaffronGoldLight,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${product.district}, ${product.state}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Body information with clean, minimal, product-first typography hierarchy
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Quick Action with Artisan Guarantee Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${product.price.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (product.originalPrice > product.price) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${product.originalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = GiTagGreen,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "85% Direct to Artisan",
                                style = MaterialTheme.typography.labelSmall,
                                color = GiTagGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = onQuickAdd,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("quick_add_${product.id}"),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyWelcomeBanner(
    productOfTheDay: Product,
    onExploreCraft: (Product) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Amazon-style signature multi-hue luminous mesh & linear gradient
    val amazonGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF007185), // Amazon deep rich teal
            Color(0xFF00A896), // Amazon turquoise / sea green
            Color(0xFF2C3E50), // Amazon deep twilight
            Color(0xFFEB8D00)  // Amazon sunrise warm amber
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 600f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_welcome_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(amazonGradient)
        ) {
            // Luminous glowing ambient spheres (Amazon app atmospheric style)
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD814).copy(alpha = 0.38f), Color.Transparent),
                        center = Offset(w * 0.88f, h * 0.15f),
                        radius = w * 0.55f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(w * 0.1f, h * 0.85f),
                        radius = w * 0.5f
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header row with pill badge and close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.25f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFFFD814).copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFFFD814),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TODAY'S REGIONAL DISCOVERY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 0.8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("dismiss_welcome_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Daily Banner",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Preserving authentic Indian crafts through certified geographic provenance & master artisans.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Product Spotlight Glassmorphic Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExploreCraft(productOfTheDay) },
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.16f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(productOfTheDay.primaryColorHex),
                                            Color(productOfTheDay.primaryColorHex).copy(alpha = 0.7f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = productOfTheDay.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD814),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${productOfTheDay.villageOrCluster}, ${productOfTheDay.state}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Button(
                            onClick = { onExploreCraft(productOfTheDay) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD814),
                                contentColor = Color(0xFF111111)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier
                                .heightIn(min = 38.dp)
                                .testTag("explore_now_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Explore now",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF111111)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FivePillarsKnowledgeViewer(
    product: Product,
    artisan: Artisan?,
    onArtisanClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("WHAT", "WHERE", "WHY", "WHO", "HOW")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "The 5 Pillars of Provenance",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Tab bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> { // WHAT
                    Column {
                        Text(
                            text = "What is this craft?",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.whatDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Key Materials Used:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            product.materials.forEach { mat ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(text = mat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
                1 -> { // WHERE
                    Column {
                        Text(
                            text = "Geographical Origin & Cluster",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.whereContext,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SaffronGoldLight.copy(alpha = 0.5f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(SaffronGold, SaffronGold))
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${product.villageOrCluster}, ${product.district}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "State: ${product.state}, India (Verified Registry)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> { // WHY
                    Column {
                        Text(
                            text = "Cultural & Historical Significance",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.whySignificance,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (product.sourceConflictNote != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PriceDropRedBg
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = PriceDropRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Note: ${product.sourceConflictNote}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PriceDropRed
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> { // WHO
                    Column {
                        Text(
                            text = "Artisan Lineage & Community",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.whoArtisanStory,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (artisan != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArtisanClick(artisan.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = artisan.name.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = artisan.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${artisan.experienceYears} Years Experience • ${artisan.lineageGenerations} Generations Lineage",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "View Profile",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                4 -> { // HOW
                    Column {
                        Text(
                            text = "How to Care & Verify Authenticity",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.howCareAndBuy,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Authenticity Verification Points:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        product.authenticityChecklist.forEach { check ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GiTagGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = check,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableCitationsCard(
    citations: List<Citation>,
    lastVerifiedDate: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Official Citations & Sources (${citations.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Last verified on $lastVerifiedDate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    citations.forEachIndexed { index, cit ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}. ${cit.title}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GiTagGreenBg
                                    ) {
                                        Text(
                                            text = cit.reliabilityScore,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 8.sp,
                                            color = GiTagGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Publisher: ${cit.publisher} (${cit.publicationYear})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${cit.excerpt}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriceHistoryVisualizer(
    priceHistory: List<PricePoint>,
    currentPrice: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = GiTagGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transparent Price History",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GiTagGreenBg
                ) {
                    Text(
                        text = "Fair Artisan Wages",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = GiTagGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (priceHistory.isNotEmpty()) {
                val prices = priceHistory.map { it.price }
                val minPrice = (prices.minOrNull() ?: 1000.0) * 0.9
                val maxPrice = (prices.maxOrNull() ?: 5000.0) * 1.1

                val primaryColor = MaterialTheme.colorScheme.primary

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(horizontal = 10.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val stepX = if (priceHistory.size > 1) w / (priceHistory.size - 1) else w

                    val path = Path()
                    priceHistory.forEachIndexed { i, point ->
                        val normalizedY = ((point.price - minPrice) / (maxPrice - minPrice)).toFloat()
                        val y = h - (normalizedY * h)
                        val x = i * stepX
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)

                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 2.5.dp.toPx())
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    priceHistory.forEach { point ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "₹${point.price.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                            Text(
                                text = point.monthYear,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = actionText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
