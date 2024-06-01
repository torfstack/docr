package com.torfstack.docr.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class DocrCrypto {

    companion object {

        private val ENCRYPTION_HEADER = byteArrayOf(0x44, 0x4f, 0x43, 0x52)
        private val ENCRYPTION_VERSION = byteArrayOf(0x00, 0x00, 0x00, 0x01)

        private var key: Key? = null

        fun encrypt(data: ByteArray): ByteArray {
            createEncryptCipher(getKey()).run {
                val encrypted = doFinal(data)
                return ENCRYPTION_HEADER + ENCRYPTION_VERSION + iv + encrypted
            }
        }

        fun decrypt(data: ByteArray): ByteArray {
            val header = data.sliceArray(0 until 4)
            if (!header.contentEquals(ENCRYPTION_HEADER)) {
                throw IllegalArgumentException("Invalid encryption header")
            }
            val nonce = data.sliceArray(8 until 20)
            createDecryptCipher(getKey(), nonce).run {
                return doFinal(data.sliceArray(20 until data.size))
            }
        }

        private fun createEncryptCipher(key: Key): Cipher {
            return Cipher.getInstance(cipherTransform()).apply {
                init(Cipher.ENCRYPT_MODE, key)
            }
        }

        private fun createDecryptCipher(key: Key, nonce: ByteArray): Cipher {
            return Cipher.getInstance(cipherTransform()).apply {
                init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, nonce))
            }
        }

        private fun cipherTransform(): String {
            return "${KeyProperties.KEY_ALGORITHM_AES}/" +
                    "${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
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