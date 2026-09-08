package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GaonovaFloatingNavBar
import com.example.ui.components.GaonovaTopBar
import com.example.ui.components.MittiSoilEmblem
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: GaonovaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            GaonovaTheme {
                GaonovaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaonovaApp(viewModel: GaonovaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val walletState by viewModel.walletState.collectAsState()
    val selectedState by viewModel.selectedStateFilter.collectAsState()
    val notification by viewModel.currentNotification.collectAsState()
    val isFirstLaunchWelcomeVisible by viewModel.isFirstLaunchWelcomeVisible.collectAsState()
    val locationContext by viewModel.locationContext.collectAsState()
    val locationStatus by viewModel.locationStatus.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val hapticFeedback = LocalHapticFeedback.current

    var showLocationSelectorDialog by remember { mutableStateOf(false) }

    // Foreground Location Permission Launcher (Coarse + Fine)
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        viewModel.handlePermissionResult(coarse || fine)
    }

    // Startup parallel location resolution if permission is already granted
    LaunchedEffect(Unit) {
        if (viewModel.locationService.hasLocationPermission()) {
            viewModel.startLocationResolution(isUserTriggered = false)
        }
        val currentUserName = viewModel.authState.value.currentUser?.name ?: "Patron"
        viewModel.checkAndTriggerDailyWelcome(currentUserName)
    }

    LaunchedEffect(notification) {
        notification?.let {
            snackbarHostState.showSnackbar(it.message)
            viewModel.clearNotification()
        }
    }

    // First Launch Emotional Welcome & Gratitude Screen
    if (isFirstLaunchWelcomeVisible) {
        WelcomeGratitudeScreen(
            locationContext = locationContext,
            locationStatus = locationStatus,
            onRequestLocationPermission = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onManualPinSelected = { pin -> viewModel.selectManualPinCode(pin) },
            onClusterSelected = { cluster -> viewModel.selectManualCluster(cluster) },
            onEnterGaonova = { viewModel.dismissFirstLaunchWelcome() }
        )
        return
    }

    // Intercept hardware back button
    BackHandler(enabled = currentScreen != ScreenDestination.Home) {
        viewModel.navigateBack()
    }

    val isTopLevelScreen = currentScreen is ScreenDestination.Home ||
            currentScreen is ScreenDestination.Explore ||
            currentScreen is ScreenDestination.Mitti ||
            currentScreen is ScreenDestination.Cart ||
            currentScreen is ScreenDestination.Account

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                val isPatronLoggedIn = authState.isLoggedIn && authState.currentUser != null && !authState.isGuest
                GaonovaFloatingNavBar(
                    currentScreen = currentScreen,
                    cartItems = cartItems,
                    isLoggedIn = isPatronLoggedIn,
                    onNavigate = { destination ->
                        viewModel.navigateTo(destination)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    when (targetState) {
                        is ScreenDestination.Mitti -> {
                            // Organic bloom transition into Mitti
                            (fadeIn(animationSpec = tween(MotionTokens.DurationEmphasis, easing = MotionTokens.DecelerateEasing)) +
                                    scaleIn(initialScale = 0.95f, animationSpec = tween(MotionTokens.DurationEmphasis, easing = MotionTokens.OrganicEasing)))
                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing)))
                        }
                        is ScreenDestination.Wallet -> {
                            // Smooth elevation scale and vertical slide into Wallet
                            (fadeIn(animationSpec = tween(MotionTokens.DurationEmphasis, easing = MotionTokens.DecelerateEasing)) +
                                    slideInVertically(initialOffsetY = { 70 }, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)) +
                                    scaleIn(initialScale = 0.95f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)))
                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing)) +
                                        scaleOut(targetScale = 0.98f, animationSpec = tween(MotionTokens.DurationMicro)))
                        }
                        is ScreenDestination.Explore -> {
                            // Directional explore transition
                            (fadeIn(animationSpec = tween(MotionTokens.DurationStandard)) +
                                    slideInHorizontally(initialOffsetX = { 50 }, animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing)))
                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationMicro)))
                        }
                        is ScreenDestination.ProductDetail, is ScreenDestination.ArtisanDetail -> {
                            // Detail Hero Push
                            (fadeIn(animationSpec = tween(MotionTokens.DurationStandard)) +
                                    slideInHorizontally(initialOffsetX = { 80 }, animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing)))
                                .togetherWith(slideOutHorizontally(targetOffsetX = { -40 }, animationSpec = tween(MotionTokens.DurationStandard)) + fadeOut(animationSpec = tween(MotionTokens.DurationMicro)))
                        }
                        is ScreenDestination.Account -> {
                            // Clean profile reveal
                            (fadeIn(animationSpec = tween(MotionTokens.DurationStandard)) +
                                    slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.DecelerateEasing)))
                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationMicro)))
                        }
                        else -> {
                            // Soft content reveal for Home and standard screens
                            (fadeIn(animationSpec = tween(MotionTokens.DurationStandard, easing = MotionTokens.StandardEasing)) +
                                    scaleIn(initialScale = 0.98f, animationSpec = tween(MotionTokens.DurationStandard)))
                                .togetherWith(fadeOut(animationSpec = tween(MotionTokens.DurationMicro, easing = MotionTokens.StandardEasing)))
                        }
                    }
                },
                label = "screen_navigation_animated_content"
            ) { screen ->
                when (screen) {
                    is ScreenDestination.Home -> HomeScreen(viewModel = viewModel)
                    is ScreenDestination.Explore -> ExploreScreen(viewModel = viewModel)
                    is ScreenDestination.Mitti -> MittiScreen(viewModel = viewModel)
                    is ScreenDestination.Account -> AccountScreen(viewModel = viewModel)
                    is ScreenDestination.Cart -> CartCheckoutScreen(viewModel = viewModel)
                    is ScreenDestination.Gifting -> GiftingScreen(viewModel = viewModel)
                    is ScreenDestination.OrdersAlerts -> OrdersAlertsScreen(viewModel = viewModel)
                    is ScreenDestination.ProductDetail -> ProductDetailScreen(productId = screen.productId, viewModel = viewModel)
                    is ScreenDestination.ArtisanDetail -> ArtisanDetailScreen(artisanId = screen.artisanId, viewModel = viewModel)
                    is ScreenDestination.Wallet -> WalletScreen(viewModel = viewModel)
                }
            }
        }
    }

    // State / Region / Location Selector Dialog
    if (showLocationSelectorDialog) {
        com.example.ui.components.LocationSelectorDialog(
            currentLocation = locationContext,
            isResolvingLocation = locationStatus is com.example.data.location.LocationStatus.Resolving,
            onDismiss = { showLocationSelectorDialog = false },
            onUseCurrentLocation = {
                viewModel.startLocationResolution(isUserTriggered = true)
            },
            onSelectPinCode = { pin ->
                viewModel.selectManualPinCode(pin)
            },
            onSelectCluster = { cluster ->
                viewModel.selectManualCluster(cluster)
            }
        )
    }
}
