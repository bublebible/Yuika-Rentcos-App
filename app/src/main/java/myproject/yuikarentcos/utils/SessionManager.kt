package com.yuikarentcos.app.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("yuika_session", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    // Simpan data login
    fun saveSession(token: String, role: String, name: String) {
        editor.putString("token", token)
        editor.putString("role", role)
        editor.putString("name", name)
        editor.putBoolean("is_login", true)
        editor.apply()
    }

    // Cek status login
    fun isLogin(): Boolean {
        return prefs.getBoolean("is_login", false)
    }

    // Ambil Token (buat request API nanti)
    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    // Hapus sesi (Logout)
    fun logout() {
        editor.clear()
        editor.apply()
    }
}