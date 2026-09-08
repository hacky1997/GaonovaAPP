package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DemoUsers
import com.example.data.models.Order
import com.example.data.models.OrderStatus
import com.example.data.models.ShippingAddress
import com.example.data.models.UserProfile
import com.example.data.remote.dto.UserSessionDto
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsState()
    val allRegisteredUsers by viewModel.allRegisteredUsers.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val priceAlerts by viewModel.priceAlerts.collectAsState()
    val walletState by viewModel.walletState.collectAsState()
    val otpCountdown by viewModel.otpResendCountdown.collectAsState()
    val isOtpLoading by viewModel.isOtpLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val userSessions by viewModel.userSessions.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showSwitchUserDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    // Dynamic Auth mode for Guest / Logged out state:
    // 0: Fast Switcher, 1: Mobile OTP, 2: Email Sign In, 3: Create Account
    var guestAuthTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RawSilkCream)
            .testTag("account_screen"),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // =====================================================================
        // 1. PROFILE / AUTHENTICATION HEADER
        // =====================================================================
        item {
            if (authState.isLoggedIn && authState.currentUser != null) {
                val user = authState.currentUser!!
                LoggedInProfileHeader(
                    user = user,
                    onEditProfile = { showEditProfileDialog = true },
                    onSwitchAccount = { showSwitchUserDialog = true },
                    onLogout = { showLogoutConfirmDialog = true }
                )
            } else {
                GuestAuthHeader(
                    onLoginClick = { guestAuthTab = 0 }
                )
            }
        }

        // If NOT logged in, display the dynamic Login & Registration hub
        if (!authState.isLoggedIn || authState.currentUser == null) {
            item {
                DynamicAuthHub(
                    selectedTab = guestAuthTab,
                    onTabSelect = { 
                        guestAuthTab = it
                        viewModel.clearAuthError()
                    },
                    allUsers = allRegisteredUsers.ifEmpty { DemoUsers.allDemoUsers },
                    pendingOtpPhone = authState.pendingOtpPhone,
                    generatedOtp = authState.generatedOtpCode,
                    otpCountdown = otpCountdown,
                    isOtpLoading = isOtpLoading,
                    authError = authError,
                    onClearError = { viewModel.clearAuthError() },
                    onSelectUser = { viewModel.loginWithDemoUser(it) },
                    onRequestOtp = { viewModel.requestOtp(it) },
                    onVerifyOtp = { viewModel.verifyOtpAndLogin(it) },
                    onEmailLogin = { email, pass -> viewModel.loginWithCredentials(email, pass) },
                    onRegister = { name, email, phone, city -> viewModel.registerNewPatron(name, email, phone, city) },
                    onContinueGuest = { viewModel.continueAsGuest() }
                )
            }
        }

        // =====================================================================
        // 2. SWADESHI PATRON WALLET (Dynamic Balance & Karma Coins)
        // =====================================================================
        item {
            val isUserLoggedIn = authState.isLoggedIn && authState.currentUser != null
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable { viewModel.navigateTo(ScreenDestination.Wallet) }
                    .testTag("account_wallet_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isUserLoggedIn) SaffronGoldLight else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isUserLoggedIn) Icons.Default.AccountBalanceWallet else Icons.Default.Lock,
                                contentDescription = "Wallet",
                                tint = if (isUserLoggedIn) TerracottaDark else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isUserLoggedIn) "Swadeshi Patron Balance" else "Swadeshi Patron Wallet (Guest)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                            if (isUserLoggedIn) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹${walletState.balance.toInt()}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = TerracottaDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GiTagGreenBg
                                    ) {
                                        Text(
                                            text = "${walletState.karmaCoins} Karma Coins",
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.5.sp,
                                            color = GiTagGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "Sign in to access your passbook",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TerracottaPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Wallet",
                        tint = TextMuted
                    )
                }
            }
        }

        // =====================================================================
        // 3. YOUR ORDERS & TRACKING SECTION
        // =====================================================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Orders & Tracking",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    TextButton(onClick = { viewModel.navigateTo(ScreenDestination.OrdersAlerts) }) {
                        Text(
                            text = "View all (${orders.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = TerracottaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (orders.isNotEmpty()) {
                    val activeOrder = orders.first()
                    AccountActiveOrderCard(
                        order = activeOrder,
                        onTrackClick = { viewModel.navigateTo(ScreenDestination.OrdersAlerts) }
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            text = if (authState.isLoggedIn) "No recent orders. Explore verified village crafts on Home." else "Sign in to view your orders and track live artisan dispatches.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 4. SAVED CRAFTS & ALERTS
        // =====================================================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Saved & Activity",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        AccountActionRow(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Saved Wishlist Crafts",
                            subtitle = "${wishlistIds.size} heirloom items saved",
                            badge = if (wishlistIds.isNotEmpty()) "${wishlistIds.size}" else null,
                            onClick = { viewModel.navigateTo(ScreenDestination.Explore) }
                        )
                        Divider(color = CardStrokeBorderSubtle)
                        AccountActionRow(
                            icon = Icons.Outlined.NotificationsActive,
                            title = "Price Drop & Stock Alerts",
                            subtitle = "${priceAlerts.size} active craft monitors",
                            onClick = { viewModel.navigateTo(ScreenDestination.OrdersAlerts) }
                        )
                        Divider(color = CardStrokeBorderSubtle)
                        AccountActionRow(
                            icon = Icons.Outlined.CardGiftcard,
                            title = "Regional Gifting Guides",
                            subtitle = "Curated handloom & craft hampers",
                            onClick = { viewModel.navigateTo(ScreenDestination.Gifting) }
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 5. DELIVERY ADDRESS BOOK (Dynamic Address Management)
        // =====================================================================
        if (authState.isLoggedIn && authState.currentUser != null) {
            val user = authState.currentUser!!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Delivery Address Book (${user.addresses.size})",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        TextButton(onClick = { showAddAddressDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Add New",
                                style = MaterialTheme.typography.labelMedium,
                                color = TerracottaPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (user.addresses.isEmpty()) {
                                Text(
                                    text = "No addresses saved yet. Tap 'Add New' to add your delivery location.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                user.addresses.forEachIndexed { index, addr ->
                                    if (index > 0) {
                                        Divider(
                                            color = CardStrokeBorderSubtle,
                                            modifier = Modifier.padding(vertical = 10.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.LocationOn,
                                                contentDescription = null,
                                                tint = if (addr.isDefault) TerracottaPrimary else TextMuted,
                                                modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = addr.fullName,
                                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                        color = TextPrimary
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = if (addr.isDefault) GiTagGreenBg else SaffronGoldLight
                                                    ) {
                                                        Text(
                                                            text = addr.tag,
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 9.sp,
                                                            color = if (addr.isDefault) GiTagGreen else TerracottaDark,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    if (addr.isDefault) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = TerracottaContainerLight
                                                        ) {
                                                            Text(
                                                                text = "Default",
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontSize = 9.sp,
                                                                color = TerracottaDark,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${addr.street}, ${addr.city}, ${addr.state} - ${addr.postalCode}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary
                                                )
                                                Text(
                                                    text = "Phone: ${addr.phone}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextMuted
                                                )
                                            }
                                        }

                                        // Address Actions
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (!addr.isDefault) {
                                                TextButton(
                                                    onClick = { viewModel.setDefaultShippingAddress(addr.id) },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "Set Default",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TerracottaPrimary
                                                    )
                                                }
                                            }
                                            if (user.addresses.size > 1) {
                                                IconButton(
                                                    onClick = { viewModel.deleteShippingAddress(addr.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.DeleteOutline,
                                                        contentDescription = "Delete Address",
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // 6. SUPPORT & ABOUT GAONOVA
        // =====================================================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Support & Governance",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column {
                        AccountActionRow(
                            icon = Icons.Outlined.SupportAgent,
                            title = "Craft Desk & Human Officers",
                            subtitle = "Guaranteed response under 4 hours",
                            onClick = { viewModel.navigateTo(ScreenDestination.OrdersAlerts) }
                        )
                        Divider(color = CardStrokeBorderSubtle)
                        AccountActionRow(
                            icon = Icons.Outlined.Info,
                            title = "About Gaonova",
                            subtitle = "Founded by Mr. Arnab Roy & Mr. Sayak Naskar",
                            onClick = { showAboutDialog = true }
                        )
                        Divider(color = CardStrokeBorderSubtle)
                        AccountActionRow(
                            icon = Icons.Outlined.Translate,
                            title = "Language & Regional Preferences",
                            subtitle = "English · বাংলা · हिन्दी",
                            onClick = { viewModel.showNotification("Language preference: English (Active)") }
                        )
                        if (authState.isLoggedIn && authState.currentUser != null) {
                            Divider(color = CardStrokeBorderSubtle)
                            AccountActionRow(
                                icon = Icons.Outlined.Devices,
                                title = "Active Devices & Sessions",
                                subtitle = "Multi-device hardware encryption & revoke tokens",
                                onClick = { 
                                    viewModel.refreshSessions()
                                    showSessionsDialog = true 
                                }
                            )
                            Divider(color = CardStrokeBorderSubtle)
                            AccountActionRow(
                                icon = Icons.Outlined.DeleteForever,
                                title = "Account Privacy & Deletion",
                                subtitle = "Permanently purge account & isolated local data",
                                onClick = { showDeleteAccountDialog = true }
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // 7. SESSION MANAGEMENT / LOGOUT CARD
        // =====================================================================
        if (authState.isLoggedIn && authState.currentUser != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("logout_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TerracottaDark
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(TerracottaPrimary, TerracottaDark))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Log Out",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out of Patron Session",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 8. FOOTER BRAND SEAL
        // =====================================================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GAONOVA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = TerracottaDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Empowering 2,000+ Indian village artisan clusters",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "Version 2.4.0 · Dynamic Session Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }

    // =========================================================================
    // DIALOGS & BOTTOM SHEETS
    // =========================================================================

    // 1. Switch User / Account Dialog
    if (showSwitchUserDialog) {
        SwitchAccountDialog(
            currentUser = authState.currentUser,
            allUsers = allRegisteredUsers.ifEmpty { DemoUsers.allDemoUsers },
            onSelectUser = {
                viewModel.loginWithDemoUser(it)
                showSwitchUserDialog = false
            },
            onDismiss = { showSwitchUserDialog = false }
        )
    }

    // 2. Edit Profile Dialog
    if (showEditProfileDialog && authState.currentUser != null) {
        EditProfileDialog(
            user = authState.currentUser!!,
            onSave = { name, email, phone, location ->
                viewModel.updateProfile(name, email, phone, location)
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }

    // 3. Add Delivery Address Dialog
    if (showAddAddressDialog && authState.currentUser != null) {
        AddAddressDialog(
            defaultName = authState.currentUser!!.name,
            defaultPhone = authState.currentUser!!.phone,
            onSave = { tag, name, street, city, state, pin, phone ->
                viewModel.addShippingAddress(tag, name, street, city, state, pin, phone)
                showAddAddressDialog = false
            },
            onDismiss = { showAddAddressDialog = false }
        )
    }

    // 4. Logout Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = null,
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Confirm Sign Out",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of ${authState.currentUser?.name ?: "your account"}? Your cart and wishlist will remain saved locally.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout()
                        showLogoutConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // 5. About Gaonova Dialog
    if (showAboutDialog) {
        AboutGaonovaDialog(onDismiss = { showAboutDialog = false })
    }

    // 6. Active Multi-Device Sessions Dialog
    if (showSessionsDialog && authState.currentUser != null) {
        SessionsManagementDialog(
            sessions = userSessions,
            onRevoke = { sessionId -> viewModel.revokeSession(sessionId) },
            onDismiss = { showSessionsDialog = false }
        )
    }

    // 7. Delete Account Confirmation Dialog
    if (showDeleteAccountDialog && authState.currentUser != null) {
        DeleteAccountConfirmationDialog(
            userName = authState.currentUser?.name ?: "Patron",
            onConfirmDelete = {
                viewModel.deleteAccountPermanently()
                showDeleteAccountDialog = false
            },
            onDismiss = { showDeleteAccountDialog = false }
        )
    }
}

// =============================================================================
// SUB-COMPONENTS & DIALOGS
// =============================================================================

@Composable
fun LoggedInProfileHeader(
    user: UserProfile,
    onEditProfile: () -> Unit,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dynamic Avatar with Initials
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(TerracottaPrimary, SaffronGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.avatarInitials,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GiTagGreenBg
                        ) {
                            Text(
                                text = user.role,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = GiTagGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${user.phone} · ${user.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 1
                    )
                    Text(
                        text = "Patron since ${user.memberSince} · ${user.location}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Pills (Edit Profile, Switch Account, Log Out)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Profile",
                        modifier = Modifier.size(14.dp),
                        tint = TerracottaDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.labelSmall,
                        color = TerracottaDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onSwitchAccount,
                    modifier = Modifier.weight(1.1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwitchAccount,
                        contentDescription = "Switch Account",
                        modifier = Modifier.size(14.dp),
                        tint = TerracottaDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Switch User",
                        style = MaterialTheme.typography.labelSmall,
                        color = TerracottaDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RawSilkCream)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = "Log Out",
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GuestAuthHeader(
    onLoginClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SaffronGoldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = null,
                        tint = TerracottaDark,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Namaste, Guest Patron",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SaffronGoldLight
                        ) {
                            Text(
                                text = "Atithi / Guest",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = TerracottaDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sign in to save orders, track dispatches, and earn Karma Coins.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun DynamicAuthHub(
    selectedTab: Int,
    onTabSelect: (Int) -> Unit,
    allUsers: List<UserProfile>,
    pendingOtpPhone: String?,
    generatedOtp: String?,
    otpCountdown: Int = 0,
    isOtpLoading: Boolean = false,
    authError: String? = null,
    onClearError: () -> Unit = {},
    onSelectUser: (UserProfile) -> Unit,
    onRequestOtp: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onEmailLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String) -> Unit,
    onContinueGuest: () -> Unit
) {
    var phoneInput by remember { mutableStateOf(pendingOtpPhone ?: "") }
    var otpInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regCity by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("dynamic_auth_hub"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Sign In to Gaonova",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                text = "Select your preferred authentication method below:",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            // Auth Error Banner
            if (authError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authError,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = RawSilkCream,
                contentColor = TerracottaPrimary,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelect(0) },
                    text = { Text("⚡ 1-Tap Switch", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelect(1) },
                    text = { Text("📱 Phone OTP", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { onTabSelect(2) },
                    text = { Text("✉️ Email", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { onTabSelect(3) },
                    text = { Text("✨ Register", fontSize = 12.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                // TAB 0: Fast Switcher Cards
                0 -> {
                    Column {
                        Text(
                            text = "Tap any patron profile to sign in instantly:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        allUsers.forEach { user ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onSelectUser(user) },
                                shape = RoundedCornerShape(12.dp),
                                color = RawSilkCream,
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(listOf(TerracottaPrimary, SaffronGold))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.avatarInitials,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GiTagGreenBg
                                            ) {
                                                Text(
                                                    text = user.role,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 8.5.sp,
                                                    color = GiTagGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${user.location} · ₹${user.walletBalance.toInt()} Balance",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Login,
                                        contentDescription = "Log in as ${user.name}",
                                        tint = TerracottaPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 1: Mobile Phone OTP
                1 -> {
                    Column {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Mobile Phone Number") },
                            placeholder = { Text("+91 98300 12345") },
                            leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { onRequestOtp(phoneInput.ifBlank { "98300 12345" }) },
                            enabled = !isOtpLoading && (otpCountdown == 0 || pendingOtpPhone != phoneInput),
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                        ) {
                            if (isOtpLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sending SMS OTP...")
                            } else if (otpCountdown > 0 && pendingOtpPhone == phoneInput) {
                                Text("Resend OTP in ${otpCountdown}s")
                            } else {
                                Text("Send 6-Digit OTP Code")
                            }
                        }

                        if (generatedOtp != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SaffronGoldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Simulated SMS Gateway Received:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TerracottaDark
                                        )
                                        Text(
                                            text = "OTP Code: $generatedOtp",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TerracottaDark
                                        )
                                    }
                                    TextButton(onClick = { otpInput = generatedOtp }) {
                                        Text("Auto-Fill", color = TerracottaPrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Enter 6-digit OTP") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onVerifyOtp(otpInput) },
                                enabled = !isOtpLoading && otpInput.length == 6,
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GiTagGreen)
                            ) {
                                if (isOtpLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verifying...")
                                } else {
                                    Text("Verify OTP & Sign In")
                                }
                            }
                        }
                    }
                }

                // TAB 2: Email & Password
                2 -> {
                    Column {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("sayak@gaonova.com") },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onEmailLogin(
                                    emailInput.ifBlank { "sayak@gaonova.com" },
                                    passwordInput.ifBlank { "patron123" }
                                )
                            },
                            enabled = !isOtpLoading,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                        ) {
                            if (isOtpLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Authenticating...")
                            } else {
                                Text("Sign In with Email")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                onEmailLogin("sayak.naskar@gaonova.com", "google_oauth_token")
                            },
                            enabled = !isOtpLoading,
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = TerracottaPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google Patron ID", color = TextPrimary)
                        }
                    }
                }

                // TAB 3: Register New Patron
                3 -> {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GiTagGreenBg,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GiTagGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎁 Joining Bonus: ₹500 Patron Credit + 100 Karma Coins",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GiTagGreen
                                )
                            }
                        }

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("Subhasish Das") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("subhasish@example.com") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+91 98311 55443") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regCity,
                            onValueChange = { regCity = it },
                            label = { Text("City / Region") },
                            placeholder = { Text("Kolkata, West Bengal") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                onRegister(regName, regEmail, regPhone, regCity.ifBlank { "Kolkata, West Bengal" })
                            },
                            enabled = !isOtpLoading && regName.isNotBlank() && regPhone.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                        ) {
                            if (isOtpLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Account...")
                            } else {
                                Text("Create Patron Account & Join")
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// MODAL DIALOG IMPLEMENTATIONS
// =============================================================================

@Composable
fun SwitchAccountDialog(
    currentUser: UserProfile?,
    allUsers: List<UserProfile>,
    onSelectUser: (UserProfile) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.SwitchAccount, contentDescription = null, tint = TerracottaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Switch Patron Session", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select a registered user profile to switch active session:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                allUsers.forEach { user ->
                    val isSelected = user.id == currentUser?.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectUser(user) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) SaffronGoldLight else RawSilkCream,
                        border = if (isSelected) BorderStroke(1.5.dp, TerracottaPrimary) else CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(TerracottaPrimary, SaffronGold))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.avatarInitials,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.name,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GiTagGreenBg
                                        ) {
                                            Text(
                                                text = "Active",
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 8.sp,
                                                color = GiTagGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${user.role} · ${user.location}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TerracottaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun EditProfileDialog(
    user: UserProfile,
    onSave: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf(user.phone) }
    var location by remember { mutableStateOf(user.location) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = TerracottaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile Details", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (City, State)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, email, phone, location) },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AddAddressDialog(
    defaultName: String,
    defaultPhone: String,
    onSave: (String, String, String, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tag by remember { mutableStateOf("Home") }
    var name by remember { mutableStateOf(defaultName) }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Kolkata") }
    var state by remember { mutableStateOf("West Bengal") }
    var postalCode by remember { mutableStateOf("700091") }
    var phone by remember { mutableStateOf(defaultPhone) }

    val tags = listOf("Home", "Work", "Studio", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AddLocationAlt, contentDescription = null, tint = TerracottaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Delivery Address", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Tag Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { t ->
                        FilterChip(
                            selected = tag == t,
                            onClick = { tag = t },
                            label = { Text(t, fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street Address / Flat No.") },
                    placeholder = { Text("Flat 4B, Heritage Enclave, Sector V") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("PIN Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(tag, name, street.ifBlank { "Sector V, Salt Lake" }, city, state, postalCode, phone)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
            ) {
                Text("Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AboutGaonovaDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = TerracottaPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("About Gaonova", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Gaonova connects conscious patrons directly with authentic Indian village crafts, handloom weavers, and ancestral artisan guilds.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RawSilkCream,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Founding Leadership:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TerracottaDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Mr. Arnab Roy\n• Mr. Sayak Naskar",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Every item features certified geographical provenance, fair artisan remuneration, and verifiable GI credentials.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TerracottaPrimary)
            }
        }
    )
}

@Composable
fun AccountActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TerracottaPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TerracottaContainerLight
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TerracottaDark,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AccountActiveOrderCard(
    order: Order,
    onTrackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTrackClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (order.status) {
                        OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> GiTagGreenBg
                        OrderStatus.ARTISAN_PREPARING -> SaffronGoldLight
                        OrderStatus.DELIVERED -> GiTagGreenBg
                        else -> RawSilkCream
                    }
                ) {
                    Text(
                        text = order.status.label,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (order.status) {
                            OrderStatus.SHIPPED, OrderStatus.OUT_FOR_DELIVERY -> GiTagGreen
                            OrderStatus.ARTISAN_PREPARING -> TerracottaDark
                            else -> TextSecondary
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach { item ->
                Text(
                    text = "• ${item.product.name} (x${item.quantity})",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Est. Delivery: ${order.estimatedDeliveryDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Live Tracking",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SessionsManagementDialog(
    sessions: List<UserSessionDto>,
    onRevoke: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Devices, contentDescription = null, tint = TerracottaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Active Multi-Device Sessions", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Hardware-backed keystore tokens active across your devices. You can revoke access for any remote session.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (sessions.isEmpty()) {
                    Text(
                        text = "No other active sessions found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    sessions.forEach { session ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (session.isCurrentDevice) SaffronGoldLight else RawSilkCream,
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when {
                                        session.platform.contains("Android", ignoreCase = true) -> Icons.Outlined.PhoneAndroid
                                        session.platform.contains("iOS", ignoreCase = true) -> Icons.Outlined.PhoneIphone
                                        else -> Icons.Outlined.Laptop
                                    },
                                    contentDescription = null,
                                    tint = TerracottaDark,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = session.deviceName,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        if (session.isCurrentDevice) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GiTagGreenBg
                                            ) {
                                                Text(
                                                    text = "This Device",
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 8.sp,
                                                    color = GiTagGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${session.platform} · IP: ${session.ipAddress}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Session: ${session.sessionId.take(12)}...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                if (!session.isCurrentDevice) {
                                    TextButton(
                                        onClick = { onRevoke(session.sessionId) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Revoke",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
fun DeleteAccountConfirmationDialog(
    userName: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Delete Account & Purge Data",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "This will permanently delete the patron profile for $userName. All associated data will be purged immediately:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• All active cart items and stored wishlists\n• Local and cloud order histories\n• Shipping addresses and contact info\n• Karma coin balances and wallet credentials\n• Hardware cryptographic keys on this device",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "To confirm deletion, please type DELETE below:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    placeholder = { Text("DELETE") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                enabled = confirmText.trim().equals("DELETE", ignoreCase = false),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Permanently Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
