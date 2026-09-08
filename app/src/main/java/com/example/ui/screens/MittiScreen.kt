package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatMessage
import com.example.data.models.Citation
import com.example.data.models.MessageSender
import com.example.data.models.Product
import com.example.data.repository.ProductRepository
import com.example.ui.components.CraftArtworkView
import com.example.ui.components.MittiSoilEmblem
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

// =========================================================================
// PURE VILLAGE GREEN & WARM EARTH PALETTE (LIGHT & MINIMAL)
// =========================================================================
private val VillageGreenDark = Color(0xFF1B4332)
private val VillageGreenPrimary = Color(0xFF2D6A4F)
private val VillageGreenLeaf = Color(0xFF40916C)
private val VillageGreenSoft = Color(0xFFEAF4EE)
private val VillageGreenTint = Color(0xFFD8EEDF)
private val MittiCanvasBg = Color(0xFFF9F7F2)
private val MittiUserBubbleBg = Color(0xFFE2EFE7)
private val MittiAgentBubbleBg = Color(0xFFFFFFFF)
private val MittiTextPrimary = Color(0xFF1A261E)
private val MittiTextSecondary = Color(0xFF5A6E62)
private val TerracottaAccent = Color(0xFFBD5338)

/**
 * MittiScreen — Minimal, aesthetic, pure village-green communication guide for Gaonova.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MittiScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val queryInput by viewModel.aiQueryText.collectAsState()

    val listState = rememberLazyListState()
    var showScannerDialog by remember { mutableStateOf(false) }
    var isVoiceListening by remember { mutableStateOf(false) }

    // Auto-scroll on new messages
    LaunchedEffect(chatHistory.size, isThinking) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MittiCanvasBg)
            .testTag("mitti_screen")
    ) {
        // =========================================================================
        // 1. MINIMAL VILLAGE GREEN TOP BAR
        // =========================================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = VillageGreenDark,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header Identity
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VillageGreenSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            MittiSoilEmblem(
                                modifier = Modifier.size(24.dp),
                                leafTint = VillageGreenDark,
                                soilTint = TerracottaAccent,
                                accentTint = Color(0xFFD4A373)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mitti",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = VillageGreenLeaf.copy(alpha = 0.45f)
                                ) {
                                    Text(
                                        text = "VILLAGE GUIDE",
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 8.5.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Text(
                                text = if (isThinking) "Consulting craft archives..." else "Handicraft & GI Provenance Guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    // Reset / Clear action
                    if (chatHistory.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.stopMittiQuery()
                                viewModel.aiChatHistory.value = emptyList()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("mitti_reset_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.RestartAlt,
                                contentDescription = "Clear Chat",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 2. CONVERSATION STREAM / MINIMAL THREAD
        // =========================================================================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatHistory.isEmpty()) {
                MittiMinimalWelcome(
                    onActionSelect = { query -> viewModel.askMitti(query) },
                    onOpenScanner = { showScannerDialog = true }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 14.dp)
                ) {
                    items(chatHistory) { message ->
                        MittiMessageBubble(
                            message = message,
                            onProductClick = { pid -> viewModel.navigateTo(ScreenDestination.ProductDetail(pid)) },
                            onAddToCart = { pid ->
                                val prod = ProductRepository.getProductById(pid)
                                if (prod != null) viewModel.addToCart(prod, 1)
                            },
                            onActionClick = { action ->
                                when {
                                    action.contains("Add to Cart", ignoreCase = true) -> {
                                        val pid = message.relatedProductIds.firstOrNull()
                                        if (pid != null) {
                                            val prod = ProductRepository.getProductById(pid)
                                            if (prod != null) viewModel.addToCart(prod, 1)
                                        }
                                    }
                                    action.contains("Orders", ignoreCase = true) || action.contains("Track", ignoreCase = true) -> {
                                        viewModel.navigateTo(ScreenDestination.OrdersAlerts)
                                    }
                                    action.contains("View", ignoreCase = true) || action.contains("Shop", ignoreCase = true) -> {
                                        val pid = message.relatedProductIds.firstOrNull()
                                        if (pid != null) viewModel.navigateTo(ScreenDestination.ProductDetail(pid))
                                        else viewModel.askMitti(action)
                                    }
                                    else -> {
                                        viewModel.askMitti(action)
                                    }
                                }
                            }
                        )
                    }

                    if (isThinking) {
                        item {
                            MittiThinkingBubble(onStop = { viewModel.stopMittiQuery() })
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 3. MINIMAL VILLAGE GREEN INPUT BAR WITH STOP BUTTON & SINGLE CAMERA
        // =========================================================================
        MittiMinimalInputBar(
            queryInput = queryInput,
            isThinking = isThinking,
            isVoiceListening = isVoiceListening,
            onQueryChange = { viewModel.aiQueryText.value = it },
            onSend = {
                val q = queryInput.trim()
                if (q.isNotBlank()) {
                    isVoiceListening = false
                    viewModel.askMitti(q)
                }
            },
            onStop = { viewModel.stopMittiQuery() },
            onVoiceToggle = {
                isVoiceListening = !isVoiceListening
                if (isVoiceListening) {
                    viewModel.aiQueryText.value = "Where did Nakshi Kantha originate?"
                }
            },
            onOpenScanner = { showScannerDialog = true }
        )
    }

    if (showScannerDialog) {
        MittiCraftScannerDialog(
            onDismiss = { showScannerDialog = false },
            onSelectSample = { sampleQuery ->
                showScannerDialog = false
                viewModel.askMitti("Identify this craft: $sampleQuery")
            }
        )
    }
}

// =========================================================================
// MINIMAL WELCOME VIEW (AESTHETIC VILLAGE CRAFT THEME)
// =========================================================================

@Composable
private fun MittiMinimalWelcome(
    onActionSelect: (String) -> Unit,
    onOpenScanner: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MittiAgentBubbleBg,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(VillageGreenTint)
                ),
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VillageGreenSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            MittiSoilEmblem(
                                modifier = Modifier.size(20.dp),
                                leafTint = VillageGreenPrimary,
                                soilTint = TerracottaAccent,
                                accentTint = Color(0xFFD4A373)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Namaste, I am Mitti",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = VillageGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "I am your guide to authentic Indian village handicrafts, Geographical Indication (GI) heritage, rural artisan stories, and thoughtful gifting.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MittiTextPrimary,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Suggestions Label
        item {
            Text(
                text = "SUGGESTED CRAFT INQUIRIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = VillageGreenPrimary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        val promptSuggestions = listOf(
            "Find handmade gifts under ₹2,000" to Icons.Outlined.CardGiftcard,
            "Show authentic crafts from West Bengal and Rajasthan" to Icons.Outlined.Public,
            "What makes GI-tagged handicrafts authentic?" to Icons.Outlined.Verified,
            "How is Kannauj Mitti Attar distilled traditionally?" to Icons.Outlined.Eco
        )

        items(promptSuggestions) { (prompt, icon) ->
            Surface(
                onClick = { onActionSelect(prompt) },
                shape = RoundedCornerShape(12.dp),
                color = MittiAgentBubbleBg,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2EBE5))
                ),
                shadowElevation = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = VillageGreenLeaf,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MittiTextPrimary,
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = VillageGreenPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// MINIMAL MESSAGE BUBBLE COMPONENT
// =========================================================================

@Composable
private fun MittiMessageBubble(
    message: ChatMessage,
    onProductClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onActionClick: (String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val isHumanOfficer = message.sender == MessageSender.HUMAN_OFFICER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 330.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 14.dp
                ),
                color = if (isUser) MittiUserBubbleBg else MittiAgentBubbleBg,
                border = if (isUser) null else CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2EBE5))
                ),
                shadowElevation = 0.5.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (!isUser) {
                        Text(
                            text = if (isHumanOfficer) "Craft Specialist" else "Mitti",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = VillageGreenPrimary,
                            fontSize = 11.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp,
                        fontSize = 13.5.sp,
                        color = MittiTextPrimary
                    )

                    // Provenance / Verification disclosure badge
                    if (message.isConfidenceDisclosed && message.confidenceNote != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = VillageGreenSoft
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = VillageGreenPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = message.confidenceNote,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VillageGreenDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall,
                        color = MittiTextSecondary,
                        fontSize = 9.5.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            // Products Catalog Row
            if (message.relatedProductIds.isNotEmpty()) {
                val matched = message.relatedProductIds.mapNotNull { ProductRepository.getProductById(it) }
                if (matched.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RECOMMENDED HANDICRAFTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = VillageGreenPrimary,
                        modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(matched) { product ->
                            MittiMinimalProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product.id) }
                            )
                        }
                    }
                }
            }

            // Citations
            if (message.citations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                MittiMinimalCitations(citations = message.citations)
            }

            // Quick Follow-Up Actions
            if (message.suggestedActions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(message.suggestedActions) { action ->
                        Surface(
                            onClick = { onActionClick(action) },
                            shape = RoundedCornerShape(16.dp),
                            color = MittiAgentBubbleBg,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(VillageGreenLeaf.copy(alpha = 0.4f))
                            ),
                            shadowElevation = 0.5.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VillageGreenPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = VillageGreenPrimary,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// MINIMAL PRODUCT CARD
// =========================================================================

@Composable
private fun MittiMinimalProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MittiAgentBubbleBg,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2EBE5))
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.width(180.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(product.primaryColorHex).copy(alpha = 0.85f),
                                Color(product.primaryColorHex).copy(alpha = 0.55f)
                            )
                        )
                    )
                    .padding(4.dp)
            ) {
                CraftArtworkView(
                    productId = product.id,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = if (product.giCertified) VillageGreenSoft else Color(0xFFFFF3CD),
                    modifier = Modifier.align(Alignment.TopStart).padding(3.dp)
                ) {
                    Text(
                        text = if (product.giCertified) "GI VERIFIED" else "HANDMADE",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 7.5.sp,
                        color = if (product.giCertified) VillageGreenDark else Color(0xFF856404),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MittiTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
                Text(
                    text = "${product.district}, ${product.state}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MittiTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${product.price.toInt()}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = VillageGreenPrimary,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VillageGreenPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(
                            text = "Add",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// MINIMAL CITATIONS COLLAPSE
// =========================================================================

@Composable
private fun MittiMinimalCitations(citations: List<Citation>) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(8.dp),
        color = MittiAgentBubbleBg,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2EBE5))
        ),
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = VillageGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "National Craft Archives (${citations.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MittiTextPrimary,
                        fontSize = 11.sp
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MittiTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                citations.forEach { cit ->
                    Column(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "• ${cit.title}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = MittiTextPrimary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${cit.publisher} · ${cit.urlOrReference}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MittiTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// THINKING BUBBLE WITH STOP QUERY ACTION
// =========================================================================

@Composable
private fun MittiThinkingBubble(onStop: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = 4.dp,
                bottomEnd = 14.dp
            ),
            color = MittiAgentBubbleBg,
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2EBE5))
            ),
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsing Green Dot Animation
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VillageGreenPrimary.copy(alpha = alpha))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mitti is responding...",
                    style = MaterialTheme.typography.labelSmall,
                    color = VillageGreenPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(10.dp))
                // Stop text button
                Surface(
                    onClick = onStop,
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFDE8E8)
                ) {
                    Text(
                        text = "Stop",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFC53030),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// MINIMAL INPUT BAR WITH STOP BUTTON & SINGLE CAMERA IN THE TYPE BAR
// =========================================================================

@Composable
private fun MittiMinimalInputBar(
    queryInput: String,
    isThinking: Boolean,
    isVoiceListening: Boolean,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onVoiceToggle: () -> Unit,
    onOpenScanner: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (isVoiceListening) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = VillageGreenSoft,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(VillageGreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Listening to craft query...",
                        style = MaterialTheme.typography.labelSmall,
                        color = VillageGreenDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input TextField Capsule with integrated Camera Icon
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                color = MittiAgentBubbleBg,
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD4E2D8))
                ),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = queryInput,
                        onValueChange = onQueryChange,
                        placeholder = {
                            Text(
                                text = "Ask about Indian handicrafts...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MittiTextSecondary,
                                fontSize = 13.5.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mitti_input_field"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = VillageGreenPrimary
                        ),
                        singleLine = true
                    )

                    // Single Camera Icon inside the type bar
                    IconButton(
                        onClick = onOpenScanner,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Scan Craft Item",
                            tint = VillageGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Voice Query Mic
                    IconButton(
                        onClick = onVoiceToggle,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isVoiceListening) Icons.Default.MicOff else Icons.Outlined.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isVoiceListening) TerracottaAccent else VillageGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button: Alternates between Send and Stop
            FloatingActionButton(
                onClick = {
                    if (isThinking) {
                        onStop()
                    } else {
                        onSend()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag(if (isThinking) "mitti_stop_button" else "mitti_send_button"),
                shape = CircleShape,
                containerColor = if (isThinking) Color(0xFFC53030) else VillageGreenDark,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
            ) {
                if (isThinking) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Response",
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send Query",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// CRAFT SCANNER / PROVENANCE INSPECTOR MODAL
// =========================================================================

@Composable
fun MittiCraftScannerDialog(
    onDismiss: () -> Unit,
    onSelectSample: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = VillageGreenDark,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Craft Provenance Scanner",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = VillageGreenDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Select a craft image sample or inspect traditional techniques directly:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MittiTextSecondary,
                    fontSize = 12.sp
                )

                val samples = listOf(
                    "Blue Pottery Jaipur vase (Quartz & Glass glaze)",
                    "Nakshi Kantha embroidered tussar silk dupatta",
                    "Kannauj Mitti Attar earthen hydro-distillate",
                    "Channapatna ivory-wood vegetable lacquer toy"
                )

                samples.forEach { sample ->
                    Surface(
                        onClick = { onSelectSample(sample) },
                        shape = RoundedCornerShape(8.dp),
                        color = VillageGreenSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sample,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = VillageGreenDark,
                                fontSize = 11.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = VillageGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", color = VillageGreenDark, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MittiAgentBubbleBg,
        shape = RoundedCornerShape(16.dp)
    )
}
