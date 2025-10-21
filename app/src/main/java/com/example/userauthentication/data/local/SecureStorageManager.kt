package com.example.userauthentication.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.example.userauthentication.data.model.User
import androidx.core.content.edit
import androidx.security.crypto.MasterKey


class SecureStorageManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_user_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUser(email: String, password: String) {
        encryptedPrefs.edit().apply {
            putString("user_email", email)
            putString("user_password", password)
            apply()
        }
    }

    fun getUser(): User? {
        val email = encryptedPrefs.getString("user_email", null)
        val password = encryptedPrefs.getString("user_password", null)

        return if (email != null && password != null) {
            User(email, password)
        } else null
    }

    fun userExists(email: String): Boolean {
        val storedEmail = encryptedPrefs.getString("user_email", null)
        return storedEmail == email
    }

    fun clearUser() {
        encryptedPrefs.edit { clear() }
    }
}
