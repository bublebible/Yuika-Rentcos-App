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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart


class InventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Panggil Screen tanpa parameter aneh-aneh
            InventoryScreen()
        }
    }
}

// --- DATA MODEL ---
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
fun InventoryScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // --- STATE ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var inventoryList by remember { mutableStateOf<List<InventoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var currentEditingId by remember { mutableStateOf("") }

    var nameInput by remember { mutableStateOf("") }
    var seriesInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Costume") }
    var statusInput by remember { mutableStateOf("Ready") }
    var descriptionInput by remember { mutableStateOf("") }

    // --- FETCH DATA ---
    LaunchedEffect(Unit) {
        db.collection("inventory").addSnapshotListener { snapshot, e ->
            if (e != null) {
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

    // --- SEARCH & FILTER ---
    val filteredItems = inventoryList.filter { item ->
        val categoryMatch = if (selectedCategory == "All") true else item.category == selectedCategory
        val searchMatch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.series.contains(searchQuery, ignoreCase = true)
        categoryMatch && searchMatch
    }

    // --- CRUD ---
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
            db.collection("inventory").document(currentEditingId).update(itemData as Map<String, Any>)
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
        } else {
            db.collection("inventory").add(itemData)
            Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show()
        }
        showDialog = false
    }

    fun deleteItem(id: String) {
        db.collection("inventory").document(id).delete()
            .addOnSuccessListener { Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show() }
    }

    fun openAddDialog() {
        isEditing = false; nameInput = ""; seriesInput = ""; codeInput = ""; descriptionInput = ""
        categoryInput = "Costume"; statusInput = "Ready"; showDialog = true
    }

    fun openEditDialog(item: InventoryItem) {
        isEditing = true; currentEditingId = item.id; nameInput = item.name
        seriesInput = item.series; codeInput = item.code; descriptionInput = item.description
        categoryInput = item.category; statusInput = item.status; showDialog = true
    }

    // --- UI ---
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd),
        start = Offset(0f, 0f), end = Offset(1000f, 1000f)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openAddDialog() },
                containerColor = Color(0xFFFF80AB),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 60.dp)
            ) { Icon(Icons.Default.Add, "Add") }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            // Navbar pintar dipanggil disini
            AdminBottomNavBar(currentTab = "Inventory")
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(backgroundBrush).padding(padding)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(24.dp))
                Text("Inventory", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827), modifier = Modifier.padding(bottom = 16.dp))

                SearchBar(searchQuery) { searchQuery = it }
                Spacer(Modifier.height(16.dp))

                CategoryFilter(listOf("All", "Costume", "Wig", "Shoes", "Additional"), selectedCategory) { selectedCategory = it }
                Spacer(Modifier.height(16.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PurplePrimary) }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredItems) { item ->
                            InventoryItemCard(
                                item = item,
                                onEditClick = { openEditDialog(item) },
                                onDeleteClick = { deleteItem(item.id) },
                                onCardClick = {
                                    val intent = Intent(context, DetailInventoryActivity::class.java).apply {
                                        putExtra("NAME", item.name)
                                        putExtra("SERIES", item.series)
                                        putExtra("CODE", item.code)
                                        putExtra("CATEGORY", item.category)
                                        putExtra("STATUS", item.status)
                                        putExtra("DESCRIPTION", item.description)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isEditing) "Edit Item" else "Add Item") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(nameInput, { nameInput = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(seriesInput, { seriesInput = it }, label = { Text("Series") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(codeInput, { codeInput = it }, label = { Text("Price (e.g. 45k)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(descriptionInput, { descriptionInput = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 3)

                        Text("Category:", fontSize = 12.sp, color = Color.Gray)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Costume", "Wig", "Shoes", "Additional").forEach {
                                FilterChip(selected = categoryInput == it, onClick = { categoryInput = it }, label = { Text(it) }, modifier = Modifier.padding(end = 4.dp))
                            }
                        }

                        Text("Status:", fontSize = 12.sp, color = Color.Gray)
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            listOf("Ready", "Rented", "Laundry").forEach {
                                FilterChip(selected = statusInput == it, onClick = { statusInput = it }, label = { Text(it) }, modifier = Modifier.padding(end = 4.dp))
                            }
                        }
                    }
                },
                confirmButton = { Button(onClick = { saveData() }, colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)) { Text("Save") } },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

// --- COMPONENTS ---
@Composable
fun InventoryItemCard(item: InventoryItem, onEditClick: () -> Unit, onDeleteClick: () -> Unit, onCardClick: () -> Unit) {
    val (statusColor, statusBg) = when (item.status) {
        "Ready" -> Color(0xFF16A34A) to Color(0xFFDCFCE7)
        "Rented" -> PurplePrimary to Color(0xFFF3E5F5)
        "Laundry" -> Color(0xFFEA580C) to Color(0xFFFFEDD5)
        else -> Color.Gray to Color.LightGray
    }
    val icon = when (item.category) {
        "Costume" -> Icons.Default.Checkroom
        "Wig" -> Icons.Default.Face
        "Shoes" -> Icons.Default.RollerSkating
        "Additional" -> Icons.Default.Backpack
        else -> Icons.Outlined.Image
    }

    Surface(
        shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() }
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF3F4F6), modifier = Modifier.size(72.dp)) {
                Icon(icon, null, tint = Color.Gray, modifier = Modifier.padding(20.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                Text(item.series, fontSize = 12.sp, color = Color(0xFF6B7280))
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PurplePrimary)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = statusBg, shape = RoundedCornerShape(50)) {
                        Text(item.status.uppercase(), color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp).clickable { onEditClick() })
                Icon(Icons.Default.Delete, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp).clickable { onDeleteClick() })
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(50), shadowElevation = 4.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Icon(Icons.Default.Search, null, tint = Color.Gray)
            Spacer(Modifier.width(8.dp))
            androidx.compose.foundation.text.BasicTextField(value = query, onValueChange = onQueryChange, modifier = Modifier.weight(1f))
            Icon(Icons.Default.Tune, null, tint = Color.Gray)
        }
    }
}

@Composable
fun CategoryFilter(categories: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { cat ->
            val isSelected = cat == selected
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) PurplePrimary else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
                modifier = Modifier.clickable { onSelect(cat) }
            ) {
                Text(cat, color = if (isSelected) Color.White else Color.Gray, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
        }
    }
}