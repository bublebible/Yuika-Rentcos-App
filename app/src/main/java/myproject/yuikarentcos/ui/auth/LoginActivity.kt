package myproject.yuikarentcos.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke // Mengatasi Unresolved reference 'BorderStroke'
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import myproject.yuikarentcos.MainActivity
import myproject.yuikarentcos.R
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart
import myproject.yuikarentcos.ui.TextDark
import myproject.yuikarentcos.ui.TextGray
import myproject.yuikarentcos.ui.admin.DashboardAdminActivity

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Gagal Login Google: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            cekRoleDanPindah(auth.currentUser!!.uid)
        }

        setContent {
            LoginScreen(
                onLoginClick = { email, pass -> loginKeFirebase(email, pass) },
                onRegisterClick = { startActivity(Intent(this, RegisterActivity::class.java)) },
                onGoogleClick = { prosesGoogleLogin() },
                onForgotPassClick = { Toast.makeText(this, "Fitur reset password belum aktif", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    private fun loginKeFirebase(email: String, sandi: String) {
        if (email.isEmpty() || sandi.isEmpty()) {
            Toast.makeText(this, "Email dan Password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Sedang masuk...", Toast.LENGTH_SHORT).show()
        auth.signInWithEmailAndPassword(email, sandi)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) cekRoleDanPindah(uid)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal Masuk: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun prosesGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Toast.makeText(this, "Memproses Google...", Toast.LENGTH_SHORT).show()
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user
                val uid = user?.uid
                if (uid != null) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                cekRoleDanPindah(uid)
                            } else {
                                val userData = hashMapOf(
                                    "uid" to uid,
                                    "fullName" to (user.displayName ?: "User Google"),
                                    "username" to (user.email?.split("@")?.get(0) ?: "user"),
                                    "email" to (user.email ?: ""),
                                    "role" to "user",
                                    "createdAt" to System.currentTimeMillis()
                                )
                                db.collection("users").document(uid).set(userData)
                                    .addOnSuccessListener { cekRoleDanPindah(uid) }
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Auth Google", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cekRoleDanPindah(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        Toast.makeText(this, "Selamat Datang Admin!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, DashboardAdminActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show()
            }
    }
}


@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onForgotPassClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRemembered by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            PurpleSoftBgStart,
            Color(0xFFE1BEE7),
            PurpleSoftBgEnd
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    Box(modifier = Modifier.fillMaxSize().background(brush = backgroundBrush)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PurplePrimary),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.size(96.dp).rotate(3f)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Checkroom, "Logo", tint = Color.White, modifier = Modifier.size(56.dp).rotate(-3f))
                }
            }
            Text("Welcome Back", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(top = 24.dp))
            Text("Login to your Yuika RentCos account", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp, bottom = 40.dp))

            CustomLoginInput(email, { email = it }, "Email Address", Icons.Default.Email, keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(20.dp))
            CustomLoginInput(password, { password = it }, "Password", Icons.Default.Lock, isPassword = true)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = isRemembered, onCheckedChange = { isRemembered = it }, colors = CheckboxDefaults.colors(checkedColor = PurplePrimary))
                Text("Remember me", fontSize = 12.sp, color = TextGray, modifier = Modifier.weight(1f))
                Text("Forgot Password?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PurplePrimary, modifier = Modifier.clickable { onForgotPassClick() })
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                Text("Or continue with", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF), modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialLoginButton("Google", onGoogleClick, Modifier.weight(1f)) { Text("G", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                SocialLoginButton("Facebook", {}, Modifier.weight(1f)) { Icon(Icons.Default.Facebook, null, tint = Color(0xFF1877F2)) }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", fontSize = 14.sp, color = TextGray)
                Text("Daftar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PurplePrimary, modifier = Modifier.clickable { onRegisterClick() })
            }

            Box(modifier = Modifier.padding(top = 32.dp).width(120.dp).height(5.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE5E7EB).copy(alpha = 0.8f)))
        }
    }
}

@Composable
fun CustomLoginInput(value: String, onValueChange: (String) -> Unit, hint: String, icon: ImageVector, isPassword: Boolean = false, keyboardType: KeyboardType = KeyboardType.Text) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onValueChange, placeholder = { Text(hint, color = Color(0xFF9CA3AF)) },
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, null, tint = Color(0xFF9CA3AF)) },
        trailingIcon = if (isPassword) { { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle", tint = Color(0xFF9CA3AF)) } } } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = PurplePrimary, unfocusedBorderColor = Color.Transparent, cursorColor = PurplePrimary)
    )
}

@Composable
fun SocialLoginButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, iconContent: @Composable () -> Unit) {
    OutlinedButton(
        onClick = onClick, modifier = modifier.height(56.dp), shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)) // Import BorderStroke sudah ditambahkan di atas
    ) {
        iconContent()
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color(0xFF374151))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    LoginScreen({_,_ ->}, {}, {}, {})
}