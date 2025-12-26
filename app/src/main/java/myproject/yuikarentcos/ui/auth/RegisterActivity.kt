package myproject.yuikarentcos.ui.auth // Sesuaikan package kamu

import android.os.Bundle
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Import baru untuk ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

@Composable
fun RegisterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6F6)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection()
            Spacer(modifier = Modifier.height(24.dp))
            LogoSection()
            Spacer(modifier = Modifier.height(24.dp))
            FormSection()
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xCCF8F6F6))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle Back */ }) {
            // PERBAIKAN: Menggunakan Icons.AutoMirrored.Filled.ArrowBack
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF181111)
            )
        }
        Text(
            text = "Yuika RentCos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF181111),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(48.dp))
    }
}

@Composable
fun LogoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            // PERBAIKAN: Checkroom akan aktif setelah library extended ditambahkan
            Icon(
                imageVector = Icons.Default.Checkroom,
                contentDescription = "Cloth Icon",
                tint = Color(0xFFEE2B2B),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Join the Fun!",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF181111)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create an account to start renting your dream cosplay.",
            fontSize = 16.sp,
            color = Color(0xFF4E342E),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun FormSection() {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        RegisterInput(
            label = "Full Name",
            value = fullName,
            onValueChange = { fullName = it },
            icon = Icons.Default.Badge
        )
        RegisterInput(
            label = "Username",
            value = username,
            onValueChange = { username = it },
            icon = Icons.Default.AlternateEmail,
            topMargin = 16.dp
        )
        RegisterInput(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            topMargin = 16.dp
        )
        RegisterInput(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            icon = Icons.Default.Lock,
            isPassword = true,
            topMargin = 16.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // PERBAIKAN: Mengganti nama parameter weight jadi fraction agar tidak error
            PasswordStrengthBar(color = Color(0xFF4CAF50), fraction = 1f)
            Spacer(modifier = Modifier.width(4.dp))
            PasswordStrengthBar(color = Color(0xFF4CAF50), fraction = 1f)
            Spacer(modifier = Modifier.width(4.dp))
            PasswordStrengthBar(color = Color(0xFFE0E0E0), fraction = 1f)
            Spacer(modifier = Modifier.width(4.dp))
            PasswordStrengthBar(color = Color(0xFFE0E0E0), fraction = 1f)
        }

        RegisterInput(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            icon = Icons.Default.LockReset,
            isPassword = true,
            topMargin = 16.dp
        )

        Button(
            onClick = { /* Handle Register */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE2B2B)),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(text = "Daftar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SocialButton(text = "G")
            Spacer(modifier = Modifier.width(16.dp))
            SocialButton(icon = Icons.Default.LaptopMac)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ", color = Color(0xFF181111))
            Text(
                text = "Login",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun RegisterInput(
    label: String, value: String, onValueChange: (String) -> Unit,
    icon: ImageVector, isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text, topMargin: androidx.compose.ui.unit.Dp = 0.dp
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.padding(top = topMargin)) {
        Text(text = label, fontSize = 14.sp, color = Color(0xFF616161), modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            // Visibility perlu library extended agar tidak merah
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle"
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFEE2B2B), unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

// PERBAIKAN: Menggunakan RowScope agar modifier .weight() dikenali
@Composable
fun RowScope.PasswordStrengthBar(color: Color, fraction: Float) {
    Box(
        modifier = Modifier
            .weight(fraction) // Sekarang modifier.weight() pasti jalan
            .height(4.dp)
            .background(color, RoundedCornerShape(2.dp))
    )
}

@Composable
fun SocialButton(text: String? = null, icon: ImageVector? = null) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(1.dp, Color.LightGray, CircleShape)
            .clip(CircleShape)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        else if (icon != null) Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    RegisterScreen()
}