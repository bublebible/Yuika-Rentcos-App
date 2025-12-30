package myproject.yuikarentcos.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart



class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(
                onHomeClick = { finish() }, // Kembali ke dashboard
                onSearchClick = { /* Sudah di sini */ },
                onInboxClick = { /* TODO */ },
                onSettingsClick = { /* TODO */ }
            )
        }
    }
}


data class InventoryItem(
    val id: String = "",
    val name: String = "",
    val series: String = "",
    val code: String = "",
    val category: String = "Costume",
    val status: String = "Ready",
    val description: String = ""
)

@Composable
fun SearchScreen(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onInboxClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // --- STATE ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // State Data List
    var inventoryList by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // State Dialog (Tambah/Edit)
    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var currentEditingId by remember { mutableStateOf("") }

    // Form State
    var nameInput by remember { mutableStateOf("") }
    var seriesInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Costume") }
    var statusInput by remember { mutableStateOf("Ready") }
    var descriptionInput by remember { mutableStateOf("") }

    // --- 1. FETCH DATA REALTIME DARI FIRESTORE ---
    LaunchedEffect(Unit) {
        db.collection("inventory").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.documents.map { doc ->
                    InventoryItem(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        series = doc.getString("series") ?: "",
                        code = doc.getString("code") ?: "",
                        category = doc.getString("category") ?: "Costume",
                        status = doc.getString("status") ?: "Ready",
                        description = doc.getString("description") ?: ""
                    )
                }
                inventoryList = items
                isLoading = false
            }
        }
    }

    // --- 2. LOGIKA SEARCH & FILTER ---
    val filteredItems = inventoryList.filter { item ->
        // Filter Kategori
        val categoryMatch = if (selectedCategory == "All") true else item.category == selectedCategory
        // Filter Pencarian (Nama atau Series)
        val searchMatch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.series.contains(searchQuery, ignoreCase = true)

        categoryMatch && searchMatch
    }

    // --- 3. FUNGSI CRUD ---
    fun saveData() {
        if (nameInput.isEmpty() || seriesInput.isEmpty() || codeInput.isEmpty()) {
            Toast.makeText(context, "Mohon isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val itemData = hashMapOf(
            "name" to nameInput,
            "series" to seriesInput,
            "code" to codeInput,
            "category" to categoryInput,
            "status" to statusInput,
            "description" to descriptionInput
        )

        if (isEditing) {
            // UPDATE
            db.collection("inventory").document(currentEditingId)
                .update(itemData as Map<String, Any>)
                .addOnSuccessListener { Toast.makeText(context, "Barang diupdate!", Toast.LENGTH_SHORT).show() }
        } else {
            // CREATE (ADD)
            db.collection("inventory").add(itemData)
                .addOnSuccessListener { Toast.makeText(context, "Barang ditambahkan!", Toast.LENGTH_SHORT).show() }
        }
        showDialog = false
    }

    fun deleteItem(id: String) {
        db.collection("inventory").document(id).delete()
            .addOnSuccessListener { Toast.makeText(context, "Barang dihapus", Toast.LENGTH_SHORT).show() }
    }

    fun openAddDialog() {
        isEditing = false
        nameInput = ""
        seriesInput = ""
        codeInput = ""
        descriptionInput = ""
        categoryInput = "Costume"
        statusInput = "Ready"
        showDialog = true
    }

    fun openEditDialog(item: InventoryItem) {
        isEditing = true
        currentEditingId = item.id
        nameInput = item.name
        seriesInput = item.series
        codeInput = item.code
        descriptionInput = item.description
        categoryInput = item.category
        statusInput = item.status
        showDialog = true
    }

    // --- UI UTAMA ---
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Scaffold(
        floatingActionButton = {
            // TOMBOL TAMBAH (+) BULAT SOFT PINK
            FloatingActionButton(
                onClick = { openAddDialog() },
                containerColor = Color(0xFFFF80AB), // Soft Pink
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp) // Supaya agak naik dikit diatas navbar
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        },
        floatingActionButtonPosition = FabPosition.End, // Posisi Kanan (Diatas Settings)
        bottomBar = {
            AdminBottomNavigation(
                activeTab = "Search",
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onInboxClick = onInboxClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inventory",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827)
                    )
                }

                // Search Bar
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                Spacer(modifier = Modifier.height(16.dp))

                // Filter
                val categories = listOf("All", "Costume", "Wig", "Shoes", "Additional")
                CategoryFilter(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // List Items
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PurplePrimary)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp), // Padding bawah biar gak ketutup FAB
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredItems) { item ->
                            InventoryItemCard(
                                item = item,
                                onEditClick = { openEditDialog(item) },
                                onDeleteClick = { deleteItem(item.id) },
                                // TAMBAHAN BARU: Agar bisa diklik pindah ke Detail
                                onCardClick = {
                                    val intent = Intent(context, DetailInventoryActivity::class.java).apply {
                                        putExtra("NAME", item.name)
                                        putExtra("SERIES", item.series)
                                        putExtra("CODE", item.code)
                                        putExtra("CATEGORY", item.category)
                                        putExtra("STATUS", item.status)
                                        putExtra("DESCRIPTION", item.description) // Kirim deskripsi juga
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                        if (filteredItems.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada barang ditemukan.",
                                    modifier = Modifier.fillMaxWidth().padding(top=20.dp),
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOG INPUT DATA ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = if (isEditing) "Edit Barang" else "Tambah Barang") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nama Barang") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = seriesInput,
                            onValueChange = { seriesInput = it },
                            label = { Text("Series / Anime") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = codeInput,
                            onValueChange = { codeInput = it },
                            label = { Text("Harga Sewa (ex: 45k)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descriptionInput,
                            onValueChange = { descriptionInput = it },
                            label = { Text("Deskripsi (Kondisi/Cacat)") }, // Labelnya jelas
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp), // Bikin agak tinggi
                            maxLines = 3, // Bisa muat 3 baris
                            placeholder = { Text("Contoh: Resleting agak macet, ada noda di rok...") }
                        )

                        // Pilihan Kategori (Simple Radio/Button row)
                        Text("Kategori:", fontSize = 12.sp, color = Color.Gray)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Costume", "Wig", "Shoes", "Additional").forEach { cat ->
                                FilterChip(
                                    selected = categoryInput == cat,
                                    onClick = { categoryInput = cat },
                                    label = { Text(cat) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }

                        // Pilihan Status
                        Text("Status:", fontSize = 12.sp, color = Color.Gray)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Ready", "Rented", "Laundry").forEach { stat ->
                                FilterChip(
                                    selected = statusInput == stat,
                                    onClick = { statusInput = stat },
                                    label = { Text(stat) },
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { saveData() }, colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

// --- KOMPONEN PENDUKUNG ---

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit // <--- Parameter baru ini PENTING
) {
    // 1. Tentukan Warna Status Secara Dinamis
    val (statusColor, statusBg) = when (item.status) {
        "Ready" -> Color(0xFF16A34A) to Color(0xFFDCFCE7) // Hijau
        "Rented" -> PurplePrimary to Color(0xFFF3E5F5)    // Ungu
        "Laundry" -> Color(0xFFEA580C) to Color(0xFFFFEDD5) // Oranye
        else -> Color.Gray to Color.LightGray
    }

    // 2. Tentukan Ikon Berdasarkan Kategori
    val icon = when (item.category) {
        "Costume" -> Icons.Default.Checkroom
        "Wig" -> Icons.Default.Face
        "Shoes" -> Icons.Default.RollerSkating
        "Additional" -> Icons.Default.Backpack
        else -> Icons.Outlined.Image
    }

    // 3. UI KARTU
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            // 🔥 LOGIKA KLIK KARTU DISINI 🔥
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- GAMBAR/ICON KIRI ---
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- TEXT TENGAH (NAMA, SERIES, HARGA, STATUS) ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.series,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.code, // Harga Sewa
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge Status
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = item.status.uppercase(),
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // --- TOMBOL AKSI KANAN (EDIT & DELETE) ---
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                // Tombol Edit
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onEditClick() } // Klik pensil cuma trigger Edit, bukan pindah halaman
                )

                // Tombol Delete
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDeleteClick() } // Klik sampah cuma trigger Hapus
                )
            }
        }
    }
}


@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(50), shadowElevation = 4.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) Text("Search character or series...", color = Color.Gray, fontSize = 14.sp)
                androidx.compose.foundation.text.BasicTextField(
                    value = query, onValueChange = onQueryChange,
                    textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Tune, "Filter", tint = Color.Gray, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun CategoryFilter(categories: List<String>, selectedCategory: String, onCategorySelected: (String) -> Unit) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) PurplePrimary else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.clickable { onCategorySelected(category) }.padding(vertical = 4.dp)
            ) {
                Text(category, color = if (isSelected) Color.White else Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
fun AdminBottomNavigation(activeTab: String, onHomeClick: () -> Unit, onSearchClick: () -> Unit, onInboxClick: () -> Unit, onSettingsClick: () -> Unit) {
    Surface(color = Color.White.copy(alpha = 0.95f), shadowElevation = 16.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            BottomNavItem("Home", Icons.Outlined.Home, activeTab == "Home", onHomeClick)
            BottomNavItem("Search", Icons.Outlined.Search, activeTab == "Search", onSearchClick)
            BottomNavItem("Inbox", Icons.Outlined.Inbox, activeTab == "Inbox", onInboxClick)
            BottomNavItem("Settings", Icons.Outlined.Settings, activeTab == "Settings", onSettingsClick)
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    val color = if (isActive) PurplePrimary else Color(0xFF9CA3AF)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).width(64.dp)) {
        Icon(icon, label, tint = color, modifier = Modifier.size(26.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium, color = color)
    }
}

