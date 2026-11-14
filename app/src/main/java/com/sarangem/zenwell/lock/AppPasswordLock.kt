package com.sarangem.zenwell.lock

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AppPasswordLock {
    private const val PREFS = "app_password_lock"
    private const val KEY_HASH = "hash"
    private const val KEY_SALT = "salt"
    private const val ITERATIONS = 60_000
    const val MIN_LENGTH = 4

    var isLocked by mutableStateOf(true)
        private set

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun hasPassword(context: Context): Boolean = prefs(context).contains(KEY_HASH)

    fun syncOnLaunch(context: Context) {
        if (!hasPassword(context)) isLocked = false
    }

    fun lockIfEnabled(context: Context) {
        if (hasPassword(context)) isLocked = true
    }

    fun verify(context: Context, password: String): Boolean {
        val p = prefs(context)
        val hash = p.getString(KEY_HASH, null) ?: return false
        val salt = p.getString(KEY_SALT, null) ?: return false
        val expected = Base64.decode(hash, Base64.NO_WRAP)
        val actual = derive(password, Base64.decode(salt, Base64.NO_WRAP))
        return MessageDigest.isEqual(expected, actual)
    }

    fun unlock(context: Context, password: String): Boolean {
        val ok = verify(context, password)
        if (ok) isLocked = false
        return ok
    }

    fun setPassword(context: Context, password: String) {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        prefs(context).edit()
            .putString(KEY_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(KEY_HASH, Base64.encodeToString(derive(password, salt), Base64.NO_WRAP))
            .commit()
    }

    fun removePassword(context: Context) {
        prefs(context).edit().remove(KEY_HASH).remove(KEY_SALT).commit()
        isLocked = false
    }

    private fun derive(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
