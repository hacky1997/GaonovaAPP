package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ProductRepository
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun GiftingScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val giftGuides = ProductRepository.sampleGiftGuides
    var selectedGuideId by remember { mutableStateOf(giftGuides.first().id) }

    val activeGuide = remember(selectedGuideId) {
        giftGuides.find { it.id == selectedGuideId } ?: giftGuides.first()
    }

    val recommendedProducts = remember(selectedGuideId) {
        activeGuide.productIds.mapNotNull { ProductRepository.getProductById(it) }
    }

    val wishlistIds by viewModel.wishlistIds.collectAsState()

    var customMessage by remember {
        mutableStateOf("May this handcrafted heritage bring warmth, good fortune, and timeless elegance to your home.")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("gifting_screen"),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SaffronGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Cultural Gifting Concierge",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Gifts with deep provenance, artisan blessing cards, and sustainable botanical wrapping.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Occasion selector carousel
        item {
            LazyRow(
                modifier = Modifier.padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(giftGuides) { guide ->
                    val isSelected = selectedGuideId == guide.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedGuideId = guide.id }
                            .testTag("gift_guide_${guide.id}"),
                        color = if (isSelected) SaffronGold else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        border = if (!isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = guide.occasionTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "For: ${guide.recipientType}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SaffronGoldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Gifting Guide Deep Dive
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeGuide.occasionTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GiTagGreenBg
                        ) {
                            Text(
                                text = activeGuide.budgetRange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = GiTagGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = activeGuide.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolunteerActivism,
                                contentDescription = null,
                                tint = SaffronGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cultural Etiquette: ${activeGuide.culturalEtiquette}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Complimentary Gift Packaging & Card Customizer Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndigoMidnight)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = GoldenAmber
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Complimentary Handwritten Artisan Gift Card",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customMessage,
                        onValueChange = { customMessage = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldenAmber,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "• Packaged in handmade tree-free banana bark kraft paper with pressed marigold petals.",
                        style = MaterialTheme.typography.labelSmall,
                        color = SaffronGoldLight,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Curated Treasures for ${activeGuide.recipientType}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Recommended items grid
        val pairs = recommendedProducts.chunked(2)
        items(pairs) { pair ->
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
                            onQuickAdd = {
                                viewModel.addToCart(prod, 1, isGiftWrapped = true, message = customMessage)
                            }
                        )
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
