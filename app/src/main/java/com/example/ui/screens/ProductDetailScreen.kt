package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Product
import com.example.data.repository.ProductRepository
import com.example.ui.components.CraftArtworkView
import com.example.ui.components.ExpandableCitationsCard
import com.example.ui.components.FivePillarsKnowledgeViewer
import com.example.ui.components.PriceHistoryVisualizer
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val product = remember(productId) {
        ProductRepository.getProductById(productId) ?: ProductRepository.sampleProducts.first()
    }
    val artisan = remember(product.artisanId) {
        ProductRepository.getArtisanById(product.artisanId)
    }
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val isWishlisted = wishlistIds.contains(product.id)

    var showPriceAlertDialog by remember { mutableStateOf(false) }
    var targetPriceInput by remember { mutableStateOf((product.price * 0.85).toInt().toString()) }

    var isGiftWrapped by remember { mutableStateOf(false) }
    var giftMessage by remember { mutableStateOf("") }

    // Micro-motion for wishlist button
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isWishlisted) {
        scaleAnim.animateTo(
            targetValue = 1.3f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        scaleAnim.animateTo(1f, animationSpec = tween(150))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleWishlist(product.id) },
                        modifier = Modifier.testTag("detail_wishlist_button")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) PriceDropRed else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.scale(if (isWishlisted) scaleAnim.value else 1f)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.AiSage) },
                        modifier = Modifier.testTag("detail_ask_ai_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ask AI Sage",
                            tint = SaffronGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${product.price.toInt()}",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (product.originalPrice > product.price) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "₹${product.originalPrice.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Text(
                            text = "Direct Artisan Guarantee",
                            style = MaterialTheme.typography.labelSmall,
                            color = GiTagGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = { showPriceAlertDialog = true },
                            modifier = Modifier.testTag("set_price_alert_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Alert",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.addToCart(
                                    product = product,
                                    quantity = 1,
                                    isGiftWrapped = isGiftWrapped,
                                    message = giftMessage
                                )
                            },
                            modifier = Modifier.testTag("add_to_cart_primary_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Cart", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("product_detail_scroll"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Hero Visual Box with Authentic Craft Artwork
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(product.primaryColorHex),
                                    Color(product.primaryColorHex).copy(alpha = 0.90f)
                                )
                            )
                        )
                ) {
                    // Authentic artisan illustration canvas
                    CraftArtworkView(
                        productId = product.id,
                        modifier = Modifier.fillMaxSize(),
                        isHero = true
                    )

                    // Scrim gradient at bottom so text is crystal clear
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.55f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = SaffronGoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.villageOrCluster}, ${product.district}, ${product.state}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }

                    // Authenticity Seal Badge with minimal green tick
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(GiTagGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verified Craft",
                                    tint = GiTagGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${product.authenticityScore}/100",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = GiTagGreen,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 2. Overview & GI Tag Registry Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GiTagGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (product.giCertified) (product.giRegistrationNo ?: "GI Certified") else "Handicrafts Board Authenticated",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (product.giCertified) GiTagGreen else SaffronGold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product.shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            product.certifications.forEach { cert ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(cert, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // 3. Five Pillars Knowledge Tabs (WHAT, WHERE, WHY, WHO, HOW)
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    FivePillarsKnowledgeViewer(
                        product = product,
                        artisan = artisan,
                        onArtisanClick = { aid -> viewModel.navigateTo(ScreenDestination.ArtisanDetail(aid)) }
                    )
                }
            }

            // 4. Price History Graph
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    PriceHistoryVisualizer(
                        priceHistory = product.priceHistory,
                        currentPrice = product.price
                    )
                }
            }

            // 5. Expandable Citations & Government Registry Links
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ExpandableCitationsCard(
                        citations = product.citations,
                        lastVerifiedDate = product.citations.firstOrNull()?.lastVerifiedDate ?: "2026"
                    )
                }
            }

            // 6. Gifting Option Addon
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = SaffronGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send as Artisan Gift Pack",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Switch(
                                checked = isGiftWrapped,
                                onCheckedChange = { isGiftWrapped = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SaffronGold,
                                    checkedTrackColor = SaffronGoldLight
                                )
                            )
                        }

                        if (isGiftWrapped) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = giftMessage,
                                onValueChange = { giftMessage = it },
                                placeholder = { Text("Personal greeting message for recipient...", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }

    // Set Price Alert Dialog
    if (showPriceAlertDialog) {
        AlertDialog(
            onDismissRequest = { showPriceAlertDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Price Drop Alert", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Current Artisan Price: ₹${product.price.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We will monitor verified cooperative releases and notify you immediately if the price meets or drops below your target.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = targetPriceInput,
                        onValueChange = { targetPriceInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Target Price (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = targetPriceInput.toDoubleOrNull() ?: product.price
                        viewModel.createPriceAlert(product, target)
                        showPriceAlertDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Create Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPriceAlertDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
