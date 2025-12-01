package com.techtool.giftsuggester.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.techtool.giftsuggester.GiftSuggesterViewModel
import com.techtool.giftsuggester.R
import com.techtool.giftsuggester.navigation.NavigationItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ScanScreen(navController: NavController, viewModel: GiftSuggesterViewModel) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedMode by remember { mutableStateOf("text") } // "text" or "photo"

    var occasion by remember { mutableStateOf("") }
    var recipientInfo by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    val context = LocalContext.current
    val kanitRegular = FontFamily(Font(R.font.kanitregular))
    val scrollState = rememberScrollState()

    // Camera URI state
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Create camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    bitmap = inputStream?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error loading photo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                bitmap = inputStream?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = createImageFile(context)
            photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(photoUri!!)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery permission launcher
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }

        if (hasPermission) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Find Perfect Gift",
                color = Color.White,
                fontSize = 28.sp,
                fontFamily = kanitRegular,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your preferred method",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontFamily = kanitRegular,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mode Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeButton(
                    icon = Icons.Default.TextFields,
                    label = "Text Input",
                    isSelected = selectedMode == "text",
                    onClick = {
                        selectedMode = "text"
                        bitmap = null
                    },
                    modifier = Modifier.weight(1f)
                )

                ModeButton(
                    icon = Icons.Default.PhotoCamera,
                    label = "Photo Analysis",
                    isSelected = selectedMode == "photo",
                    onClick = {
                        selectedMode = "photo"
                        occasion = ""
                        recipientInfo = ""
                        budget = ""
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content based on selected mode
            if (selectedMode == "text") {
                // Text Input Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Gift Details",
                            color = Color(0xFF8B0000),
                            fontSize = 20.sp,
                            fontFamily = kanitRegular,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Occasion Field
                        OutlinedTextField(
                            value = occasion,
                            onValueChange = { occasion = it },
                            label = {
                                Text(
                                    "Occasion",
                                    fontFamily = kanitRegular
                                )
                            },
                            placeholder = {
                                Text(
                                    "e.g., Birthday, Anniversary, Wedding",
                                    fontFamily = kanitRegular
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = "Occasion",
                                    tint = Color(0xFFDC143C)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Recipient Info Field
                        OutlinedTextField(
                            value = recipientInfo,
                            onValueChange = { recipientInfo = it },
                            label = {
                                Text(
                                    "Recipient Information",
                                    fontFamily = kanitRegular
                                )
                            },
                            placeholder = {
                                Text(
                                    "e.g., Age, gender, interests, hobbies",
                                    fontFamily = kanitRegular
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Recipient",
                                    tint = Color(0xFFDC143C)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Budget Field
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            label = {
                                Text(
                                    "Budget",
                                    fontFamily = kanitRegular
                                )
                            },
                            placeholder = {
                                Text(
                                    "e.g., $20-$50, Under $100",
                                    fontFamily = kanitRegular
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = "Budget",
                                    tint = Color(0xFFDC143C)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            } else {
                // Photo Mode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Selected Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = "Upload",
                                    tint = Color(0xFFDC143C).copy(alpha = 0.5f),
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No photo selected",
                                    color = Color.Black.copy(alpha = 0.5f),
                                    fontSize = 18.sp,
                                    fontFamily = kanitRegular
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Photo Upload Options
                PhotoUploadCard(
                    icon = Icons.Default.CameraAlt,
                    title = "Take Photo",
                    description = "Capture with camera",
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) -> {
                                val photoFile = createImageFile(context)
                                photoUri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    photoFile
                                )
                                cameraLauncher.launch(photoUri!!)
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PhotoUploadCard(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Choose from Gallery",
                    description = "Select existing photo",
                    onClick = {
                        when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                galleryPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                                )
                            }
                            else -> {
                                galleryPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Optional fields for photo mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Optional Details",
                            color = Color(0xFF8B0000),
                            fontSize = 16.sp,
                            fontFamily = kanitRegular,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = occasion,
                            onValueChange = { occasion = it },
                            label = { Text("Occasion (Optional)", fontFamily = kanitRegular) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = budget,
                            onValueChange = { budget = it },
                            label = { Text("Budget (Optional)", fontFamily = kanitRegular) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = recipientInfo,
                            onValueChange = { recipientInfo = it },
                            label = { Text("Additional Info (Optional)", fontFamily = kanitRegular) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFDC143C),
                                focusedLabelColor = Color(0xFFDC143C),
                                cursorColor = Color(0xFFDC143C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Get Suggestions Button
            Button(
                onClick = {
                    if (selectedMode == "text") {
                        if (occasion.isBlank() || recipientInfo.isBlank() || budget.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please fill in all fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.setOccasion(occasion)
                            viewModel.setRecipientInfo(recipientInfo)
                            viewModel.setBudget(budget)
                            viewModel.suggestGiftsFromText()
                            navController.navigate(NavigationItem.Result.route)
                        }
                    } else {
                        if (bitmap == null) {
                            Toast.makeText(
                                context,
                                "Please select or capture a photo first",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.setUploadedImage(bitmap)
                            viewModel.setOccasion(occasion)
                            viewModel.setRecipientInfo(recipientInfo)
                            viewModel.setBudget(budget)
                            viewModel.suggestGiftsFromImage(bitmap!!)
                            navController.navigate(NavigationItem.Result.route)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Get Suggestions",
                    tint = Color(0xFFDC143C),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Get Gift Suggestions",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontFamily = kanitRegular,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = kanitRegular,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ModeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val kanitRegular = FontFamily(Font(R.font.kanitregular))

    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFFDC143C) else Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = if (isSelected) Color(0xFFDC143C) else Color.White,
                fontSize = 14.sp,
                fontFamily = kanitRegular,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PhotoUploadCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val kanitRegular = FontFamily(Font(R.font.kanitregular))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFDC143C),
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF8B0000),
                    fontSize = 16.sp,
                    fontFamily = kanitRegular,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color.Black.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    fontFamily = kanitRegular
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = Color(0xFFDC143C),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.cacheDir
    return File.createTempFile(
        "GIFT_${timeStamp}_",
        ".jpg",
        storageDir
    )
}
