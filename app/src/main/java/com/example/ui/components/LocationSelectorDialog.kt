package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.location.*
import com.example.data.models.ShippingAddress
import com.example.data.models.UserProfile
import com.example.ui.theme.*

@Composable
fun LocationSelectorDialog(
    currentLocation: LocationContext,
    isResolvingLocation: Boolean,
    onDismiss: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSelectPinCode: (String) -> Unit,
    onSelectCluster: ((PostalClusterInfo) -> Unit)? = null,
    userProfile: UserProfile? = null,
    onSelectAddress: ((ShippingAddress) -> Unit)? = null,
    onManageAddresses: (() -> Unit)? = null
) {
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sampleAddresses = remember(userProfile) {
        userProfile?.addresses?.ifEmpty { null } ?: listOf(
            ShippingAddress(
                id = "default_addr_1",
                tag = "Home",
                fullName = userProfile?.name ?: "Sayak Naskar",
                street = "Flat 4B, Heritage Enclave, Salt Lake Sector V",
                city = "Kolkata",
                state = "West Bengal",
                postalCode = "700091",
                phone = "+91 98300 12345",
                isDefault = true
            ),
            ShippingAddress(
                id = "work_addr_2",
                tag = "Work",
                fullName = userProfile?.name ?: "Sayak Naskar",
                street = "Godrej Waterside, Tower 2, Sector V",
                city = "Kolkata",
                state = "West Bengal",
                postalCode = "700091",
                phone = "+91 98300 12345",
                isDefault = false
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable(enabled = false) {}
                    .testTag("amazon_location_dialog"),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Amazon Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 12.dp, top = 16.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose your location",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF0F1111)
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF0F1111),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "Select a delivery location to see product availability and delivery options",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF565959),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Saved Addresses horizontal card slider
                    if (sampleAddresses.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            sampleAddresses.forEach { address ->
                                val isSelected = currentLocation.postalCode == address.postalCode

                                Surface(
                                    modifier = Modifier
                                        .width(190.dp)
                                        .clickable {
                                            if (onSelectAddress != null) {
                                                onSelectAddress(address)
                                            }
                                            onSelectPinCode(address.postalCode)
                                            onDismiss()
                                        }
                                        .testTag("saved_address_${address.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFFF7FAFA) else Color.White,
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (isSelected) Color(0xFF007185) else Color(0xFFD5D9D9)
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = address.fullName,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF0F1111),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (address.isDefault) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFFE8F6F6)
                                                ) {
                                                    Text(
                                                        text = "Default",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = Color(0xFF007185),
                                                        fontSize = 9.sp,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "${address.street}, ${address.city} ${address.postalCode}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF565959),
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = {
                                                    if (onSelectAddress != null) {
                                                        onSelectAddress(address)
                                                    }
                                                    onSelectPinCode(address.postalCode)
                                                    onDismiss()
                                                },
                                                modifier = Modifier.size(16.dp),
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = Color(0xFF007185)
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isSelected) "Delivering here" else "Select address",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                ),
                                                color = if (isSelected) Color(0xFF007185) else Color(0xFF565959),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    HorizontalDivider(color = Color(0xFFE7E7E7), thickness = 1.dp)

                    // Amazon Option 1: Enter an Indian pincode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text = "Enter an Indian pincode",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F1111),
                            fontSize = 13.5.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { input ->
                                    if (input.length <= 6 && input.all { it.isDigit() }) {
                                        pinInput = input
                                        pinError = null
                                    }
                                },
                                placeholder = {
                                    Text("Enter a 6-digit PIN code", fontSize = 13.sp, color = Color(0xFF888888))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("amazon_pincode_input"),
                                shape = RoundedCornerShape(8.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        val trimmed = pinInput.trim()
                                        if (trimmed.length == 6 && IndianPostalDirectory.isValidPinCode(trimmed)) {
                                            onSelectPinCode(trimmed)
                                            onDismiss()
                                        } else {
                                            pinError = "Please enter a valid 6-digit Indian PIN code"
                                        }
                                    }
                                ),
                                singleLine = true,
                                isError = pinError != null
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    val trimmed = pinInput.trim()
                                    if (trimmed.length == 6 && IndianPostalDirectory.isValidPinCode(trimmed)) {
                                        onSelectPinCode(trimmed)
                                        onDismiss()
                                    } else {
                                        pinError = "Please enter a valid 6-digit Indian PIN code"
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD814),
                                    contentColor = Color(0xFF0F1111)
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("amazon_apply_pin_btn")
                            ) {
                                Text(
                                    text = "Apply",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        if (pinError != null) {
                            Text(
                                text = pinError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7E7E7), thickness = 1.dp)

                    // Amazon Option 2: Use current location
                    Surface(
                        onClick = {
                            onUseCurrentLocation()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amazon_use_location_btn"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MyLocation,
                                contentDescription = null,
                                tint = Color(0xFF007185),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Use my current location",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF0F1111),
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "Using GPS to determine your location",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF565959),
                                    fontSize = 11.5.sp
                                )
                            }
                            if (isResolvingLocation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF007185)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7E7E7), thickness = 1.dp)

                    // Amazon Option 3: Add an address or pick-up point
                    Surface(
                        onClick = {
                            if (onManageAddresses != null) {
                                onManageAddresses()
                            }
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("amazon_add_address_btn"),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddLocationAlt,
                                contentDescription = null,
                                tint = Color(0xFF007185),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = "Add an address or pick-up point",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF0F1111),
                                fontSize = 13.5.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF888888),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE7E7E7), thickness = 1.dp)

                    // Quick City Delivery Targets (Amazon India Metro Quick Select)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Quick Delivery Cities:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF565959),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Kolkata (700001)" to "700001",
                                "Mumbai (400001)" to "400001",
                                "Delhi (110001)" to "110001",
                                "Bengaluru (560001)" to "560001",
                                "Hyderabad (500001)" to "500001",
                                "Chennai (600001)" to "600001",
                                "Jaipur (302001)" to "302001",
                                "Pune (411001)" to "411001"
                            ).forEach { (label, pin) ->
                                Surface(
                                    onClick = {
                                        onSelectPinCode(pin)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (currentLocation.postalCode == pin) Color(0xFFE8F6F6) else Color(0xFFF2F4F4),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            if (currentLocation.postalCode == pin) Color(0xFF007185) else Color(0xFFE0E0E0)
                                        )
                                    )
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (currentLocation.postalCode == pin) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        fontSize = 11.sp,
                                        color = if (currentLocation.postalCode == pin) Color(0xFF007185) else Color(0xFF0F1111)
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
