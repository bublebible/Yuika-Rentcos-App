package myproject.yuikarentcos.ui.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import myproject.yuikarentcos.ui.GlassWhite
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart
import myproject.yuikarentcos.ui.TextDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentManageScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- STATE FORM ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    // String untuk Tampilan UI
    var startDateString by remember { mutableStateOf("") }
    var endDateString by remember { mutableStateOf("") }

    // Long (Millis) untuk Logika Validasi & Database
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }

    // --- STATE IMAGE PICKER ---
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) bannerUri = uri
    }

    // --- STATE DATE PICKER ---
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Fungsi helper format tanggal
    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    // --- LOGIKA SIMPAN (PUBLISH) ---
    fun handlePublish() {
        // 1. Cek Kelengkapan
        if (title.isEmpty() || startDateMillis == null || endDateMillis == null || bannerUri == null) {
            Toast.makeText(context, "Mohon lengkapi judul, banner, dan durasi!", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Cek Logika Tanggal Sekali Lagi (Safety)
        if (endDateMillis!! < startDateMillis!!) {
            Toast.makeText(context, "Error: Tanggal Selesai mundur!", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Simulasi Simpan ke Database
        // Di Real App: Kamu akan menyimpan 'endDateMillis' ke Firestore.
        // Logic User App: query.whereGreaterThan("expiredAt", System.currentTimeMillis())
        // Maka banner otomatis hilang dari user jika waktu sekarang > endDateMillis.

        Toast.makeText(context, "Promo Published! Akan hilang otomatis pada $endDateString", Toast.LENGTH_LONG).show()

        // Reset Form (Optional)
        // title = ""; description = ""; ...
    }

    // Background Gradient
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd),
        start = Offset(0f, 0f), end = Offset(1000f, 1000f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        // CONTENT SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 100.dp)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create Promotion",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                TextButton(onClick = { /* TODO: Cancel Action */ }) {
                    Text("Cancel", color = Color.Gray)
                }
            }

            // 1. BANNER UPLOAD SECTION
            SectionHeader(icon = Icons.Default.Image, title = "Banner Image")
            Spacer(modifier = Modifier.height(12.dp))

            UploadBannerArea(
                imageUri = bannerUri,
                onUploadClick = { galleryLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. PROMOTION DETAILS SECTION
            SectionHeader(icon = Icons.Default.EditNote, title = "Promotion Details")
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    label = "Ad/Promo Title",
                    placeholder = "e.g., Summer Cosplay Sale",
                    value = title,
                    onValueChange = { title = it }
                )
                CustomTextField(
                    label = "Description",
                    placeholder = "Describe the promotion details...",
                    value = description,
                    onValueChange = { description = it },
                    isSingleLine = false,
                    height = 100.dp
                )
                CustomTextField(
                    label = "Target Link (Optional)",
                    placeholder = "https://yuikarentcos.com/sale",
                    value = link,
                    onValueChange = { link = it },
                    leadingIcon = Icons.Default.Link
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. DURATION SECTION (DATE PICKER)
            SectionHeader(icon = Icons.Default.CalendarMonth, title = "Duration")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassWhite)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    CustomTextField(
                        label = "Start Date",
                        placeholder = "DD/MM/YYYY",
                        value = startDateString,
                        onValueChange = {}, // Read only
                        leadingIcon = Icons.Default.DateRange,
                        readOnly = true,
                        onClick = { showStartDatePicker = true }
                    )
                }
                Box(Modifier.weight(1f)) {
                    CustomTextField(
                        label = "End Date",
                        placeholder = "DD/MM/YYYY",
                        value = endDateString,
                        onValueChange = {}, // Read only
                        leadingIcon = Icons.Default.DateRange,
                        readOnly = true,
                        onClick = { showEndDatePicker = true }
                    )
                }
            }
            // Pesan Error Kecil jika tanggal invalid (Optional Visual Feedback)
            if (startDateMillis != null && endDateMillis != null && endDateMillis!! < startDateMillis!!) {
                Text(
                    text = "* Tanggal selesai tidak boleh sebelum tanggal mulai",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. PREVIEW SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(icon = Icons.Default.Visibility, title = "Home Screen Preview")

                // Status Live / Expired
                val isExpired = endDateMillis != null && endDateMillis!! < System.currentTimeMillis()
                val statusText = if (isExpired) "EXPIRED" else "LIVE"
                val statusColor = if (isExpired) Color.Red else PurplePrimary

                Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text(statusText, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            PreviewCard(title = title, desc = description, imageUri = bannerUri)
        }

        // BOTTOM ACTION BAR (STICKY)
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 16.dp
        ) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Button(
                    onClick = { handlePublish() },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.RocketLaunch, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Ad/Promo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        // --- DIALOG DATE PICKER (START) ---
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { selected ->
                            // Set Start Date
                            startDateMillis = selected
                            startDateString = convertMillisToDate(selected)

                            // Reset End Date kalau End Date jadi tidak valid
                            if (endDateMillis != null && endDateMillis!! < selected) {
                                endDateMillis = null
                                endDateString = ""
                                Toast.makeText(context, "End Date direset karena kurang dari Start Date", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showStartDatePicker = false
                    }) { Text("OK", color = PurplePrimary) }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel", color = Color.Gray) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // --- DIALOG DATE PICKER (END) ---
        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { selected ->
                            // LOGIKA VALIDASI MINUS
                            if (startDateMillis != null && selected < startDateMillis!!) {
                                Toast.makeText(context, "Tanggal Selesai tidak boleh sebelum Tanggal Mulai!", Toast.LENGTH_LONG).show()
                            } else {
                                endDateMillis = selected
                                endDateString = convertMillisToDate(selected)
                                showEndDatePicker = false // Tutup dialog cuma kalo valid
                            }
                        }
                    }) { Text("OK", color = PurplePrimary) }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel", color = Color.Gray) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

// ================= KOMPONEN UI TAMBAHAN =================

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
}

@Composable
fun UploadBannerArea(imageUri: Uri?, onUploadClick: () -> Unit) {
    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(0.6f))
            .drawBehind {
                if (imageUri == null) {
                    drawRoundRect(color = Color.LightGray, style = stroke, cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()))
                }
            }
            .clickable { onUploadClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                color = Color.Black.copy(0.6f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
            ) {
                Icon(Icons.Default.Edit, "Edit", tint = Color.White, modifier = Modifier.padding(8.dp).size(16.dp))
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(PurplePrimary.copy(0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CloudUpload, null, tint = PurplePrimary, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Upload Banner", fontWeight = FontWeight.Bold, color = TextDark)
                Text("Recommended: 1200x600px", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onUploadClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = TextDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Select Image", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isSingleLine: Boolean = true,
    height: androidx.compose.ui.unit.Dp = 56.dp,
    leadingIcon: ImageVector? = null,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark.copy(0.8f))

        val modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = height)
            .let { if (onClick != null) it.clickable { onClick() } else it }

        Box(modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
                singleLine = isSingleLine,
                leadingIcon = if(leadingIcon != null) { { Icon(leadingIcon, null, tint = Color.Gray) } } else null,
                readOnly = readOnly,
                enabled = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF9FAFB),
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray.copy(0.5f),
                    focusedBorderColor = PurplePrimary,
                    disabledTextColor = TextDark,
                    disabledBorderColor = Color.LightGray,
                    disabledContainerColor = Color(0xFFF9FAFB)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
            if (readOnly) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable { onClick?.invoke() }
                )
            }
        }
    }
}

@Composable
fun PreviewCard(title: String, desc: String, imageUri: Uri? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray.copy(0.5f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEAB308)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF22C55E)))
            }
            Text("USER VIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        }

        Box(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Outlined.Image, null,
                        tint = Color.White.copy(0.2f),
                        modifier = Modifier.align(Alignment.Center).size(64.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(0.8f))))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Surface(color = PurplePrimary, shape = RoundedCornerShape(4.dp)) {
                        Text("PROMO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = title.ifEmpty { "Summer Cosplay Extravaganza" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = desc.ifEmpty { "Get 20% off all costumes..." },
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}