package myproject.yuikarentcos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
// Hapus import yang tidak terpakai agar tidak memicu error
import myproject.yuikarentcos.ui.auth.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Langsung pindah ke Login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}