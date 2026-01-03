package myproject.yuikarentcos.ui.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke // Import BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong // Import Icon Receipt
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // <--- INI YANG TADINYA HILANG
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgStart
import myproject.yuikarentcos.ui.TextDark
import myproject.yuikarentcos.ui.admin.InventoryItem

class HomeUserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserHomeScreen()
        }
    }
}

// Data Class Promo Sederhana
data class PromotionItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val endDate: Long = 0
)

@Composable
fun UserHomeScreen() {
    val db = FirebaseFirestore.getInstance()
    val scrollState = rememberScrollState()

    // --- STATE DATA ---
    var promotionList by remember { mutableStateOf<List<PromotionItem>>(emptyList()) }
    var productList by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // --- FETCH DATA ---
    LaunchedEffect(Unit) {
        // 1. Fetch Promotions
        val currentTime = System.currentTimeMillis()
        db.collection("promotions")
            .whereGreaterThan("endDate", currentTime)
            .get()
            .addOnSuccessListener { result ->
                val promos = result.documents.map { doc ->
                    PromotionItem(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        endDate = doc.getLong("endDate") ?: 0L
                    )
                }
                promotionList = promos
            }

        // 2. Fetch Products
        db.collection("inventory")
            .whereEqualTo("status", "Ready")
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val products = result.documents.map { doc ->
                    // Ambil list foto
                    val imgField = doc.get("imageUrls")
                    val imgList = if (imgField is List<*>) {
                        imgField.map { it.toString() }
                    } else {
                        val singleImg = doc.getString("imageUrl")
                        if (singleImg.isNullOrEmpty()) emptyList() else listOf(singleImg)
                    }

                    InventoryItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        series = doc.getString("series") ?: "",
                        code = doc.getString("code") ?: "",
                        category = doc.getString("category") ?: "Costume",
                        status = doc.getString("status") ?: "Ready",
                        imageUrls = imgList
                    )
                }
                productList = products
            }
    }

    // Filter Lokal
    val filteredProducts = if (selectedCategory == "All") productList else productList.filter { it.category == selectedCategory }

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        bottomBar = { UserFloatingNavBar() }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // 1. HEADER
            HeaderSection()

            // 2. SEARCH BAR
            SearchBarSection(searchQuery) { searchQuery = it }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. CAROUSEL PROMO
            if (promotionList.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(promotionList) { promo ->
                        PromoCard(promo)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleSoftBgStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Active Promotions", color = PurplePrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. CATEGORIES
            CategorySection(selectedCategory) { selectedCategory = it }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. PRODUCT GRID
            ProductSection(filteredProducts)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ================= KOMPONEN UI =================

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://ui-avatars.com/api/?name=User+Yuika&background=random",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, PurplePrimary.copy(0.2f), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Hello, Cosplayer!", fontSize = 12.sp, color = Color.Gray)
                Text("Yuika Rentcos", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color.Gray.copy(0.1f), CircleShape)
            ) {
                Box {
                    Icon(Icons.Outlined.Notifications, null, tint = TextDark)
                    Box(Modifier.size(8.dp).clip(CircleShape).background(PurplePrimary).align(Alignment.TopEnd))
                }
            }
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color.Gray.copy(0.1f), CircleShape)
            ) {
                Icon(Icons.Outlined.ShoppingBag, null, tint = TextDark)
            }
        }
    }
}

@Composable
fun SearchBarSection(query: String, onQueryChange: (String) -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .shadow(4.dp, RoundedCornerShape(50), spotColor = PurplePrimary.copy(0.1f))
                .background(Color.White, RoundedCornerShape(50))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = PurplePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) Text("Cari Kostum...", color = Color.Gray)
                    innerTextField()
                }
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Tune, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun PromoCard(promo: PromotionItem) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
    ) {
        AsyncImage(
            model = promo.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Black.copy(0.7f), Color.Transparent))))

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp)
                .width(200.dp)
        ) {
            Surface(color = PurplePrimary, shape = RoundedCornerShape(4.dp)) {
                Text("PROMO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(promo.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
            Text(promo.description, color = Color.White.copy(0.8f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CategorySection(selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Categories", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("See all", fontSize = 14.sp, color = PurplePrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val categories = listOf("All", "Costume", "Wig", "Shoes", "Prop")
            items(categories) { cat ->
                val isSelected = selected == cat
                val bgColor = if (isSelected) PurplePrimary else Color.White
                val txtColor = if (isSelected) Color.White else TextDark
                val border = if (isSelected) null else BorderStroke(1.dp, Color.Gray.copy(0.2f))

                Surface(
                    shape = RoundedCornerShape(50),
                    color = bgColor,
                    border = border,
                    modifier = Modifier.clickable { onSelect(cat) }
                ) {
                    Text(
                        text = cat,
                        color = txtColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductSection(products: List<InventoryItem>) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text("Popular Costumes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(16.dp))

        if (products.isEmpty()) {
            Text("No items found.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            val chunked = products.chunked(2)
            chunked.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowItems.forEach { item ->
                        Box(Modifier.weight(1f)) {
                            UserProductCard(item)
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UserProductCard(item: InventoryItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Gray.copy(0.1f))
        ) {
            AsyncImage(
                model = item.imageUrls.firstOrNull() ?: "",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                color = Color(0xFF22C55E).copy(0.9f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
            ) {
                Text("READY", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.2f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Text("4.8", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text("${item.series} • All Size", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(4.dp))
        Text(item.code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PurplePrimary)
    }
}

@Composable
fun UserFloatingNavBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(50), spotColor = Color.Black.copy(0.1f))
                .background(Color.White, RoundedCornerShape(50))
                .border(1.dp, Color.Gray.copy(0.1f), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(Icons.Default.Home, isSelected = true)
            NavBarItem(Icons.Default.Search, isSelected = false)

            // MENU ORDER (Tengah)
            Box(
                modifier = Modifier
                    .offset(y = (-20).dp)
                    .size(56.dp)
                    .shadow(10.dp, CircleShape, spotColor = PurplePrimary.copy(0.4f))
                    .background(PurplePrimary, CircleShape)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                // Gunakan Icon ReceiptLong versi AutoMirrored (FIX)
                Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }

            NavBarItem(Icons.Outlined.ShoppingCart, isSelected = false)
            NavBarItem(Icons.Outlined.Person, isSelected = false)
        }
    }
}

@Composable
fun NavBarItem(icon: ImageVector, isSelected: Boolean) {
    IconButton(onClick = { /* Navigate */ }) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) PurplePrimary else Color.Gray.copy(0.6f),
            modifier = Modifier.size(28.dp)
        )
    }
}