package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserRole
import com.example.data.models.WalletTransaction
import com.example.data.models.WalletTransactionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val walletState by viewModel.walletState.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val effectiveRole = authState.effectiveRole()
    val isGuest = effectiveRole == UserRole.GUEST

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // State for interactive modals & cards
    var showTopUpDialog by remember { mutableStateOf(false) }
    var topUpAmountText by remember { mutableStateOf("1000") }
    var selectedPreset by remember { mutableStateOf(1000.0) }

    var showRedeemDialog by remember { mutableStateOf(false) }
    var coinsToRedeem by remember { mutableStateOf(100) }

    var isBalanceVisible by remember { mutableStateOf(true) }
    var isCardFlipped by remember { mutableStateOf(false) }
    var showCardDetails by remember { mutableStateOf(false) }
    var showScanPayDialog by remember { mutableStateOf(false) }
    var showPayLaterDialog by remember { mutableStateOf(false) }
    var showGiftCardDialog by remember { mutableStateOf(false) }
    var showScratchCardDialog by remember { mutableStateOf(false) }
    var showAutoTopupDialog by remember { mutableStateOf(false) }

    var selectedLedgerFilter by remember { mutableStateOf("All") }
    val ledgerFilters = listOf("All", "Paid to Artisan", "Money Added", "Cashback", "Escrow Settled")

    // Screen Entry Choreography Animation on Navigation
    val screenEntry = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        screenEntry.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    // 3D Card Flip Animation State
    val flipRotation by animateFloatAsState(
        targetValue = if (isCardFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "wallet_card_3d_flip"
    )

    // Card interactive ambient shimmer loop
    val infiniteTransition = rememberInfiniteTransition(label = "wallet_ambient_loop")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -350f,
        targetValue = 950f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "card_golden_shimmer"
    )

    // Filter transactions based on selection
    val filteredTransactions = remember(walletState.transactions, selectedLedgerFilter) {
        when (selectedLedgerFilter) {
            "Paid to Artisan" -> walletState.transactions.filter { !it.isCredit }
            "Money Added" -> walletState.transactions.filter { it.isCredit && it.type == WalletTransactionType.TOP_UP }
            "Cashback" -> walletState.transactions.filter { it.type == WalletTransactionType.PATRON_CASHBACK || it.type == WalletTransactionType.ARTISAN_DIVIDEND }
            "Escrow Settled" -> walletState.transactions.filter { it.status.contains("Settled", ignoreCase = true) }
            else -> walletState.transactions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("wallet_screen_scroll"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Amazon Pay Style Header & Patron Digital Pass with interactive shimmer, 3D flip & entry physics
        item(key = "patron_pass_header") {
            var isCardPressed by remember { mutableStateOf(false) }
            val cardScale by animateFloatAsState(
                targetValue = if (isCardPressed) 0.98f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "wallet_card_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val entry = screenEntry.value
                        rotationX = (1f - entry) * 12f
                        translationY = (1f - entry) * 45f
                        scaleX = (0.94f + 0.06f * entry) * cardScale
                        scaleY = (0.94f + 0.06f * entry) * cardScale
                        alpha = entry.coerceIn(0f, 1f)
                        rotationY = flipRotation
                        cameraDistance = 14f * density
                    }
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (!isGuest) {
                            isCardFlipped = !isCardFlipped
                        }
                    }
                    .testTag("patron_wallet_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = TerracottaDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                if (flipRotation > 90f) {
                    // Back Face of the Pass (Rotated 180 so text and content are normal orientation)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                rotationY = 180f
                            }
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        IndigoMidnight,
                                        TerracottaDark,
                                        Color(0xFF1E1C1A)
                                    )
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Back Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode2,
                                        contentDescription = null,
                                        tint = SaffronGoldLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ESCROW PASS & QR TOKEN",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SaffronGoldContainer,
                                        letterSpacing = 1.2.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        isCardFlipped = false
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flip,
                                            contentDescription = "Flip Back",
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Flip Front",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Middle: QR Code & Escrow Token Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.Black.copy(alpha = 0.35f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.QrCode2,
                                        contentDescription = "Escrow Pass QR",
                                        tint = Color.Black,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "PASS TOKEN ID",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RawSilkCream.copy(alpha = 0.6f),
                                        fontSize = 9.sp
                                    )
                                    Text(
                                        text = "GAON-ESC-${walletState.transactions.size.plus(1024)}-NODE",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = SaffronGoldContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Linked to ${authState.currentUser?.name ?: "Patron"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RawSilkCream.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Security & Trustee Attributes
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "TRUSTEE BANK",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RawSilkCream.copy(alpha = 0.6f),
                                        fontSize = 8.5.sp
                                    )
                                    Text(
                                        text = "RBI Multi-Trustee Custody",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GiTagGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "COMMISSION GUARANTEE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RawSilkCream.copy(alpha = 0.6f),
                                        fontSize = 8.5.sp
                                    )
                                    Text(
                                        text = "0% Cut · 100% to Artisans",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronGoldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tap to flip hint pill
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = RawSilkCream.copy(alpha = 0.7f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tap pass anywhere to return to balance & quick actions",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RawSilkCream.copy(alpha = 0.8f),
                                        fontSize = 9.5.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Front Face of the Pass
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    if (isGuest) {
                                        listOf(
                                            Color(0xFF374151),
                                            Color(0xFF1F2937),
                                            Color(0xFF111827)
                                        )
                                    } else {
                                        listOf(
                                            TerracottaPrimary,
                                            TerracottaDark,
                                            IndigoMidnight
                                        )
                                    }
                                )
                            )
                    ) {
                        // Shimmering Golden Highlight Canvas Overlay
                        if (!isGuest) {
                            Canvas(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(22.dp))
                            ) {
                                val shimmerBrush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        SaffronGoldLight.copy(alpha = 0.12f),
                                        Color.White.copy(alpha = 0.22f),
                                        SaffronGoldLight.copy(alpha = 0.12f),
                                        Color.Transparent
                                    ),
                                    start = Offset(shimmerOffset, 0f),
                                    end = Offset(shimmerOffset + 240f, size.height)
                                )
                                drawRect(brush = shimmerBrush)
                            }
                        }

                        Column(modifier = Modifier.padding(20.dp)) {
                            // Pass Header: Brand & Escrow Tag + 3D Flip Indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(if (isGuest) Color(0xFF6B7280) else GoldenAmber),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isGuest) Icons.Default.Lock else Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isGuest) "GAONOVA SMART WALLET (GUEST)" else "GAONOVA SMART WALLET",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isGuest) RawSilkCream else SaffronGoldContainer,
                                            letterSpacing = 1.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isGuest) "Atithi Mode · Sign in to unlock personal escrow passbook" else "${effectiveRole.badgeLabel} · Direct Artisan Escrow",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RawSilkCream.copy(alpha = 0.75f),
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isGuest) Color.White.copy(alpha = 0.15f) else GiTagGreenBg
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isGuest) Icons.Default.Lock else Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = if (isGuest) RawSilkCream else GiTagGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isGuest) "LOCKED" else "100% ESCROW",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isGuest) RawSilkCream else GiTagGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (!isGuest) {
                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                isCardFlipped = true
                                            },
                                            shape = CircleShape,
                                            color = Color.White.copy(alpha = 0.18f),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.FlipCameraAndroid,
                                                    contentDescription = "Flip Card",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Balance Display with Visibility Toggle & Smooth Numerical Transitions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isGuest) "Guest Balance" else "Available Balance",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = RawSilkCream.copy(alpha = 0.85f)
                                        )
                                        if (!isGuest) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    isBalanceVisible = !isBalanceVisible
                                                },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isBalanceVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                                    contentDescription = "Toggle Balance",
                                                    tint = RawSilkCream.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    AnimatedContent(
                                        targetState = if (isGuest) "₹0.00" else if (isBalanceVisible) "₹${"%,.2f".format(walletState.balance)}" else "₹ ••••••••",
                                        transitionSpec = {
                                            (fadeIn(animationSpec = tween(MotionTokens.DurationStandard)) +
                                                    slideInVertically(animationSpec = tween(MotionTokens.DurationStandard)) { height -> height / 3 })
                                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationMicro)) +
                                                        slideOutVertically(animationSpec = tween(MotionTokens.DurationMicro)) { height -> -height / 3 })
                                        },
                                        label = "wallet_balance_animated"
                                    ) { balanceText ->
                                        Text(
                                            text = balanceText,
                                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Normal),
                                            color = Color.White
                                        )
                                    }
                                }

                                // Quick Action Button with spring scale
                                if (isGuest) {
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.isLoginModalVisible.value = true
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SaffronGoldLight,
                                            contentColor = TerracottaDark
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("wallet_guest_login_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Login,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Sign In",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            showTopUpDialog = true
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SaffronGoldLight,
                                            contentColor = TerracottaDark
                                        ),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                        modifier = Modifier.testTag("wallet_top_up_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Add Money",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Swadeshi Karma Coins Vault Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.28f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldenAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isGuest) "Sign in to collect Swadeshi Karma Coins" else "${walletState.karmaCoins} Swadeshi Karma Coins (₹${walletState.karmaCoins} value)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = RawSilkCream
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        if (isGuest) {
                                            viewModel.isLoginModalVisible.value = true
                                        } else {
                                            showRedeemDialog = true
                                        }
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text(
                                        text = if (isGuest) "Sign In →" else "Redeem →",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronGoldLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (!isGuest) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Flip,
                                        contentDescription = null,
                                        tint = RawSilkCream.copy(alpha = 0.6f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Tap pass to flip for Escrow QR & Token Node",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = RawSilkCream.copy(alpha = 0.65f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1.5 FOUNDER GOVERNANCE & AUDIT CONSOLE (RBAC: ONLY FOR PATRON_FOUNDER)
        if (effectiveRole == UserRole.PATRON_FOUNDER) {
            item(key = "founder_governance_console") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("founder_governance_console"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, GoldenAmber)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(GoldenAmber.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = GoldenAmber,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🏛️ Founder & Governance Console",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Co-Founder Protocol: ${authState.currentUser?.name} (${authState.currentUser?.email})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldenAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "FOUNDER ACCESS",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TerracottaDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Section 1: GI Certificate Provenance Approval
                        Text(
                            text = "GI Provenance Seal Master Approval",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "2 certified artisan batches awaiting cryptographically signed provenance seals:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.approveGiCertificateSeal("WB-2026-88", "Bolpur Nakshi Kantha Cooperative")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = GiTagGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sign Bolpur Kantha #88", fontSize = 10.5.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.approveGiCertificateSeal("CG-2026-14", "Kondagaon Bastar Dokra Guild")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = GiTagGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sign Bastar Dokra #14", fontSize = 10.5.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Section 2: Artisan Cluster Escrow Settlement Release
                        Text(
                            text = "Artisan Cooperative Direct Escrow Release",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Zero-commission direct bank settlements ready for cooperative transfer:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.releaseArtisanClusterEscrow("Nadia Weaver Cooperative", 48500.0)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaDark),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Release ₹48,500 Nadia", fontSize = 10.5.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.releaseArtisanClusterEscrow("Raghurajpur Heritage Pattachitra Guild", 32200.0)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoMidnight),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Release ₹32,200 Puri", fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Amazon Pay-Style 8-Action Smart Services Grid (4 x 2)
        item(key = "amazon_services_grid") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Smart Wallet Services",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmartServiceItem(
                        icon = Icons.Outlined.QrCodeScanner,
                        title = "Scan & Pay",
                        badge = "UPI",
                        badgeColor = TerracottaDark,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in as a Patron to use Scan & Pay.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showScanPayDialog = true
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.AccountBalance,
                        title = "Add Money",
                        badge = "+5% Coins",
                        badgeColor = GiTagGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in as a Patron to load money into your wallet.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showTopUpDialog = true
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.CreditCard,
                        title = "Pay Later",
                        badge = "0% EMI",
                        badgeColor = GoldenAmber,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in to check your Gaonova Pay Later eligibility.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showPayLaterDialog = true
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.CardGiftcard,
                        title = "Gift Cards",
                        badge = "Art E-Card",
                        badgeColor = TerracottaPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in to send handcrafted gift cards.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showGiftCardDialog = true
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmartServiceItem(
                        icon = Icons.Outlined.Autorenew,
                        title = "Auto-Topup",
                        badge = "Smart",
                        badgeColor = IndigoMidnight,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in to set up Smart Auto-Topup.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showAutoTopupDialog = true
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.Redeem,
                        title = "Rewards Hub",
                        badge = "3 Cards",
                        badgeColor = PriceDropRed,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in to view and scratch your reward cards.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                showScratchCardDialog = true
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.ElectricBolt,
                        title = "Weaver Fund",
                        badge = "Solar Power",
                        badgeColor = TerracottaDark,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (isGuest) {
                                viewModel.showNotification("Please sign in to donate directly to the Weaver Solar Fund.")
                                viewModel.isLoginModalVisible.value = true
                            } else {
                                viewModel.showNotification("Contributed ₹50 to Nadia Weaver Solar Power Fund!")
                            }
                        }
                    )
                    SmartServiceItem(
                        icon = Icons.Outlined.VerifiedUser,
                        title = "Escrow Shield",
                        badge = "100% Safe",
                        badgeColor = GiTagGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.showNotification("Protected by Gaonova Direct Artisan Escrow.")
                        }
                    )
                }
            }
        }

        // 3. Feature Highlight 1: Gaonova Pay Later (Amazon Pay Later equivalent)
        item(key = "pay_later_feature_card") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPayLaterDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IndigoMidnight.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditScore,
                            contentDescription = null,
                            tint = IndigoMidnight,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gaonova Pay Later",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GiTagGreenBg
                            ) {
                                Text(
                                    text = "₹10,000 LIMIT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GiTagGreen,
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Buy GI handlooms & brass crafts today. Pay next month with 0% interest & 1-tap checkout.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4. Feature Highlight 2: Rewards & Scratch Cards Hub
        item(key = "rewards_scratch_cards_card") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showScratchCardDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SaffronGoldContainer.copy(alpha = 0.35f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldenAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Celebration,
                            contentDescription = null,
                            tint = TerracottaDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "3 Scratch Cards Waiting!",
                            style = MaterialTheme.typography.titleSmall,
                            color = TerracottaDark,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Scratch to unlock up to ₹250 instant cashback on your next Dokra or Pashmina order.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TerracottaDark
                    ) {
                        Text(
                            text = "Scratch",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 5. Regional Spend Analytics & Socio-Economic Impact
        item(key = "socio_economic_impact_card") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Your Swadeshi Economic Footprint",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "8 Families Supported",
                            style = MaterialTheme.typography.labelSmall,
                            color = GiTagGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your direct patron payments have delivered ₹12,450 directly to weaver & handicraft cooperatives with zero commission deductions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Cluster Progress Bars
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImpactProgressBar(label = "West Bengal (Kantha & Dokra)", percentage = 0.45f, color = TerracottaPrimary)
                        ImpactProgressBar(label = "Rajasthan (Blue Pottery & Mojaris)", percentage = 0.30f, color = GoldenAmber)
                        ImpactProgressBar(label = "Odisha (Pattachitra & Silver Filigree)", percentage = 0.25f, color = IndigoMidnight)
                    }
                }
            }
        }

        // 6. Transaction Activity Ledger & Invoices
        item(key = "ledger_header") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Patron Activity Ledger",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "${filteredTransactions.size} transactions",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ledgerFilters) { filterName ->
                        val isSelected = selectedLedgerFilter == filterName
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedLedgerFilter = filterName },
                            label = { Text(filterName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        if (isGuest) {
            item(key = "guest_ledger_locked") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Artisan Passbook is Locked",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Sign in to access your direct artisan escrow transactions, GST tax invoices, and dividend receipts.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { viewModel.isLoginModalVisible.value = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaDark)
                        ) {
                            Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign In to View Passbook")
                        }
                    }
                }
            }
        } else if (filteredTransactions.isEmpty()) {
            item(key = "empty_ledger") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SaffronGoldContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ReceiptLong,
                                contentDescription = null,
                                tint = GoldenAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Your wallet history is quiet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Add funds or make your first artisan purchase when you're ready.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { txn ->
                WalletTransactionItem(txn = txn, onDownloadInvoice = {
                    viewModel.showNotification("GST Tax Invoice & Escrow Certificate downloaded for ${txn.id}")
                })
            }
        }
    }

    // ==========================================
    // INTERACTIVE SMART WALLET DIALOGS
    // ==========================================

    // 1. Top Up / Add Money Dialog with interactive simulated escrow processing
    if (showTopUpDialog) {
        var isProcessingPayment by remember { mutableStateOf(false) }
        var paymentSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (!isProcessingPayment) showTopUpDialog = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Money to Swadeshi Wallet",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                if (isProcessingPayment) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = paymentSuccess,
                            label = "payment_processing_state"
                        ) { isSuccess ->
                            if (isSuccess) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(GiTagGreenBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GiTagGreen,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(54.dp),
                                    color = TerracottaPrimary,
                                    strokeWidth = 4.dp
                                )
                            }
                        }

                        Text(
                            text = if (paymentSuccess) "Funds Added & Escrow Locked!" else "Securing funds in RBI Multi-Trustee Escrow...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (paymentSuccess) "₹$topUpAmountText added to your available balance" else "Connecting with UPI Gateway",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Enter amount to instantly fund your wallet with direct zero-fee escrow:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = topUpAmountText,
                            onValueChange = {
                                topUpAmountText = it.filter { char -> char.isDigit() }
                                selectedPreset = topUpAmountText.toDoubleOrNull() ?: 0.0
                            },
                            prefix = { Text("₹ ", fontWeight = FontWeight.SemiBold) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Interactive Preset Selector Chips with Spring Scale
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(500.0, 1000.0, 2000.0, 5000.0).forEach { preset ->
                                val isSelected = selectedPreset == preset
                                val chipScale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.05f else 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                    label = "preset_chip_scale"
                                )

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedPreset = preset
                                        topUpAmountText = preset.toInt().toString()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) TerracottaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .scale(chipScale)
                                ) {
                                    Text(
                                        text = "₹${preset.toInt()}",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Dynamic calculation preview
                        val estimatedCoins = (topUpAmountText.toDoubleOrNull() ?: 0.0) * 0.05
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GiTagGreenBg.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GiTagGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Earn +${estimatedCoins.toInt()} Swadeshi Karma Coins (+5%) instantly",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GiTagGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!isProcessingPayment) {
                    Button(
                        onClick = {
                            val amount = topUpAmountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isProcessingPayment = true
                                coroutineScope.launch {
                                    delay(900)
                                    paymentSuccess = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    delay(600)
                                    viewModel.topUpWallet(amount)
                                    showTopUpDialog = false
                                    isProcessingPayment = false
                                    paymentSuccess = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Text("Proceed to Pay")
                    }
                }
            },
            dismissButton = {
                if (!isProcessingPayment) {
                    TextButton(onClick = { showTopUpDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // 2. Scan & Pay QR Simulation Dialog with Animated Laser Beam
    if (showScanPayDialog) {
        val scanLaserTransition = rememberInfiniteTransition(label = "scan_laser")
        val laserPosition by scanLaserTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "laser_pos"
        )

        AlertDialog(
            onDismissRequest = { showScanPayDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan & Pay Artisan UPI QR", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Point camera at any craft mela stall or artisan UPI QR code to pay directly with zero transaction fee.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Scanner Viewport with Animated Laser Line
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF161514))
                            .border(2.dp, TerracottaPrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode2,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(110.dp)
                        )

                        // Animated Laser Sweep Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val y = size.height * laserPosition
                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, GiTagGreen, Color.White, GiTagGreen, Color.Transparent)
                                ),
                                start = Offset(16.dp.toPx(), y),
                                end = Offset(size.width - 16.dp.toPx(), y),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    Text(
                        text = "Tap sample craft stall to simulate instant scan & pay:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.topUpWallet(-350.0)
                                showScanPayDialog = false
                                viewModel.showNotification("Paid ₹350 to Raghurajpur Pattachitra Guild via QR")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Text("Raghurajpur (₹350)", fontSize = 10.5.sp)
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.topUpWallet(-850.0)
                                showScanPayDialog = false
                                viewModel.showNotification("Paid ₹850 to Bankura Dokra Stall via QR")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                        ) {
                            Text("Bankura (₹850)", fontSize = 10.5.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showScanPayDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // 3. Gaonova Pay Later Dialog
    if (showPayLaterDialog) {
        AlertDialog(
            onDismissRequest = { showPayLaterDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = IndigoMidnight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gaonova Pay Later", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = IndigoMidnight.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Pre-approved Credit Limit",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹10,000.00",
                                style = MaterialTheme.typography.titleLarge,
                                color = IndigoMidnight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Status: Instant Active · 0% Interest · 30 Days Free",
                                style = MaterialTheme.typography.labelSmall,
                                color = GiTagGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = "Benefits of Pay Later on Gaonova:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• 1-Tap instant checkout without entering UPI PINs or OTPs\n• Order limited-batch heritage handlooms before they sell out\n• Automatically settles from your linked bank account on the 5th of every month\n• 100% covered under Gaonova Buyer Authenticity Protection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showPayLaterDialog = false
                        viewModel.showNotification("Pay Later is active for your account!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoMidnight)
                ) {
                    Text("Use in Checkout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayLaterDialog = false }) {
                    Text("Done")
                }
            }
        )
    }

    // 4. Scratch Card Dialog with Interactive Scratch Animation & Starburst Rays
    if (showScratchCardDialog) {
        var scratchCount by remember { mutableIntStateOf(0) }
        val isScratched = scratchCount >= 3

        val rayTransition = rememberInfiniteTransition(label = "starburst_rays")
        val rotationAngle by rayTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "ray_rot"
        )

        AlertDialog(
            onDismissRequest = { showScratchCardDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = GoldenAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Patron Reward Scratch Card", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (!isScratched) "Tap the card 3 times to scratch and unveil your artisan reward! (${3 - scratchCount} taps left)" else "Congratulations! ₹150 Cashback credited to your wallet balance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                if (scratchCount < 3) {
                                    scratchCount++
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (scratchCount == 3) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.topUpWallet(150.0)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isScratched) {
                            // Spinning Sunburst Rays Canvas
                            Canvas(modifier = Modifier.fillMaxSize().rotate(rotationAngle)) {
                                val rayCount = 12
                                for (i in 0 until rayCount) {
                                    val angle = (i * 360f / rayCount) * (Math.PI / 180f).toFloat()
                                    val x = center.x + kotlin.math.cos(angle) * size.width
                                    val y = center.y + kotlin.math.sin(angle) * size.height
                                    drawLine(
                                        color = GoldenAmber.copy(alpha = 0.15f),
                                        start = center,
                                        end = Offset(x, y),
                                        strokeWidth = 24f
                                    )
                                }
                            }

                            // Glowing Prize Badge
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(GoldenAmber, SaffronGoldLight, TerracottaPrimary)
                                        )
                                    )
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "₹150",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "CASHBACK",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                            }
                        } else {
                            // Metallic Scratch Foil
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                TerracottaDark,
                                                TerracottaPrimary,
                                                TerracottaDark
                                            )
                                        )
                                    )
                                    .border(2.dp, SaffronGoldLight.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.TouchApp,
                                        contentDescription = null,
                                        tint = RawSilkCream,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "TAP TO SCRATCH",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = RawSilkCream,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${scratchCount}/3",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronGoldLight
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showScratchCardDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text(if (isScratched) "Collect Reward" else "Close")
                }
            }
        )
    }

    // 5. Gift Card Creation Dialog
    if (showGiftCardDialog) {
        var giftCardAmount by remember { mutableStateOf("1500") }
        var recipientName by remember { mutableStateOf("") }
        var giftMessage by remember { mutableStateOf("Wishing you the finest handmade heritage!") }

        AlertDialog(
            onDismissRequest = { showGiftCardDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Swadeshi Gift Card", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Send an authentic handcrafted art voucher valid on all GI registered crafts:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text("Recipient Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = giftCardAmount,
                        onValueChange = { giftCardAmount = it.filter { c -> c.isDigit() } },
                        label = { Text("Denomination (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = giftMessage,
                        onValueChange = { giftMessage = it },
                        label = { Text("Custom Greeting Note") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = giftCardAmount.toDoubleOrNull() ?: 1500.0
                        showGiftCardDialog = false
                        viewModel.showNotification("Generated Gift Card (GAONOVA-GIFT-${(1000..9999).random()}) of ₹$amount for ${recipientName.ifEmpty { "Friend" }}!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text("Generate & Share")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGiftCardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. Auto-Topup Setup Dialog
    if (showAutoTopupDialog) {
        AlertDialog(
            onDismissRequest = { showAutoTopupDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        tint = TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Patron Auto-Topup", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ensure your wallet always has enough funds to grab limited-drop handcrafted releases instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Rule: Auto-add ₹1,000 when balance falls below ₹500",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Linked Payment: UPI AutoPay (State Bank of India)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAutoTopupDialog = false
                        viewModel.showNotification("Auto-Topup rule enabled successfully!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text("Save Rule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAutoTopupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 7. Redeem Karma Coins Dialog
    if (showRedeemDialog) {
        AlertDialog(
            onDismissRequest = { showRedeemDialog = false },
            title = {
                Text(
                    text = "Redeem Karma Coins",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "You have ${walletState.karmaCoins} Patron Coins available (1 Coin = ₹1).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Select coins to convert directly into wallet balance:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(50, 100, 200, walletState.karmaCoins).filter { it <= walletState.karmaCoins && it > 0 }.forEach { coins ->
                            val isSelected = coinsToRedeem == coins
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    coinsToRedeem = coins
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) GoldenAmber else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$coins",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.redeemKarmaCoins(coinsToRedeem)
                        showRedeemDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenAmber)
                ) {
                    Text("Convert to ₹$coinsToRedeem Credit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRedeemDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SmartServiceItem(
    icon: ImageVector,
    title: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val itemScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "smart_service_scale"
    )

    Surface(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier.scale(itemScale)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeColor,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun ImpactProgressBar(label: String, percentage: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "${(percentage * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun WalletTransactionItem(
    txn: WalletTransaction,
    onDownloadInvoice: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadedSuccess by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                isExpanded = !isExpanded
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (txn.isCredit) GiTagGreenBg else PriceDropRedBg
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (txn.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (txn.isCredit) GiTagGreen else PriceDropRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = txn.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = txn.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                        Text(
                            text = "${txn.date} · Txn ID: ${txn.id.takeLast(6).uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (txn.isCredit) "+" else "-"}₹${"%,.2f".format(txn.amount)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (txn.isCredit) GiTagGreen else MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isExpanded) "Details" else "Trace",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Expanded Cryptographic Escrow & GST Invoice Audit Trail
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Escrow Clearance Status",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = txn.status,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GiTagGreen
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Cryptographic Hash",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "SHA256-${txn.id.take(8)}...OK",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Platform Commission",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹0.00 (100% to Artisan)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = GiTagGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                if (!isDownloading) {
                                    isDownloading = true
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    coroutineScope.launch {
                                        delay(700)
                                        isDownloading = false
                                        downloadedSuccess = true
                                        onDownloadInvoice()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (downloadedSuccess) GiTagGreenBg else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (downloadedSuccess) GiTagGreen else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth().height(34.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = TerracottaPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generating Verified Tax Receipt...", fontSize = 11.sp)
                            } else if (downloadedSuccess) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Official Invoice & Escrow Cert Downloaded", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download Verified Tax & GI Authenticity Receipt", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
