package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.GaonovaViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartCheckoutScreen(
    viewModel: GaonovaViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val walletState by viewModel.walletState.collectAsState()

    var isGiftOrder by remember { mutableStateOf(false) }
    var giftMessageInput by remember { mutableStateOf("Warm wishes and artisanal blessings from our family.") }
    var useWalletBalance by remember { mutableStateOf(false) }

    var recipientName by remember { mutableStateOf("Sayak Naskar") }
    var addressLine by remember { mutableStateOf("Flat 4B, Heritage Enclave, Salt Lake Sector V") }
    var cityDistrict by remember { mutableStateOf("Kolkata, West Bengal") }
    var pincode by remember { mutableStateOf("700091") }
    var phoneNumber by remember { mutableStateOf("+91 98300 12345") }

    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.product.price * it.quantity }
    }
    val ecoPackagingFee = if (isGiftOrder || cartItems.any { it.isGiftWrapped }) 99.0 else 0.0
    val deliveryFee = if (subtotal > 2000.0 || subtotal == 0.0) 0.0 else 120.0
    val totalAmount = subtotal + ecoPackagingFee + deliveryFee

    val walletDeduction = if (useWalletBalance) minOf(walletState.balance, totalAmount) else 0.0
    val finalPayable = maxOf(0.0, totalAmount - walletDeduction)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Artisan Direct Cart (${cartItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("cart_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (useWalletBalance && walletDeduction > 0) "Net Remaining via UPI" else "Total Payable",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (finalPayable == 0.0 && useWalletBalance) "₹0 (Paid from Wallet)" else "₹${finalPayable.toInt()}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.placeOrder(
                                        isGift = isGiftOrder,
                                        giftMessage = if (isGiftOrder) giftMessageInput else null,
                                        useWallet = useWalletBalance
                                    )
                                },
                                modifier = Modifier
                                    .testTag("place_order_button")
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = if (useWalletBalance && finalPayable == 0.0) Icons.Default.AccountBalanceWallet else Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (useWalletBalance && finalPayable == 0.0) "1-Tap Wallet Pay" else "Confirm & Pay",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Outlined.ShoppingBag,
                    title = "Your Craft Basket is Empty",
                    subtitle = "Explore authentic GI certified crafts directly from Indian village cooperatives.",
                    actionText = "Start Discovering Crafts",
                    onActionClick = { viewModel.navigateTo(ScreenDestination.Home) }
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cart items
                items(cartItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(item.product.primaryColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.product.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "From: ${item.product.villageOrCluster}, ${item.product.state}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Artisan: ${item.product.artisanName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SaffronGold,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.removeFromCart(item.product.id) },
                                    modifier = Modifier.testTag("remove_item_${item.product.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = PriceDropRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "₹${(item.product.price * item.quantity).toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Quantity Stepper
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = item.quantity.toString(),
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(
                                        onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Swadeshi Wallet Payment Option Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (useWalletBalance) SaffronGoldLight else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(TerracottaDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Swadeshi Patron Wallet",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Available Balance: ₹${"%,.2f".format(walletState.balance)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TerracottaDark
                                        )
                                    }
                                }

                                Switch(
                                    checked = useWalletBalance,
                                    onCheckedChange = { useWalletBalance = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = TerracottaPrimary,
                                        checkedTrackColor = SaffronGoldContainer
                                    )
                                )
                            }

                            if (useWalletBalance) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (walletState.balance >= totalAmount)
                                        "✓ Full order amount (₹${totalAmount.toInt()}) will be paid instantly from your wallet escrow."
                                    else
                                        "✓ ₹${walletState.balance.toInt()} deducted from wallet. Remaining ₹${finalPayable.toInt()} via UPI.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GiTagGreen
                                )
                            }
                        }
                    }
                }

                // Gifting Option
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
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
                                        text = "Wrap as Cultural Heritage Gift (+₹99)",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Switch(
                                    checked = isGiftOrder,
                                    onCheckedChange = { isGiftOrder = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SaffronGold,
                                        checkedTrackColor = SaffronGoldLight
                                    )
                                )
                            }

                            if (isGiftOrder) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = giftMessageInput,
                                    onValueChange = { giftMessageInput = it },
                                    label = { Text("Calligraphic Greeting Note", style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                // Shipping Address
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Delivery Destination in India",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = recipientName,
                                onValueChange = { recipientName = it },
                                label = { Text("Recipient Full Name", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = addressLine,
                                onValueChange = { addressLine = it },
                                label = { Text("Street Address, Flat, Society", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = cityDistrict,
                                    onValueChange = { cityDistrict = it },
                                    label = { Text("City, State", style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.weight(1.5f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = pincode,
                                    onValueChange = { pincode = it },
                                    label = { Text("PIN", style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number for Dispatch Updates", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }

                // Price Breakdown Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Transparent Price Summary",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Artisan Direct Value", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "₹${subtotal.toInt()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            if (ecoPackagingFee > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Botanical Eco-Packaging & Card", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "₹${ecoPackagingFee.toInt()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Climate-Safe Craft Logistics", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = if (deliveryFee == 0.0) "FREE (Cooperative Subsidy)" else "₹${deliveryFee.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (deliveryFee == 0.0) GiTagGreen else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (useWalletBalance && walletDeduction > 0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Swadeshi Wallet Escrow", style = MaterialTheme.typography.bodyMedium, color = GiTagGreen)
                                    Text(text = "-₹${walletDeduction.toInt()}", style = MaterialTheme.typography.bodyMedium, color = GiTagGreen)
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Final Payable Amount", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = "₹${finalPayable.toInt()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
