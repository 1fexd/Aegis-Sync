package fe.aegissync.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


object Crypto {
    val masterPasswordHashBitLength = 256
    val saltBitLength = 32
    val ivBitLength = 16
    val protectedSymetricKeyBitLength = 512
    val pbkdf2IterationRounds = 600_000

    val secureRandom = SecureRandom.getInstanceStrong()

    fun generateRandom(length: Int = saltBitLength): ByteArray {
        return ByteArray(length).apply {
            secureRandom.nextBytes(this)
        }
    }

    enum class PBKDF2Algorithm(val length: Int) {
        SHA256(256), SHA512(512);
    }

    fun pbkd2(input: CharArray, salt: ByteArray, algorithm: PBKDF2Algorithm = PBKDF2Algorithm.SHA256): ByteArray {
        val spec = PBEKeySpec(input, salt, pbkdf2IterationRounds, algorithm.length)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmac$algorithm")

        return skf.generateSecret(spec).encoded
    }

    fun generateAESKey(length: Int = 256): ByteArray {
        return KeyGenerator.getInstance("AES").apply { init(length, secureRandom) }.generateKey().encoded
    }

    fun encryptAES(input: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        // PKCS5 == PKCS7
        return Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        }.doFinal(input)
    }

    fun decryptAES(input: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        // PKCS5 == PKCS7
        return Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        }.doFinal(input)
    }
}
