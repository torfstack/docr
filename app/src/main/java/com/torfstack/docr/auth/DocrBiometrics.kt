package com.torfstack.docr.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.fragment.app.FragmentActivity

class DocrBiometrics {

    fun authenticate(
        fragmentActivity: FragmentActivity,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (!isBiometricAvailable(fragmentActivity)) return

        val prompt = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric authentication")
            .setSubtitle("Authenticate using biometrics")
            .setNegativeButtonText("Cancel")
            .build()

        BiometricPrompt(fragmentActivity, object : AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailure()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onFailure()
            }
        }).authenticate(prompt)
    }

    private fun isBiometricAvailable(context: Context): Boolean {
        val status = BiometricManager.from(context)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        return status == BiometricManager.BIOMETRIC_SUCCESS
    }
}