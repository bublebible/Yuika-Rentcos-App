package myproject.yuikarentcos.ui.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // <--- JANGAN LUPA LIBRARY COIL
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgStart
import myproject.yuikarentcos.ui.StatusLaundryBg
import myproject.yuikarentcos.ui.StatusLaundryText
import myproject.yuikarentcos.ui.StatusReadyBg
import myproject.yuikarentcos.ui.StatusReadyText
import myproject.yuikarentcos.ui.StatusRentedBg
import myproject.yuikarentcos.ui.TextDark
import myproject.yuikarentcos.ui.TextGray


class DetailInventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. TANGKAP DATA DARI INTENT
        val name = intent.getStringExtra("NAME") ?: "Unknown"
        val series = intent.getStringExtra("SERIES") ?: "-"
        val code = intent.getStringExtra("CODE") ?: "-"
        val category = intent.getStringExtra("CATEGORY") ?: "Costume"
        val status = intent.getStringExtra("STATUS") ?: "Ready"
        val description = intent.getStringExtra("DESCRIPTION") ?: "Tidak ada deskripsi tambahan."
        val imageUrl = intent.getStringExtra("IMAGE_URL") ?: "" // <--- AMBIL URL FOTO

        setContent {
            DetailScreen(
                name = name,
                series = series,
                code = code,
                category = category,
                status = status,
                description = description,
                imageUrl = imageUrl, // Kirim ke UI
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun DetailScreen(
    name: String,
    series: String,
    code: String,
    category: String,
    status: String,
    description: String,
    imageUrl: String,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Tentukan Warna Status (Tambah Repair)
    val (statusColor, statusBg) = when (status) {
        "Ready" -> StatusReadyText to StatusReadyBg
        "Rented" -> PurplePrimary to StatusRentedBg
        "Laundry" -> StatusLaundryText to StatusLaundryBg
        "Repair" -> Color(0xFFB91C1C) to Color(0xFFFEE2E2) // Merah
        else -> Color.Gray to Color.LightGray
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // --- A. HEADER IMAGE BESAR ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Sedikit dipertinggi biar lega
                    .background(Color(0xFFF3F4F6))
            ) {
                // LOGIKA TAMPILAN GAMBAR
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Detail Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback Icon jika tidak ada gambar
                    Icon(
                        imageVector = when(category) {
                            "Wig" -> Icons.Default.Face
                            "Shoes" -> Icons.Default.RollerSkating
                            else -> Icons.Default.Checkroom
                        },
                        contentDescription = null,
                        tint = Color.Gray.copy(0.5f),
                        modifier = Modifier.size(100.dp).align(Alignment.Center)
                    )
                }

                // Gradient Hitam Tipis di Atas (Biar tombol back kelihatan jelas)
                Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(0.4f), Color.Transparent)
                    )
                ))

                // Tombol Back Floating
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(top = 48.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.9f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            // --- B. KONTEN DETAIL (Sheet Melengkung) ---
            Column(
                modifier = Modifier
                    .offset(y = (-32).dp) // Efek menumpuk ke atas lebih dalam
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                // 1. Judul & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(0.2f))
                    ) {
                        Text(
                            text = status.uppercase(),
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = code, // Harga Sewa
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Nama Barang
                Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Text(text = series, fontSize = 16.sp, color = TextGray)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Info Grid (Kategori & Dummy Size)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DetailInfoCard("Category", category, Icons.Default.Category, Modifier.weight(1f))
                    DetailInfoCard("Size", "All Size", Icons.Default.Straighten, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Deskripsi & Kondisi
                Text("Description & Condition", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                // Menampilkan Deskripsi dari Database
                Text(
                    text = description.ifEmpty { "Tidak ada catatan kondisi khusus." },
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(100.dp)) // Padding bawah biar tombol gak nutupin
            }
        }

        // --- C. TOMBOL AKSI BAWAH ---
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shadowElevation = 16.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* TODO: Arahkan ke Edit Dialog */ },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E5F5), contentColor = PurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Info")
                }
                Button(
                    onClick = { /* TODO: Hapus Item */ },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun DetailInfoCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(PurpleSoftBgStart, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 10.sp, color = TextGray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1)
        }
    }
}