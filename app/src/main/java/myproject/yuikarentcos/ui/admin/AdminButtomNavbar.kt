package myproject.yuikarentcos.ui.admin

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import myproject.yuikarentcos.ui.PurplePrimary


@Composable
fun AdminBottomNavBar(currentTab: String) {
    val context = LocalContext.current

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
            // 1. HOME
            BottomNavItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                isActive = currentTab == "Home",
                onClick = {
                    if (currentTab != "Home") {
                        val intent = Intent(context, DashboardAdminActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        context.startActivity(intent)
                    }
                }
            )

            // 2. INVENTORY
            BottomNavItem(
                label = "Inventory",
                icon = Icons.Outlined.Search, // Atau Icons.Outlined.Checkroom
                isActive = currentTab == "Inventory",
                onClick = {
                    if (currentTab != "Inventory") {
                        val intent = Intent(context, InventoryActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        context.startActivity(intent)
                    }
                }
            )

            // 3. CONTENT (PENGGANTI INBOX)
            BottomNavItem(
                label = "Content",
                icon = Icons.Outlined.WebStories, // Ikon Content/Banner
                isActive = currentTab == "Content",
                onClick = {
                    /* TODO: Nanti bikin ContentActivity buat manage banner */
                    // val intent = Intent(context, ContentActivity::class.java)
                    // context.startActivity(intent)
                }
            )

            // 4. SETTINGS
            BottomNavItem(
                label = "Settings",
                icon = Icons.Outlined.Settings,
                isActive = currentTab == "Settings",
                onClick = { /* TODO: Settings */ }
            )
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
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
            color = color
        )
    }
}