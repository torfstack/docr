package com.torfstack.docr.auth

import android.content.Context
import android.util.Base64
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.torfstack.docr.ui.theme.DocRTheme
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class DocrAuth {

    @Composable
    fun AuthenticateWithPassword(
        fragmentActivity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        var password by remember { mutableStateOf("") }
        return DocRTheme {
            AlertDialog(
                confirmButton = {
                    Button(
                        onClick = {
                            if (checkPassword(fragmentActivity, password)) {
                                onSuccess()
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                onDismissRequest = { onFailure() },
                title = { Text("Enter password") },
                text = {
                    OutlinedTextField(
                        isError = password.isEmpty(),
                        value = password,
                        onValueChange = {
                            password = it
                        }
                    )
                }
            )
        }
    }

    private fun checkPassword(context: Context, password: CharSequence): Boolean {
        val salt = Base64.decode(
            context.getSharedPreferences("docr", Context.MODE_PRIVATE)
                .getString("salt", "") ?: "",
            Base64.DEFAULT
        )
        val pbkfd2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val secret = pbkfd2.generateSecret(
            PBEKeySpec(
                password.toString().toCharArray(),
                salt,
                1000,
                128
            )
        )
        val storedSecret = Base64.decode(
            context.getSharedPreferences("docr", Context.MODE_PRIVATE)
                .getString("password", "") ?: "",
            Base64.DEFAULT
        )
        return secret.encoded.contentEquals(storedSecret)
    }

    @Composable
    fun SetPassword(fragmentActivity: FragmentActivity, onClosed: (Boolean) -> Unit) {
        var password by remember { mutableStateOf("") }
        return DocRTheme {
            AlertDialog(
                confirmButton = {
                    Button(
                        onClick = {
                            if (password.isEmpty()) {
                                // TODO: show error message
                                return@Button
                            }
                            val salt = ByteArray(16)
                            SecureRandom().nextBytes(salt)
                            val pbkfd2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                            val secret = pbkfd2.generateSecret(
                                PBEKeySpec(
                                    password.toCharArray(),
                                    salt,
                                    1000,
                                    128
                                )
                            )
                            fragmentActivity.getSharedPreferences("docr", Context.MODE_PRIVATE)
                                .edit()
                                .putString("salt", Base64.encodeToString(salt, Base64.DEFAULT))
                                .putString(
                                    "password",
                                    Base64.encodeToString(secret.encoded, Base64.DEFAULT)
                                )
                                .apply()
                            onClosed(true)
                        }
                    ) {
                        Text("OK")
                    }
                },
                onDismissRequest = { onClosed(false) },
                title = { Text("Enter password") },
                text = {
                    OutlinedTextField(
                        isError = password.isEmpty(),
                        value = password,
                        onValueChange = {
                            password = it
                        }
                    )
                }
            )
        }
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return context.getSharedPreferences("docr", Context.MODE_PRIVATE)
            .getBoolean("biometrics_enabled", false) &&
                isPasswordEnabled(context)
    }

    fun isPasswordEnabled(context: Context): Boolean {
        return context.getSharedPreferences("docr", Context.MODE_PRIVATE)
            .getBoolean("password_enabled", false)
    }

    fun setPasswordActivated(context: Context, activated: Boolean) {
        context.getSharedPreferences("docr", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("password_enabled", activated)
            .apply()
        if (!activated) {
            context.getSharedPreferences("docr", Context.MODE_PRIVATE)
                .edit()
                .remove("salt")
                .remove("password")
                .apply()
        }
    }

    fun setBiometricsActivated(context: Context, activated: Boolean) {
        context.getSharedPreferences("docr", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("biometrics_enabled", activated)
            .apply()
        if (activated) {
            setPasswordActivated(context, true)
        }
    }
}