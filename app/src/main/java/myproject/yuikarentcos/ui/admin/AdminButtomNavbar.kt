package myproject.yuikarentcos.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myproject.yuikarentcos.ui.PurplePrimary

@Composable
fun AdminBottomNavBar(
    selectedIndex: Int, // Menerima nomor halaman (0, 1, 2, 3)
    onItemSelected: (Int) -> Unit // Fungsi callback saat diklik
) {
    Surface(
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("Home", Icons.Outlined.Home, selectedIndex == 0) { onItemSelected(0) }
            BottomNavItem("Inventory", Icons.Outlined.Search, selectedIndex == 1) { onItemSelected(1) }
            BottomNavItem("Content", Icons.Outlined.WebStories, selectedIndex == 2) { onItemSelected(2) }
            BottomNavItem("Settings", Icons.Outlined.Settings, selectedIndex == 3) { onItemSelected(3) }
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    val color = if (isActive) PurplePrimary else Color(0xFF9CA3AF)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(64.dp)
    ) {
        Icon(icon, label, tint = color, modifier = Modifier.size(26.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium, color = color)
    }
}