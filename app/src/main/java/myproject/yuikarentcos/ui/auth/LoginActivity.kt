package myproject.yuikarentcos.ui.auth // Sesuaikan package kamu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import myproject.yuikarentcos.databinding.ActivityLoginBinding // Pastikan nama ini sesuai nama XML kamu

class LoginActivity : AppCompatActivity() {

    // Variabel untuk menghubungkan XML ke Kotlin
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Sambungkan ke XML (ActivityLoginBinding otomatis dibuat dari activity_login.xml)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- SEKARANG XML KAMU SUDAH MUNCUL, SAATNYA KASIH LOGIKA ---

        // Logika Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            } else {
                // Disini logika login ke server/firebase nanti
                Toast.makeText(this, "Login Berhasil (Simulasi)", Toast.LENGTH_SHORT).show()
            }
        }

        // Logika Pindah ke Halaman Daftar (Register)
        binding.tvRegister.setOnClickListener {
            // Pastikan kamu punya RegisterActivity.class
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Logika Lupa Password
        binding.tvForgotPass.setOnClickListener {
            Toast.makeText(this, "Fitur lupa password...", Toast.LENGTH_SHORT).show()
        }
    }
}