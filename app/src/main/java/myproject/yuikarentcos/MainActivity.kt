package myproject.yuikarentcos

import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // <--- Import Penting buat gambar sendiri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import myproject.yuikarentcos.ui.admin.DashboardAdminActivity
import myproject.yuikarentcos.ui.PurplePrimary
import myproject.yuikarentcos.ui.PurpleSoftBgEnd
import myproject.yuikarentcos.ui.PurpleSoftBgStart

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val scale = remember { Animatable(0f) }

    // LOGIKA PINDAH HALAMAN
    LaunchedEffect(key1 = true) {
        // 1. Animasi Logo (Membesar)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(4f).getInterpolation(it) }
            )
        )

        // 2. Tunggu 2.5 Detik
        delay(2500L)

        // 3. Pindah ke Dashboard
        val intent = Intent(context, DashboardAdminActivity::class.java)
        context.startActivity(intent)

        // 4. Tutup Activity ini
        (context as? ComponentActivity)?.finish()
    }

    // TAMPILAN UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PurpleSoftBgStart, Color(0xFFE1BEE7), PurpleSoftBgEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO CUSTOM ---
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(4.dp, PurplePrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    // Panggil gambar dari Drawable
                    painter = painterResource(id = R.drawable.ic_logo_yuika_rentcos),
                    contentDescription = "Logo Yuika Rentcos",

                    // PENTING: Pakai Unspecified biar warna asli gambar muncul (gak jadi ungu doang)
                    tint = Color.Unspecified,

                    modifier = Modifier.size(80.dp) // Atur ukuran logo di dalam lingkaran
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TEKS JUDUL
            Text(
                text = "Yuika Rentcos",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4A148C),
                modifier = Modifier.scale(scale.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // LOADING
            CircularProgressIndicator(
                color = PurplePrimary,
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
        }

        // Copyright
        Text(
            text = "v1.0.0 Beta",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}