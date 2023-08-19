package fe.aegissync.test

import fe.aegissync.crypto.Crypto
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun main() {
    val username = "1fexd"
    val mp = "thisIsNotASecureMasterPassword"

//     don't strecht, just derive 512 instead of 256
    // java AES doesn support 512 bit?
    val mk = Crypto.pbkd2(mp.toCharArray(), username.toByteArray(), Crypto.PBKDF2Algorithm.SHA256)
    val smk = mk

    // java AES doesn't support 512 bit?
    val sk = Crypto.generateAESKey(256)
    val pskIv = Crypto.generateRandom(Crypto.ivBitLength)
    val psk = Crypto.encryptAES(sk, smk, pskIv)

    val mph = Crypto.pbkd2(String(mk).toCharArray(), mp.toByteArray())
    println(Base64.encode(mk))
    println(Base64.encode(sk))
    println(Base64.encode(psk))
    println(Base64.encode(mph))

//    val testIv = Crypto.generateRandom(Crypto.ivBitLength)
//    val encrypted = Crypto.encryptAES("helloworld".toByteArray(), sk, testIv)
//    println(Base64.encode(encrypted))
//    println(String(Crypto.decryptAES(encrypted, sk, testIv)))

//    AES/CBC/PKCS5PADDING
//    val hkdf = HKDF.fromHmacSha256().expand(mk, )
}
