package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.data.models.*
import com.example.data.repository.ProductRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

data class HeroSlideData(
    val title: String,
    val subtitle: String,
    val badge: String,
    val gradientColors: List<Color>,
    val targetDestination: ScreenDestination,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class CraftClusterItem(
    val name: String,
    val region: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val count: String,
    val gradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedState by viewModel.selectedStateFilter.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val walletState by viewModel.walletState.collectAsState()
    val isWelcomeDismissed by viewModel.isDailyWelcomeDismissed.collectAsState()
    val isGiOnly by viewModel.isGiOnlyFilter.collectAsState()
    val isPriceDropOnly by viewModel.isPriceDropOnly.collectAsState()
    val isVoiceActive by viewModel.isVoiceSearchActive.collectAsState()
    val isLensActive by viewModel.isLensSearchActive.collectAsState()

    val locationContext by viewModel.locationContext.collectAsState()
    val locationStatus by viewModel.locationStatus.collectAsState()
    val activeUser by viewModel.activeUser.collectAsState()
    val isDailyGreetingActive by viewModel.isDailyWelcomeGreetingActive.collectAsState()
    val dailyGreetingText by viewModel.dailyWelcomeGreetingText.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var isAdvantageExpanded by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current

    // Entry animation orchestration
    val screenEntrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        screenEntrance.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val productOfTheDay = remember {
        ProductRepository.sampleProducts.firstOrNull { it.isProductOfTheDay }
            ?: ProductRepository.sampleProducts.first()
    }

    val craftClusters = remember {
        listOf(
            CraftClusterItem("Kashmir Silks", "Srinagar & Baramulla", Icons.Default.Spa, "42 Weavers", listOf(Color(0xFF144533), Color(0xFF2E6F56))),
            CraftClusterItem("Jaipur Ceramics", "Sanganer & Amer", Icons.Default.Diamond, "28 Guilds", listOf(Color(0xFF1A355B), Color(0xFF3B68A2))),
            CraftClusterItem("Bankura Clay", "Bishnupur Hub", Icons.Default.Yard, "35 Families", listOf(Color(0xFF7A2E14), Color(0xFFB85324))),
            CraftClusterItem("Varanasi Weaves", "Kashi Guild", Icons.Default.AllInclusive, "64 Masters", listOf(Color(0xFF4A2810), Color(0xFF8B4D22)))
        )
    }

    val heroSlides = remember {
        listOf(
            HeroSlideData(
                title = "Authentic Village Handlooms & Silks",
                subtitle = "Direct from master weavers in Bengal, Kashmir & Assam. 100% natural pure yarn.",
                badge = "HERITAGE HANDLOOM",
                gradientColors = listOf(Color(0xFF7A2E14), Color(0xFFB85324), Color(0xFFD68A42)),
                targetDestination = ScreenDestination.Explore,
                icon = Icons.Default.Spa
            ),
            HeroSlideData(
                title = "GI-Certified Regional Heritage",
                subtitle = "Govt. verified geographic provenance from 28 states & 500+ artisan clusters.",
                badge = "GI REGISTRY CERTIFIED",
                gradientColors = listOf(Color(0xFF144533), Color(0xFF236B52), Color(0xFF5D9D84)),
                targetDestination = ScreenDestination.Explore,
                icon = Icons.Default.Verified
            ),
            HeroSlideData(
                title = "Jaipur Blue Pottery & Royal Ceramics",
                subtitle = "Generational quartz & copper oxide glazes sculpted by master guild artisans.",
                badge = "ROYAL CERAMICS",
                gradientColors = listOf(Color(0xFF1A355B), Color(0xFF28548A), Color(0xFF5B89BD)),
                targetDestination = ScreenDestination.Explore,
                icon = Icons.Default.Diamond
            ),
            HeroSlideData(
                title = "85% Direct Value to Rural Artisans",
                subtitle = "Empowering generational craft families with transparent, zero-middleman fair pay.",
                badge = "FAIR CRAFT DIRECT",
                gradientColors = listOf(Color(0xFF4A2810), Color(0xFF78451D), Color(0xFFB5753C)),
                targetDestination = ScreenDestination.Explore,
                icon = Icons.Default.Handshake
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { heroSlides.size })

    // Auto-slide loop with smooth easing
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5200)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.currentPage + 1) % heroSlides.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    // Dynamic animated colors that smoothly update on slide change
    val currentSlide = heroSlides[pagerState.currentPage]
    val activeTopGradientStart by animateColorAsState(
        targetValue = currentSlide.gradientColors.first(),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "activeTopGradientStart"
    )
    val activeTopGradientEnd by animateColorAsState(
        targetValue = currentSlide.gradientColors.getOrElse(1) { currentSlide.gradientColors.first() },
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "activeTopGradientEnd"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Atmospheric Multi-stop Ambient Gradient Glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            activeTopGradientStart.copy(alpha = 0.65f),
                            activeTopGradientEnd.copy(alpha = 0.45f),
                            activeTopGradientEnd.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Radial ambient illumination
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            val w = size.width
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        activeTopGradientStart.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, 40f),
                    radius = w * 0.85f
                )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val progress = screenEntrance.value
                    alpha = progress.coerceIn(0f, 1f)
                    translationY = (1f - progress) * 40f
                }
                .testTag("home_screen_scroll"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Sleek Search Bar & Zomato-Style Wallet Icon
            item(key = "search_bar_item") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, activeTopGradientStart.copy(alpha = 0.35f)),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = activeTopGradientStart,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search crafts, weaves, GI regions...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF6B7280),
                                        fontSize = 13.5.sp,
                                        maxLines = 1
                                    )
                                }
                                androidx.compose.foundation.text.BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { newValue -> viewModel.searchQuery.value = newValue },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF111827),
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("global_search_input")
                                )
                            }

                            // Actions inside search bar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.searchQuery.value = "" },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = Color(0xFF565959),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.isLensSearchActive.value = true },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("search_camera_scanner_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CenterFocusStrong,
                                        contentDescription = "Camera Scanner",
                                        tint = activeTopGradientStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.isVoiceSearchActive.value = true },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("search_speech_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Speech Search",
                                        tint = activeTopGradientStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ZomatoStyleWalletHeaderButton(
                        onClick = {
                            viewModel.navigateTo(ScreenDestination.Wallet)
                        },
                        gradientStart = activeTopGradientStart,
                        gradientEnd = activeTopGradientEnd
                    )
                }
            }

            // 2. Delivery Location Bar
            item(key = "amazon_location_bar_item") {
                val isResolving = locationStatus is com.example.data.location.LocationStatus.Resolving
                val firstName = activeUser?.name?.trim()?.split(" ")?.firstOrNull() ?: ""
                val deliveryCityPin = buildString {
                    append(locationContext.city)
                    if (!locationContext.postalCode.isNullOrBlank()) {
                        append(" ")
                        append(locationContext.postalCode)
                    }
                }
                val deliveryLabel = if (firstName.isNotBlank()) {
                    "Deliver to $firstName - $deliveryCityPin"
                } else {
                    "Deliver to $deliveryCityPin"
                }

                Surface(
                    onClick = { showLocationDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, activeTopGradientStart.copy(alpha = 0.35f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 3.dp)
                        .testTag("amazon_location_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        activeTopGradientStart.copy(alpha = 0.20f),
                                        activeTopGradientEnd.copy(alpha = 0.10f),
                                        Color.White.copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(activeTopGradientStart.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = "Delivery Location",
                                tint = activeTopGradientStart,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isResolving) "Locating delivery address..." else deliveryLabel,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp
                            ),
                            color = Color(0xFF0F1111),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Choose Location",
                            tint = Color(0xFF565959),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 3. Hero Banner Slider
            item(key = "amazon_hero_card_slider") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amazon_hero_slider")
                    ) { pageIndex ->
                        val slide = heroSlides[pageIndex]
                        var isHeroPressed by remember { mutableStateOf(false) }
                        val heroScale by animateFloatAsState(
                            targetValue = if (isHeroPressed) 0.98f else 1f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "hero_scale"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scale(heroScale)
                                .clickable {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.navigateTo(slide.targetDestination)
                                }
                                .testTag("amazon_hero_card_$pageIndex"),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = slide.gradientColors,
                                            start = Offset(0f, 0f),
                                            end = Offset(1000f, 1000f)
                                        )
                                    )
                            ) {
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    val w = size.width
                                    val h = size.height
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.14f),
                                        radius = h * 0.75f,
                                        center = Offset(w * 0.95f, h * 0.15f)
                                    )
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.08f),
                                        radius = h * 0.55f,
                                        center = Offset(w * 0.05f, h * 0.95f)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.15f),
                                                    Color.Black.copy(alpha = 0.48f)
                                                )
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 18.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color.Black.copy(alpha = 0.40f),
                                        border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.6f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = slide.icon,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD814),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = slide.badge,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.8.sp
                                                ),
                                                color = Color.White,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = slide.title,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            lineHeight = 26.sp
                                        ),
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = slide.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 18.sp,
                                            color = Color.White.copy(alpha = 0.92f)
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { viewModel.navigateTo(slide.targetDestination) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFFD814),
                                            contentColor = Color(0xFF111111)
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                                        modifier = Modifier
                                            .heightIn(min = 40.dp)
                                            .testTag("hero_explore_now_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Explore Collection",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.5.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp),
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

            // 6. Interactive GI Craft Cluster Hubs
            item(key = "craft_cluster_radar") {
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
                            text = "ICONIC CRAFT HUBS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "View Map →",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { viewModel.navigateTo(ScreenDestination.Explore) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        craftClusters.forEach { cluster ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.searchQuery.value = cluster.name.split(" ").first()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, cluster.gradient.first().copy(alpha = 0.3f)),
                                shadowElevation = 1.5.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(cluster.gradient.first().copy(alpha = 0.12f), Color.White)
                                            )
                                        )
                                        .padding(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(cluster.gradient.first()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cluster.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = cluster.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = cluster.count,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GiTagGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Village Direct Advantage Accordion
            item(key = "village_standout_pillars") {
                val arrowRotation by animateFloatAsState(
                    targetValue = if (isAdvantageExpanded) 180f else 0f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "advantage_arrow_rotation"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            isAdvantageExpanded = !isAdvantageExpanded
                        }
                        .testTag("village_standout_pillars"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAdvantageExpanded) Color(0xFFFCFAF7) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isAdvantageExpanded) TerracottaDark.copy(alpha = 0.35f) else Color(0xFFE5E7EB)
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(TerracottaDark, TerracottaPrimary)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "THE VILLAGE DIRECT ADVANTAGE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = TerracottaDark,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = GiTagGreenBg
                                        ) {
                                            Text(
                                                text = "0% Middlemen",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = GiTagGreen,
                                                fontSize = 9.sp,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    if (!isAdvantageExpanded) {
                                        Text(
                                            text = "100% Pure • GI Tagged • 85% Fair Pay • 1-2d Express",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isAdvantageExpanded) TerracottaContainerLight else Color(0xFFF3F4F6)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isAdvantageExpanded) "Collapse" else "Expand",
                                    tint = if (isAdvantageExpanded) TerracottaDark else Color(0xFF565959),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .rotate(arrowRotation)
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = isAdvantageExpanded,
                            enter = expandVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn(animationSpec = tween(220)),
                            exit = shrinkVertically(
                                animationSpec = spring(stiffness = Spring.StiffnessMedium)
                            ) + fadeOut(animationSpec = tween(150))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, MittiRoyalGreen.copy(alpha = 0.22f)),
                                        shadowElevation = 1.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            MittiGreenSurface.copy(alpha = 0.5f),
                                                            Color.White
                                                        )
                                                    )
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(MittiGreenSurface),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Spa,
                                                        contentDescription = null,
                                                        tint = MittiRoyalGreen,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = MittiRoyalGreen.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "ORGANIC",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = MittiRoyalGreen,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "100% Pure Soil",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF0F1111)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Natural river clay, non-toxic & zero lead glazing.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, GiTagGreen.copy(alpha = 0.22f)),
                                        shadowElevation = 1.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            GiTagGreenBg.copy(alpha = 0.5f),
                                                            Color.White
                                                        )
                                                    )
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(GiTagGreenBg),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Verified,
                                                        contentDescription = null,
                                                        tint = GiTagGreen,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = GiTagGreen.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "GI TAGGED",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = GiTagGreen,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "GI Certified",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF0F1111)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Govt. registry audited regional provenance.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, TerracottaDark.copy(alpha = 0.22f)),
                                        shadowElevation = 1.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            SaffronGoldLight.copy(alpha = 0.5f),
                                                            Color.White
                                                        )
                                                    )
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(SaffronGoldLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CurrencyRupee,
                                                        contentDescription = null,
                                                        tint = TerracottaDark,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = TerracottaDark.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "FAIR PAY",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = TerracottaDark,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "85% Direct Pay",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF0F1111)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Zero aggregator commissions directly into artisan bank.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp)),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, TerracottaPrimary.copy(alpha = 0.22f)),
                                        shadowElevation = 1.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(
                                                            TerracottaContainerLight.copy(alpha = 0.5f),
                                                            Color.White
                                                        )
                                                    )
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(TerracottaContainerLight),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.LocalShipping,
                                                        contentDescription = null,
                                                        tint = TerracottaPrimary,
                                                        modifier = Modifier.size(17.dp)
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = TerracottaPrimary.copy(alpha = 0.12f)
                                                ) {
                                                    Text(
                                                        text = "EXPRESS",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = TerracottaPrimary,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "1-2 Days Express",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF0F1111)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "Custom shockproof packaging from regional hub.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 8. Category Filter Chips with Tactile Feedback
            item(key = "category_carousel_item") {
                LazyRow(
                    modifier = Modifier.padding(vertical = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isAllSelected = selectedCategory == null
                        FilterChip(
                            selected = isAllSelected,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectedCategory.value = null
                            },
                            label = {
                                Text(
                                    text = "All Crafts",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            modifier = Modifier.testTag("category_all_chip"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }

                    items(ProductCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.selectedCategory.value = if (isSelected) null else cat
                            },
                            label = {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("category_${cat.name}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 9. Secondary Quick Filters
            item(key = "secondary_filter_chips_item") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedFilterChip(
                        selected = isGiOnly,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.isGiOnlyFilter.value = !isGiOnly
                        },
                        label = { Text("GI Certified Only", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (isGiOnly) Color.White else GiTagGreen,
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = GiTagGreen,
                            selectedLabelColor = Color.White
                        )
                    )

                    ElevatedFilterChip(
                        selected = isPriceDropOnly,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.isPriceDropOnly.value = !isPriceDropOnly
                        },
                        label = { Text("Recent Price Drops", style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isPriceDropOnly) Color.White else PriceDropRed,
                                modifier = Modifier.size(13.dp)
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = PriceDropRed,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 10. Section Header with Explore Map link
            item(key = "section_header_item") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedState != "All India") "$selectedState Regional Crafts" else "Curated Heritage Crafts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.3).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${products.size} authentic items with verified artisan provenance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.Explore) },
                        modifier = Modifier.testTag("explore_map_button")
                    ) {
                        Text(
                            text = "Explore Map",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 11. Product Grid
            if (products.isEmpty()) {
                item(key = "empty_state_item") {
                    EmptyStateView(
                        icon = Icons.Default.SearchOff,
                        title = "No crafts match your filter",
                        subtitle = "Try adjusting your search criteria or ask the AI Sage to discover regional alternatives.",
                        actionText = "Reset All Filters",
                        onActionClick = {
                            viewModel.searchQuery.value = ""
                            viewModel.selectedCategory.value = null
                            viewModel.selectedStateFilter.value = "All India"
                            viewModel.isGiOnlyFilter.value = false
                            viewModel.isPriceDropOnly.value = false
                        }
                    )
                }
            } else {
                val chunked = products.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { prod ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = prod,
                                    isWishlisted = wishlistIds.contains(prod.id),
                                    onProductClick = { viewModel.navigateTo(ScreenDestination.ProductDetail(prod.id)) },
                                    onWishlistToggle = { viewModel.toggleWishlist(prod.id) },
                                    onQuickAdd = { viewModel.addToCart(prod, 1) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // 12. Curated Cultural Gifting Concierge Spotlight Card
            item(key = "gifting_spotlight_item") {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { viewModel.navigateTo(ScreenDestination.Gifting) }
                        .testTag("cultural_gifting_spotlight"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, SaffronGold.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SaffronGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cultural Gifting Concierge",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Curated traditional gifts with artisan blessing cards & eco-packaging.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to Gifting",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    // Modals: Speech Search & Google Lens Scanner
    if (isVoiceActive) {
        SpeechSearchDialog(
            onDismiss = { viewModel.isVoiceSearchActive.value = false },
            onSpeechResult = { recognizedQuery ->
                viewModel.searchQuery.value = recognizedQuery
                viewModel.isVoiceSearchActive.value = false
            }
        )
    }

    if (isLensActive) {
        GoogleLensScannerDialog(
            onDismiss = { viewModel.isLensSearchActive.value = false },
            onCraftIdentified = { identifiedCraft ->
                viewModel.searchQuery.value = identifiedCraft
                viewModel.isLensSearchActive.value = false
                viewModel.showNotification("Google Lens identified: $identifiedCraft")
            }
        )
    }

    if (showLocationDialog) {
        LocationSelectorDialog(
            currentLocation = locationContext,
            userProfile = activeUser,
            isResolvingLocation = locationStatus is com.example.data.location.LocationStatus.Resolving,
            onDismiss = { showLocationDialog = false },
            onUseCurrentLocation = {
                viewModel.startLocationResolution(isUserTriggered = true)
            },
            onSelectPinCode = { pin ->
                viewModel.selectManualPinCode(pin)
            },
            onSelectAddress = { address ->
                viewModel.selectManualPinCode(address.postalCode)
            },
            onManageAddresses = {
                viewModel.navigateTo(ScreenDestination.Account)
            }
        )
    }
}
