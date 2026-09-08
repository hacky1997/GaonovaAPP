package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun ExploreScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val regions = ProductRepository.sampleRegions
    var selectedRegionState by remember { mutableStateOf(regions.first().state) }

    val activeRegion = remember(selectedRegionState) {
        regions.find { it.state == selectedRegionState } ?: regions.first()
    }

    val regionProducts = remember(selectedRegionState) {
        ProductRepository.sampleProducts.filter { it.state.equals(selectedRegionState, ignoreCase = true) }
    }

    val wishlistIds by viewModel.wishlistIds.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Geographic Provenance Explorer",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Discover India's villages, craft clusters, and ancestral techniques state by state.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // State selector carousel
        item {
            LazyRow(
                modifier = Modifier.padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(regions) { region ->
                    val isSelected = selectedRegionState == region.state
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedRegionState = region.state }
                            .testTag("region_select_${region.state}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        border = if (!isSelected) CardDefaults.outlinedCardBorder() else null,
                        shadowElevation = if (isSelected) 3.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = region.state,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${region.artisanCommunitiesCount}+ Artisan Guilds",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) SaffronGoldLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Region Cultural Knowledge Card
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
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeRegion.state,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Districts: ${activeRegion.district} • Cluster: ${activeRegion.villageOrCluster}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = SaffronGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeRegion.artisanCommunitiesCount}+ Guilds",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = activeRegion.culturalHeritageSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary crafts badges
                    Text(
                        text = "Famous Geographical Crafts:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeRegion.famousCrafts.forEach { craft ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(craft, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Verified Crafts from this Region
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Crafts from ${activeRegion.state} (${regionProducts.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "100% Origin Guaranteed",
                    style = MaterialTheme.typography.labelSmall,
                    color = GiTagGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Product list for region
        if (regionProducts.isEmpty()) {
            item {
                Text(
                    text = "More crafts from ${activeRegion.state} currently being authenticated by cooperative council.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            val chunked = regionProducts.chunked(2)
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
    }
}
