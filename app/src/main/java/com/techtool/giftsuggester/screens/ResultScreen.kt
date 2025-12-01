package com.techtool.giftsuggester.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techtool.giftsuggester.GiftSuggesterViewModel
import com.techtool.giftsuggester.GiftSuggestion
import com.techtool.giftsuggester.R
import com.techtool.giftsuggester.navigation.NavigationItem

@Composable
fun ResultScreen(navController: NavController, viewModel: GiftSuggesterViewModel) {
    val suggestions by viewModel.suggestions
    val isLoading by viewModel.isLoading
    val hasResults by viewModel.hasResults
    val errorMessage by viewModel.errorMessage
    val uploadedImage by viewModel.uploadedImage

    val kanitRegular = FontFamily(Font(R.font.kanitregular))
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDC143C),
                        Color(0xFF8B0000)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Gift",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isLoading) "Finding Perfect Gifts..." else "Gift Suggestions",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = kanitRegular,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Preview (if available)
            uploadedImage?.let { bitmap ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Analyzed Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Loading State
            if (isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFDC143C),
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Our AI is finding the perfect gifts for you...",
                            color = Color(0xFF8B0000),
                            fontSize = 18.sp,
                            fontFamily = kanitRegular,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This may take a few seconds",
                            color = Color.Black.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontFamily = kanitRegular,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            // Error State
            else if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color(0xFFDC143C),
                            modifier = Modifier.size(60.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFF8B0000),
                            fontSize = 16.sp,
                            fontFamily = kanitRegular,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            // Results State
            else if (hasResults && suggestions.isNotEmpty()) {
                // Results Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Found ${suggestions.size} Perfect Gifts!",
                                color = Color(0xFF8B0000),
                                fontSize = 20.sp,
                                fontFamily = kanitRegular,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Swipe through our personalized suggestions",
                                color = Color.Black.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontFamily = kanitRegular
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Gift Cards
                suggestions.forEachIndexed { index, suggestion ->
                    GiftCard(
                        giftNumber = index + 1,
                        suggestion = suggestion,
                        fontFamily = kanitRegular
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Try Again Button
                Button(
                    onClick = {
                        viewModel.resetSuggestions()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Try Again",
                        tint = Color(0xFFDC143C),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Try Again",
                        color = Color(0xFFDC143C),
                        fontSize = 16.sp,
                        fontFamily = kanitRegular,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Home Button
                OutlinedButton(
                    onClick = {
                        viewModel.resetSuggestions()
                        navController.navigate(NavigationItem.Info.route) {
                            popUpTo(NavigationItem.Info.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Home",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = kanitRegular,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GiftCard(
    giftNumber: Int,
    suggestion: GiftSuggestion,
    fontFamily: FontFamily
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Gift Number Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFDC143C),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Gift #$giftNumber",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Category Badge
                Surface(
                    color = Color(0xFFDC143C).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category",
                            tint = Color(0xFFDC143C),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = suggestion.category,
                            color = Color(0xFFDC143C),
                            fontSize = 12.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gift Name
            Text(
                text = suggestion.name,
                color = Color(0xFF8B0000),
                fontSize = 22.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Price",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = suggestion.price,
                    color = Color(0xFF4CAF50),
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(color = Color.Black.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = "Description",
                color = Color(0xFF8B0000),
                fontSize = 16.sp,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = suggestion.description,
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 15.sp,
                fontFamily = fontFamily,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Occasion
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "Occasion",
                    tint = Color(0xFFDC143C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Best for",
                        color = Color.Black.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = fontFamily
                    )
                    Text(
                        text = suggestion.occasion,
                        color = Color.Black.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
