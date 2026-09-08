package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// =========================================================================
// 1. SPEECH / VOICE SEARCH MODAL WITH LIVE WAVEFORM & PROMPTS
// =========================================================================

@Composable
fun SpeechSearchDialog(
    onDismiss: () -> Unit,
    onSpeechResult: (String) -> Unit
) {
    var isListening by remember { mutableStateOf(true) }
    var recognizedText by remember { mutableStateOf("") }
    val promptSuggestions = listOf(
        "Show authentic Pashmina Shawls",
        "Jaipur Blue Pottery vases",
        "Nakshi Kantha sarees from Bengal",
        "Channapatna wooden toys under ₹1000",
        "Pure Kannauj Mitti Attar",
        "GI Certified crafts from Odisha"
    )

    // Animated waveform effect
    val infiniteTransition = rememberInfiniteTransition()
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val waveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .testTag("speech_search_modal"),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top drag indicator
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isListening) "Listening for craft or artisan..." else "Processing Voice...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Speak in English, Hindi, Bengali, or your regional language",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Pulse Voice Mic Visualizer
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(130.dp)
                    ) {
                        // Outer animated ripple rings
                        Box(
                            modifier = Modifier
                                .size(120.dp * waveScale)
                                .clip(CircleShape)
                                .background(TerracottaPrimary.copy(alpha = waveAlpha))
                        )
                        Box(
                            modifier = Modifier
                                .size(95.dp * waveScale)
                                .clip(CircleShape)
                                .background(GoldenAmber.copy(alpha = waveAlpha * 1.5f))
                        )

                        // Center Mic Button
                        Surface(
                            onClick = {
                                isListening = !isListening
                            },
                            shape = CircleShape,
                            color = TerracottaPrimary,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .size(72.dp)
                                .testTag("voice_mic_active_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Microphone Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Simulated live waveform bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(30.dp)
                    ) {
                        val barHeights = listOf(12.dp, 22.dp, 28.dp, 16.dp, 26.dp, 14.dp, 20.dp)
                        barHeights.forEachIndexed { index, height ->
                            val animHeight by infiniteTransition.animateValue(
                                initialValue = 6.dp,
                                targetValue = height,
                                typeConverter = androidx.compose.ui.unit.Dp.VectorConverter,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(300 + (index * 80), easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(animHeight)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(TerracottaPrimary.copy(alpha = 0.8f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Or tap a quick craft query:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Suggestion Chips
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        promptSuggestions.chunked(2).forEach { rowPrompts ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPrompts.forEach { prompt ->
                                    Surface(
                                        onClick = {
                                            onSpeechResult(prompt)
                                            onDismiss()
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = CardDefaults.outlinedCardBorder(),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = prompt,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Voice Search", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// =========================================================================
// 2. GOOGLE LENS & VISUAL CRAFT SCANNER MODAL
// =========================================================================

data class LensSampleCraft(
    val title: String,
    val cluster: String,
    val query: String,
    val colorHex: Long
)

val sampleLensCrafts = listOf(
    LensSampleCraft("Pashmina Weave Pattern", "Srinagar, Kashmir", "Pashmina", 0xFF6E2E1A),
    LensSampleCraft("Blue Pottery Floral Glaze", "Kot Jewar, Rajasthan", "Blue Pottery", 0xFF3A6167),
    LensSampleCraft("Channapatna Lacquer Wood", "Channapatna, Karnataka", "Channapatna Toy", 0xFFA67B4C),
    LensSampleCraft("Nakshi Kantha Embroidery", "Bolpur, West Bengal", "Nakshi Kantha", 0xFF94442A),
    LensSampleCraft("Pattachitra Palm Leaf Art", "Raghurajpur, Odisha", "Pattachitra", 0xFF3B6E6A)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleLensScannerDialog(
    onDismiss: () -> Unit,
    onCraftIdentified: (String) -> Unit
) {
    var isScanning by remember { mutableStateOf(false) }
    var selectedSample by remember { mutableStateOf<LensSampleCraft?>(null) }

    val infiniteTransition = rememberInfiniteTransition()
    val scanLineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CenterFocusStrong,
                                contentDescription = null,
                                tint = SaffronGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google Lens • Craft Scanner",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF161412))
                )
            },
            containerColor = Color(0xFF161412)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Point camera at any Indian craft, textile, pottery, or heirloom to identify origin and GI authenticity",
                    style = MaterialTheme.typography.bodySmall,
                    color = RawSilkCream.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Viewfinder View
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("lens_scanner_viewfinder"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF221F1C))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background pattern texture
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeColor = Color(0xFF453F38).copy(alpha = 0.4f)
                            val step = 40.dp.toPx()
                            for (x in 0..(size.width / step).toInt()) {
                                drawLine(
                                    color = strokeColor,
                                    start = Offset(x * step, 0f),
                                    end = Offset(x * step, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(size.height / step).toInt()) {
                                drawLine(
                                    color = strokeColor,
                                    start = Offset(0f, y * step),
                                    end = Offset(size.width, y * step),
                                    strokeWidth = 1f
                                )
                            }
                        }

                        // Scanning reticle bounding box
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(240.dp)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(listOf(SaffronGold, TerracottaLight)),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            // Animated scan laser beam
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val y = size.height * scanLineProgress
                                drawLine(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            GoldenAmber,
                                            Color.White,
                                            GoldenAmber,
                                            Color.Transparent
                                        )
                                    ),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ANALYZING WEAVE & MOTIFS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SaffronGoldLight,
                                    letterSpacing = 1.2.sp
                                )

                                Icon(
                                    imageVector = Icons.Default.FilterCenterFocus,
                                    contentDescription = null,
                                    tint = RawSilkCream.copy(alpha = 0.35f),
                                    modifier = Modifier.size(64.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "AI Visual Authenticator Active",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GiTagGreen
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preset Sample Craft Images to scan
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Or choose a test craft pattern:",
                        style = MaterialTheme.typography.labelSmall,
                        color = RawSilkCream.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(sampleLensCrafts) { craft ->
                            Card(
                                onClick = {
                                    selectedSample = craft
                                    onCraftIdentified(craft.query)
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF282522)),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(craft.colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = craft.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                        Text(
                                            text = craft.cluster,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = RawSilkCream.copy(alpha = 0.6f),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action buttons: Gallery Upload & Snap
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Instant test scan
                            onCraftIdentified("Pashmina")
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RawSilkCream),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(RawSilkCream.copy(alpha = 0.4f), RawSilkCream.copy(alpha = 0.1f)))
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick Photo")
                    }

                    Button(
                        onClick = {
                            onCraftIdentified("Blue Pottery")
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("lens_capture_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Capture & Scan", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
