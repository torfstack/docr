package com.torfstack.docr.util

import java.io.ByteArrayOutputStream
import java.io.InputStream

fun InputStream.toByteArray(): ByteArray {
    val buf = ByteArrayOutputStream()
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var n: Int
    while (this.read(buffer).also { n = it } != -1) {
        buf.write(buffer, 0, n)
    }
    return buf.toByteArray()
}