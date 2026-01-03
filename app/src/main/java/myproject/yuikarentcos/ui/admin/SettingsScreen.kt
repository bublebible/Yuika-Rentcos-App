package myproject.yuikarentcos.ui.admin

import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.EmailAuthProvider // PENTING: Untuk Re-Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import myproject.yuikarentcos.ui.GlassWhite
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart
import myproject.yuikarentcos.ui.TextDark
import myproject.yuikarentcos.ui.auth.LoginActivity

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // --- STATE DATA ---
    var userName by remember { mutableStateOf(currentUser?.displayName ?: "Admin Yuika") }
    var userPhone by remember { mutableStateOf("+62 812-3456-7890") }
    var userEmail by remember { mutableStateOf(currentUser?.email ?: "admin@yuikarent.com") }

    // --- STATE FOTO PROFIL ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }

    // --- STATE DIALOG ---
    var showProfileDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) } // Dialog Ganti Nama
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Variabel Sementara
    var tempInput by remember { mutableStateOf("") }

    // Variabel Khusus Password
    var oldPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd),
        start = Offset(0f, 0f), end = Offset(1000f, 1000f)
    )

    Box(modifier = Modifier.fillMaxSize().background(brush = backgroundBrush)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- KARTU PROFIL ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassWhite)
                    .border(1.dp, Color.White, RoundedCornerShape(24.dp))
                    .clickable { showProfileDialog = true }
                    .padding(24.dp)
            ) {
                // FOTO
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(60.dp))
                    }
                    Box(Modifier.align(Alignment.BottomEnd).padding(6.dp).clip(CircleShape).background(PurplePrimary).padding(6.dp)) {
                        Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // NAMA & INFO
                Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(userEmail, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LIST MENU ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // 1. Username
                SettingsItem(Icons.Default.Badge, "Username", userName) {
                    tempInput = userName
                    showUsernameDialog = true
                }

                // 2. Phone
                SettingsItem(Icons.Default.Phone, "Phone Number", userPhone) {
                    tempInput = userPhone
                    showPhoneDialog = true
                }

                // 3. Password (Ubah Keamanan)
                SettingsItem(Icons.Default.Lock, "Change Password", "Update security") {
                    oldPasswordInput = ""
                    newPasswordInput = ""
                    showPasswordDialog = true
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- LOGOUT ---
            Button(
                onClick = {
                    auth.signOut()
                    Toast.makeText(context, "Berhasil Logout", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        // ================= POP-UP DIALOGS =================

        // 1. DIALOG GANTI FOTO
        if (showProfileDialog) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                title = { Text("Ubah Foto Profil") },
                text = { Text("Ambil foto dari galeri perangkat anda.") },
                confirmButton = {
                    Button(onClick = { galleryLauncher.launch("image/*"); showProfileDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)) { Text("Buka Galeri") }
                },
                dismissButton = { TextButton(onClick = { showProfileDialog = false }) { Text("Batal") } }
            )
        }

        // 2. DIALOG GANTI USERNAME
        if (showUsernameDialog) {
            AlertDialog(
                onDismissRequest = { showUsernameDialog = false },
                title = { Text("Ganti Username") },
                text = {
                    OutlinedTextField(
                        value = tempInput, onValueChange = { tempInput = it },
                        label = { Text("Nama Baru") }, singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        // Update UI
                        userName = tempInput
                        // Update Firebase Profile
                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(tempInput).build()
                        currentUser?.updateProfile(profileUpdates)

                        Toast.makeText(context, "Username diganti!", Toast.LENGTH_SHORT).show()
                        showUsernameDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)) { Text("Simpan") }
                },
                dismissButton = { TextButton(onClick = { showUsernameDialog = false }) { Text("Batal") } }
            )
        }

        // 3. DIALOG GANTI PHONE
        if (showPhoneDialog) {
            AlertDialog(
                onDismissRequest = { showPhoneDialog = false },
                title = { Text("Edit Nomor HP") },
                text = { OutlinedTextField(value = tempInput, onValueChange = { tempInput = it }, label = { Text("Nomor Baru") }, singleLine = true) },
                confirmButton = {
                    Button(onClick = { userPhone = tempInput; showPhoneDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)) { Text("Simpan") }
                },
                dismissButton = { TextButton(onClick = { showPhoneDialog = false }) { Text("Batal") } }
            )
        }

        // 4. DIALOG GANTI PASSWORD (DENGAN RE-AUTH & WARNING)
        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { if (!isLoading) showPasswordDialog = false },
                title = { Text("Ganti Password") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Input Password Lama
                        OutlinedTextField(
                            value = oldPasswordInput,
                            onValueChange = { oldPasswordInput = it },
                            label = { Text("Password Lama (Konfirmasi)") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Input Password Baru
                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { newPasswordInput = it },
                            label = { Text("Password Baru") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Peringatan Keamanan
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFF57C00), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Password minimal 6 karakter. Gunakan kombinasi huruf & angka agar lebih aman.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (oldPasswordInput.isEmpty() || newPasswordInput.isEmpty()) {
                                Toast.makeText(context, "Mohon isi semua kolom", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (newPasswordInput.length < 6) {
                                Toast.makeText(context, "Password baru terlalu pendek!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true

                            // 1. RE-AUTHENTICATE (Cek Password Lama)
                            val credential = EmailAuthProvider.getCredential(currentUser?.email!!, oldPasswordInput)

                            currentUser.reauthenticate(credential).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // 2. JIKA COCOK, UPDATE PASSWORD BARU
                                    currentUser.updatePassword(newPasswordInput).addOnCompleteListener { updateTask ->
                                        isLoading = false
                                        if (updateTask.isSuccessful) {
                                            Toast.makeText(context, "Password Berhasil Diganti!", Toast.LENGTH_LONG).show()
                                            showPasswordDialog = false
                                        } else {
                                            Toast.makeText(context, "Gagal Update: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "Password Lama Salah!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) Text("Memproses...") else Text("Ganti Password")
                    }
                },
                dismissButton = {
                    if(!isLoading) TextButton(onClick = { showPasswordDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

// --- KOMPONEN ITEM ---
@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.9f)).clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(PurplePrimary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = PurplePrimary)
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextDark)
            Text(value, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
    }
}