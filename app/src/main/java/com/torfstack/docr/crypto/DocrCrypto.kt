package com.torfstack.docr.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class DocrCrypto {

    companion object {

        private var key: Key? = null

        fun encrypt(data: ByteArray): ByteArray {
            val nonce = ByteArray(12)
            SecureRandom().nextBytes(nonce)
            createEncryptCipher(getKey()).run {
                val encrypted = doFinal(data)
                return iv + encrypted
            }
        }

        fun decrypt(data: ByteArray): ByteArray {
            val nonce = data.sliceArray(0 until 12)
            createDecryptCipher(getKey(), nonce).run {
                return doFinal(data.sliceArray(12 until data.size))
            }
        }

        private fun createEncryptCipher(key: Key): Cipher {
            return Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                        "${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
            ).apply {
                init(Cipher.ENCRYPT_MODE, key)
            }
        }

        private fun createDecryptCipher(key: Key, nonce: ByteArray): Cipher {
            return Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                        "${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
            ).apply {
                init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, nonce))
            }
        }

        private fun getKey(): Key {
            if (key != null) {
                return key!!
            }

            val ks = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
            val entry = ks.getEntry("docr_key", null)
            if (entry != null) {
                key = (entry as KeyStore.SecretKeyEntry).secretKey
                return key!!
            }

            key =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
                    init(
                        KeyGenParameterSpec.Builder(
                            "docr_key",
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        ).run {
                            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            build()
                        }
                    )
                }.generateKey()
            return key!!
        }
    }
}