package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.location.*
import com.example.ui.components.LocationSelectorDialog
import com.example.ui.components.MittiSoilEmblem
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * Curated Indian craft stories rotating dynamically to bring fresh brand storytelling.
 */
data class VillageCraftStory(
    val id: String,
    val title: String,
    val craftType: String,
    val region: String,
    val villageHub: String,
    val description: String,
    val imageUrl: String
)

val rotatingCraftStories = listOf(
    VillageCraftStory(
        id = "handloom",
        title = "The Weaver's Loom",
        craftType = "Mulberry Silk & Khadi",
        region = "Phulia & Varanasi",
        villageHub = "KOL / VNS",
        description = "Generational master weavers passing the wooden shuttle through thousands of organic warp threads.",
        imageUrl = "https://images.unsplash.com/photo-1606744824163-985d376605aa?auto=format&fit=crop&w=1200&q=80"
    ),
    VillageCraftStory(
        id = "pottery",
        title = "The Potter's Wheel",
        craftType = "Terracotta & Riverbed Clay",
        region = "Bankura & Khurja",
        villageHub = "KOL / DEL",
        description = "Alluvial river clay molded on manual spinning wheels, fired in wood-fueled village kilns.",
        imageUrl = "https://images.unsplash.com/photo-1590736969955-71cc94801759?auto=format&fit=crop&w=1200&q=80"
    ),
    VillageCraftStory(
        id = "dokra",
        title = "Lost-Wax Dokra Casting",
        craftType = "Ancient Bell-Metal Craft",
        region = "Bikna & Dhenkanal",
        villageHub = "KOL / BBI",
        description = "4,000-year-old unbroken metallurgy technique using beeswax coils and red earth moulds.",
        imageUrl = "https://images.unsplash.com/photo-1578749556568-bc2c40e68b61?auto=format&fit=crop&w=1200&q=80"
    ),
    VillageCraftStory(
        id = "blockprint",
        title = "The Teakwood Stamp",
        craftType = "Hand-Block Printing",
        region = "Bagru & Sanganer",
        villageHub = "JAI",
        description = "Hand-carved teakwood blocks dipped in natural fermented indigo and madder root dye.",
        imageUrl = "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=1200&q=80"
    ),
    VillageCraftStory(
        id = "tea",
        title = "Himalayan Mist Harvest",
        craftType = "Single-Origin Darjeeling Tea",
        region = "Kurseong & Darjeeling",
        villageHub = "GAU / SIL",
        description = "Hand-plucked tender two leaves and a bud, dried in high-altitude mountain air.",
        imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=1200&q=80"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeGratitudeScreen(
    locationContext: LocationContext,
    locationStatus: LocationStatus,
    onRequestLocationPermission: () -> Unit,
    onManualPinSelected: (String) -> Unit,
    onClusterSelected: (PostalClusterInfo) -> Unit,
    onEnterGaonova: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLocationSelector by remember { mutableStateOf(false) }

    // Dynamic rotation of featured village story based on day of year
    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    var currentStoryIndex by remember { mutableIntStateOf(dayOfYear % rotatingCraftStories.size) }
    val featuredStory = rotatingCraftStories[currentStoryIndex]

    // Organic Cinematic Animation Choreography Stages
    var animStage by remember { mutableIntStateOf(0) }

    // Root/Thread organic path animation progress
    val threadProgress by animateFloatAsState(
        targetValue = if (animStage >= 1) 1f else 0f,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "thread_progress"
    )

    // Subtle continuous camera drift / parallax for hero craftsmanship visual
    val infiniteTransition = rememberInfiniteTransition(label = "hero_cinematic_motion")
    val heroZoomScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_zoom"
    )
    val earthGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "earth_glow"
    )

    // Step-by-step sequential dynamic reveal (Total time ~2.8s, fast & fluid)
    LaunchedEffect(Unit) {
        delay(80)
        animStage = 1 // Soil & Thread root emerges
        delay(350)
        animStage = 2 // Gaonova Seal & Brand settled
        delay(400)
        animStage = 3 // Craft Hero visual expands
        delay(450)
        animStage = 4 // Warm gratitude copy appears
        delay(400)
        animStage = 5 // Location auto-resolved ribbon slides in
        delay(350)
        animStage = 6 // Primary Enter & Exploration ready
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("welcome_gratitude_screen")
    ) {
        // 1. Soft Warm Earthen Gradient Background (Light, Unbleached Raw Silk Canvas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            TerracottaContainerLight.copy(alpha = 0.40f),
                            RawSilkSurface.copy(alpha = 0.25f),
                            Color.White
                        )
                    )
                )
        )

        // 2. Top-Right Instant Skip Button (No user ever trapped in animation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onEnterGaonova() }
                    .testTag("welcome_skip_button"),
                color = Color.White.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, CardStrokeBorder),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explore now",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Skip",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        // 3. Main Brand Experience Flow
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Organic Soil Root & Thread Drawing Animation (Canvas)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .testTag("brand_emblem_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Subtle warm glow circle
                        drawCircle(
                            color = TerracottaContainerLight.copy(alpha = earthGlowAlpha),
                            radius = w * 0.48f,
                            center = Offset(w / 2f, h / 2f)
                        )

                        // Organic curving root/thread growth path
                        val threadPath = Path().apply {
                            moveTo(w * 0.5f, h * 0.90f)
                            cubicTo(
                                w * (0.5f - 0.35f * threadProgress),
                                h * (0.80f - 0.20f * threadProgress),
                                w * (0.5f + 0.35f * threadProgress),
                                h * (0.40f - 0.15f * threadProgress),
                                w * 0.5f,
                                h * (0.90f - 0.75f * threadProgress)
                            )
                        }
                        drawPath(
                            path = threadPath,
                            color = MittiRoyalGold.copy(alpha = 0.85f * threadProgress),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Brand Soil & Sprout Emblem
                    if (animStage >= 1) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, TerracottaContainerLight),
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                MittiSoilEmblem(
                                    modifier = Modifier.size(32.dp),
                                    leafTint = TerracottaPrimary,
                                    soilTint = TerracottaDark,
                                    accentTint = MittiRoyalGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Brand Wordmark & Tagline
                AnimatedVisibility(
                    visible = animStage >= 2,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -15 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "GAONOVA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 5.sp
                            ),
                            color = TerracottaDark,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Made by Village Hands · Discovered with Care",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Cinematic Craftsmanship Visual Story (Rotating Daily Heritage Window)
                AnimatedVisibility(
                    visible = animStage >= 3,
                    enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { 25 }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("welcome_hero_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = RawSilkSurfaceElevated),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = featuredStory.imageUrl,
                                contentDescription = featuredStory.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(heroZoomScale),
                                contentScale = ContentScale.Crop
                            )

                            // Cinematic natural dark gradient overlay for scannability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.25f),
                                                Color.Black.copy(alpha = 0.78f)
                                            )
                                        )
                                    )
                            )

                            // Craft provenance badge & story description
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MittiRoyalGold
                                    ) {
                                        Text(
                                            text = featuredStory.region.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            letterSpacing = 0.5.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.White.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = featuredStory.craftType,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = featuredStory.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    fontSize = 15.sp
                                )

                                Text(
                                    text = featuredStory.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.90f),
                                    fontSize = 11.5.sp,
                                    maxLines = 2
                                )
                            }

                            // Story Rotation Indicator Dots
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rotatingCraftStories.indices.forEach { index ->
                                    val isSelected = index == currentStoryIndex
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 14.dp else 6.dp, 6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(if (isSelected) MittiRoyalGold else Color.White.copy(alpha = 0.5f))
                                            .clickable { currentStoryIndex = index }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sincere, Humble & Warm Gratitude Message (Short, scannable, human)
                AnimatedVisibility(
                    visible = animStage >= 4,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Welcome to Gaonova.",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = TextPrimary,
                            fontSize = 19.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "We're genuinely grateful you're here.",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = TerracottaPrimary,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Across India's villages, remarkable crafts and traditions are created by people whose work deserves to be discovered.\n\nThank you for spending time with us.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Section: Parallel Location Resolution & Seamless Enter Action
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Automatic Location Detection Ribbon / Natural Permission Invite
                AnimatedVisibility(
                    visible = animStage >= 5,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 20 }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("welcome_location_card"),
                        shape = RoundedCornerShape(16.dp),
                        color = TerracottaContainerLight.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, TerracottaPrimary.copy(alpha = 0.20f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            when (locationStatus) {
                                is LocationStatus.Resolving -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(15.dp),
                                            strokeWidth = 2.dp,
                                            color = TerracottaPrimary
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Finding your place in India...",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = TerracottaDark,
                                            fontSize = 12.5.sp
                                        )
                                    }
                                }

                                is LocationStatus.PermissionRationaleRequired -> {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.Explore,
                                                contentDescription = null,
                                                tint = TerracottaPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Want Gaonova to find your area?",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "Helps us show nearby products, local artisan clusters and accurate delivery timelines.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            textAlign = TextAlign.Center,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = onRequestLocationPermission,
                                                modifier = Modifier
                                                    .weight(1.2f)
                                                    .height(36.dp)
                                                    .testTag("welcome_allow_location_button"),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Allow location", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    // Proceed smoothly without permission
                                                    onEnterGaonova()
                                                },
                                                modifier = Modifier
                                                    .weight(0.9f)
                                                    .height(36.dp)
                                                    .testTag("welcome_not_now_button"),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Not now", fontSize = 11.5.sp, color = TextPrimary)
                                            }
                                        }
                                    }
                                }

                                is LocationStatus.PermissionDenied, is LocationStatus.LocationServicesDisabled, is LocationStatus.ResolutionFailed -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Exploring from: ${locationContext.formattedDisplay()}",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = TextPrimary,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Default Indian craft region active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                        TextButton(
                                            onClick = { showLocationSelector = true },
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp)
                                        ) {
                                            Text(
                                                text = "Change",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = TerracottaPrimary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    // Location Resolved Automatically
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = TerracottaPrimary.copy(alpha = 0.15f),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Filled.LocationOn,
                                                        contentDescription = null,
                                                        tint = TerracottaPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "✦ A little closer to home",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = TerracottaDark,
                                                    fontSize = 10.sp
                                                )
                                                Text(
                                                    text = locationContext.formattedDisplay(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = TextPrimary,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Change",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TerracottaPrimary
                                            ),
                                            fontSize = 11.5.sp,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .clickable { showLocationSelector = true }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Enter CTA Button (Smooth & Responsive)
                AnimatedVisibility(
                    visible = animStage >= 6,
                    enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { 20 }
                ) {
                    Button(
                        onClick = onEnterGaonova,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("enter_gaonova_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerracottaPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Enter Gaonova",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Location Selector
    if (showLocationSelector) {
        LocationSelectorDialog(
            currentLocation = locationContext,
            isResolvingLocation = locationStatus is LocationStatus.Resolving,
            onDismiss = { showLocationSelector = false },
            onUseCurrentLocation = {
                onRequestLocationPermission()
            },
            onSelectPinCode = { pin ->
                onManualPinSelected(pin)
            },
            onSelectCluster = { cluster ->
                onClusterSelected(cluster)
            }
        )
    }
}
