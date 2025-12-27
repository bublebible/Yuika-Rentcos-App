package myproject.yuikarentcos.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : ComponentActivity() {

    // Inisialisasi Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            // Panggil Screen Utama
            RegisterScreen(
                onBackClick = { finish() },
                onLoginClick = { finish() },
                onRegisterClick = { nama, username, email, password ->
                    prosesRegister(nama, username, email, password)
                }
            )
        }
    }

    // --- LOGIKA REGISTER KE FIREBASE ---
    private fun prosesRegister(nama: String, username: String, email: String, password: String) {
        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan loading (opsional, bisa tambah state loading di UI)
        Toast.makeText(this, "Mendaftarkan...", Toast.LENGTH_SHORT).show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    val userMap = hashMapOf(
                        "uid" to uid,
                        "fullName" to nama,
                        "username" to username,
                        "email" to email,
                        "role" to "user", // Default user biasa
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_LONG).show()
                            auth.signOut() // Logout agar user login manual
                            finish()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

// --- DEFINE COLORS ---
private val PurplePrimary = Color(0xFF673AB7)
private val PurpleSoftBgStart = Color(0xFFF3E5F5)
private val PurpleSoftBgEnd = Color(0xFFD1C4E9)
private val HeaderBg = Color(0xFFD5C1E7)

// Warna Level Password (INI JUGA DI PRIVATE)
private val StrengthWeak = Color(0xFFE53935)
private val StrengthFair = Color(0xFFFFB300)
private val StrengthGood = Color(0xFF43A047)
private val StrengthStrong = Color(0xFF1B5E20)
private val StrengthNone = Color(0xFFE0E0E0)

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: (String, String, String, String) -> Unit
) {
    // State untuk menyimpan input user
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Brush untuk background gradient (Soft Purple Mesh)
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    // --- LOGIKA HITUNG SKOR PASSWORD ---
    val passwordScore = remember(password) {
        var score = 0
        if (password.isNotEmpty()) {
            if (password.length < 6) {
                score = 1 // Merah: Terlalu pendek
            } else {
                score = 2 // Kuning: Panjang cukup

                // Cek variasi karakter
                val hasDigit = password.any { it.isDigit() }
                val hasUpper = password.any { it.isUpperCase() }

                if (password.length >= 8 && (hasDigit || hasUpper)) {
                    score = 3 // Hijau: Kuat
                }
                if (password.length >= 8 && hasDigit && hasUpper) {
                    score = 4 // Hijau Tua: Sangat Kuat
                }
            }
        }
        score
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. HEADER SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderBg)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Yuika RentCos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // --- 2. LOGO SECTION ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)), // Transparan dikit
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom, // Ganti icon baju
                        contentDescription = "Logo",
                        tint = PurplePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Join the Fun!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Create an account to start renting your dream cosplay.",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // --- 3. FORM SECTION ---
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                // Input Full Name
                CustomRegisterInput(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    icon = Icons.Default.Badge
                )

                // Input Username
                CustomRegisterInput(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    icon = Icons.Default.AlternateEmail,
                    topMargin = 16.dp
                )

                // Input Email
                CustomRegisterInput(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    topMargin = 16.dp
                )

                // Input Password
                CustomRegisterInput(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    topMargin = 16.dp
                )

                // Password Strength Bar (Statis sesuai XML)
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    StrengthBar(Color(0xFF4CAF50)) // Hijau
                    StrengthBar(Color(0xFF4CAF50)) // Hijau
                    StrengthBar(Color(0xFFE0E0E0)) // Abu
                    StrengthBar(Color(0xFFE0E0E0)) // Abu
                }

                // --- VISUALISASI BAR PASSWORD ---
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tentukan warna aktif berdasarkan skor
                    val activeColor = when (passwordScore) {
                        1 -> StrengthWeak
                        2 -> StrengthFair
                        3 -> StrengthGood
                        4 -> StrengthStrong
                        else -> StrengthNone
                    }

                    // Render 4 Bar
                    StrengthBar(if (passwordScore >= 1) activeColor else StrengthNone)
                    StrengthBar(if (passwordScore >= 2) activeColor else StrengthNone)
                    StrengthBar(if (passwordScore >= 3) activeColor else StrengthNone)
                    StrengthBar(if (passwordScore >= 4) activeColor else StrengthNone)
                }

                // Opsional: Teks Status
                if (passwordScore > 0) {
                    val statusText = when(passwordScore) {
                        1 -> "Tidak Aman"
                        2 -> "Kurang Aman"
                        3 -> "Aman"
                        4 -> "Sangat Aman"
                        else -> ""
                    }
                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        color = if(passwordScore == 1) StrengthWeak else Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Input Confirm Password
                CustomRegisterInput(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    icon = Icons.Default.LockReset,
                    isPassword = true,
                    topMargin = 16.dp
                )

                // Tombol Daftar
                Button(
                    onClick = { onRegisterClick(fullName, username, email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(28.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text("DAFTAR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // --- 4. SOCIAL LOGIN & FOOTER ---
                Row(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE6DBDB))
                    Text(
                        text = "Or register with",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4040),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE6DBDB))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SocialButtonCircle(text = "G")
                    Spacer(modifier = Modifier.width(16.dp))
                    SocialButtonCircle(icon = Icons.Default.LaptopMac)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account? ", color = Color(0xFF181111))
                    Text(
                        text = "Login",
                        color = Color(0xFF673AB7), // Merah Login
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun CustomRegisterInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    topMargin: androidx.compose.ui.unit.Dp = 0.dp
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = topMargin)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF616161), // Abu gelap
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(imageVector = icon, contentDescription = null, tint = Color.Gray) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun RowScope.StrengthBar(color: Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(4.dp)
            .padding(end = 4.dp)
            .background(color, RoundedCornerShape(2.dp))
    )
}

@Composable
fun SocialButtonCircle(text: String? = null, icon: ImageVector? = null) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color.LightGray, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        } else if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterScreen(onBackClick = {}, onLoginClick = {}, onRegisterClick = {_,_,_,_ ->})
}
