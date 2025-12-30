package myproject.yuikarentcos.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myproject.yuikarentcos.ui.GlassWhite
import myproject.yuikarentcos.ui.PinkPrimary
import myproject.yuikarentcos.ui.TextDark

class DashboardAdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen()
        }
    }
}

@Composable
fun DashboardScreen() {
    // Background Mesh Gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFDF2F8), Color(0xFFFCE7F3), Color(0xFFE0F2FE))
                )
            )
    ) {
        // Langsung panggil layout Mobile (Desktop dihapus)
        MobileDashboard()
    }
}

// ================= LAYOUT UTAMA =================
@Composable
fun MobileDashboard() {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = GlassWhite,
                modifier = Modifier.border(1.dp, Color.White.copy(alpha=0.5f))
            ) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Dashboard, null, tint = PinkPrimary) }, label = { Text("Home", color = PinkPrimary) })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Search, null, tint = Color.Gray) }, label = { Text("Search") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Mail, null, tint = Color.Gray) }, label = { Text("Inbox") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, null, tint = Color.Gray) }, label = { Text("Settings") })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            OverviewSection()
            Spacer(modifier = Modifier.height(24.dp))
            RevenueChart()
            Spacer(modifier = Modifier.height(24.dp))
            ManagementSection()
            Spacer(modifier = Modifier.height(24.dp))
            RecentActivitySection()
        }
    }
}

// ================= KOMPONEN UI =================

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("ADMIN PORTAL", style = MaterialTheme.typography.labelSmall, color = PinkPrimary, fontWeight = FontWeight.Bold)
                Text("Yuika Rentcos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color  = TextDark)
            }
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(GlassWhite)
                .border(1.dp, Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.Gray)
            Box(Modifier.align(Alignment.TopEnd).padding(10.dp).size(8.dp).clip(CircleShape).background(PinkPrimary))
        }
    }
}

@Composable
fun OverviewSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionTitle(title = "Overview", color = PinkPrimary)
            Text("Last 7 Days", fontSize = 12.sp, color = PinkPrimary, modifier = Modifier.background(GlassWhite, CircleShape).padding(horizontal = 12.dp, vertical = 6.dp))
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                // UBAH DISINI: Total Orders -> Total Pendapatan
                StatCard(
                    title = "TOTAL PENDAPATAN",
                    value = "Rp 12.5jt", // Contoh nilai rupiah
                    percent = "+15%",
                    icon = Icons.Default.MonetizationOn, // Ganti icon jadi uang
                    color = PinkPrimary,
                    isGlass = true
                )
            }
            item {
                StatCard(
                    title = "ACTIVE RENTALS",
                    value = "45",
                    percent = "5%",
                    icon = Icons.Default.HourglassTop,
                    color = Color.White,
                    bgColor = Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFBE185D))),
                    textColor = Color.White,
                    isGlass = false
                )
            }
            item {
                StatCard(
                    title = "PENDING VALIDATION",
                    value = "8",
                    percent = "Action",
                    icon = Icons.Default.PendingActions,
                    color = Color(0xFFF97316),
                    isGlass = true
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String, value: String, percent: String, icon: ImageVector,
    color: Color, bgColor: Brush? = null, textColor: Color = TextDark, isGlass: Boolean
) {
    val modifier = if (isGlass) {
        Modifier
            .background(GlassWhite)
            .border(1.dp, Color.White, RoundedCornerShape(32.dp))
    } else {
        Modifier.background(bgColor ?: Brush.linearGradient(listOf(Color.White, Color.White)))
    }

    Column(
        modifier = modifier
            .width(160.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(32.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.size(40.dp).background(Color.White.copy(0.5f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Text(percent, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.background(color.copy(0.1f), CircleShape).padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Column {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textColor.copy(0.6f))
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun RevenueChart() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(GlassWhite)
            .border(1.dp, Color.White, RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Weekly Revenue", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("↑ 3.2% vs last week", fontSize = 12.sp, color = Color(0xFF10B981))
            }
            Icon(Icons.Default.MoreHoriz, null, tint = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, height * 0.7f)
                cubicTo(width * 0.1f, height * 0.2f, width * 0.2f, height * 0.9f, width * 0.3f, height * 0.5f)
                cubicTo(width * 0.4f, height * 0.2f, width * 0.5f, height * 0.6f, width * 0.6f, height * 0.1f)
                cubicTo(width * 0.7f, height * 0.5f, width * 0.8f, height * 0.8f, width * 0.9f, height * 0.3f)
                lineTo(width, height * 0.2f)
            }

            drawPath(
                path = path,
                color = PinkPrimary,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawCircle(PinkPrimary, radius = 4.dp.toPx(), center = Offset(width * 0.6f, height * 0.1f))
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(it, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ManagementSection() {
    val context = LocalContext.current // Butuh ini untuk pindah halaman

    Column {
        SectionTitle(title = "Management", color = Color(0xFF6366F1))
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // LOGIKA PINDAH KE INVENTORY & WARNA PINK
                ManagementCard(
                    title = "Inventory",
                    sub = "1,204 Items",
                    icon = Icons.Default.Checkroom,
                    initialColor = Color(0xFF6366F1), // Warna awal ungu
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Pindah ke SearchActivity (Halaman Inventory)
                        context.startActivity(Intent(context, SearchActivity::class.java))
                    }
                )

                ManagementCard(
                    title = "Orders",
                    sub = "8 Pending",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    initialColor = Color(0xFF0D9488),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Pindah ke Orders */ }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ManagementCard(
                    title = "Reports",
                    sub = "Analytics",
                    icon = Icons.Default.BarChart,
                    initialColor = Color(0xFF9333EA),
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Pindah ke Reports */ }
                )
                ManagementCard(
                    title = "Content",
                    sub = "Banners",
                    icon = Icons.Default.WebStories,
                    initialColor = PinkPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Pindah ke Content */ }
                )
            }
        }
    }
}

@Composable
fun ManagementCard(
    title: String,
    sub: String,
    icon: ImageVector,
    initialColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // State untuk mendeteksi apakah kartu sedang dipilih/diklik
    var isSelected by remember { mutableStateOf(false) }

    // Jika dipilih, background jadi PinkPrimary, jika tidak kembali ke GlassWhite
    val backgroundColor = if (isSelected) PinkPrimary else GlassWhite
    val contentColor = if (isSelected) Color.White else TextDark
    val iconTint = if (isSelected) Color.White else initialColor

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .border(1.dp, Color.White, RoundedCornerShape(32.dp))
            .clickable {
                isSelected = !isSelected // Toggle warna jadi pink
                onClick() // Jalankan aksi (pindah halaman dll)
            }
            .padding(24.dp)
    ) {
        Box(
            Modifier
                .size(56.dp)
                .background(
                    if (isSelected) Color.White.copy(0.2f) else initialColor.copy(0.1f),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = contentColor)
        Text(sub, fontSize = 10.sp, color = if (isSelected) Color.White.copy(0.8f) else Color.Gray)
    }
}

@Composable
fun RecentActivitySection() {
    Column {
        SectionTitle(title = "Recent Activity", color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))

        ActivityItem(title = "Miku Wig returned", sub = "User A • 2 mins ago", status = "Processed", icon = Icons.AutoMirrored.Filled.KeyboardReturn, color = PinkPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        ActivityItem(title = "New Order #2841", sub = "Total: $120 • 15m ago", status = "Pending", icon = Icons.Default.Inventory2, color = Color(0xFFF97316))
    }
}

@Composable
fun ActivityItem(title: String, sub: String, status: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GlassWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(Color.White.copy(0.6f), RoundedCornerShape(16.dp)).border(1.dp, Color.White, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(sub, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Text(status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.background(color.copy(0.1f), CircleShape).padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(4.dp, 24.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}