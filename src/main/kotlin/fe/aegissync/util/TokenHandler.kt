package fe.aegissync.util

import java.security.SecureRandom

object TokenHandler {
    private val secureRandom = SecureRandom()
    private const val stdChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun getStandardCharsetString(length: Int): String {
        return buildString {
            repeat(length) {
                this.append(stdChars[secureRandom.nextInt(stdChars.length)])
            }
        }
    }
}
