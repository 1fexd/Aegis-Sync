package fe.aegissync.test

import fe.aegissync.crypto.Crypto
import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.web.endpoint.http.LinkDeviceEndpoint
import fe.aegissync.module.web.endpoint.http.SignupEndpoint
import fe.aegissync.module.web.endpoint.http.UploadBackupEndpoint
import io.javalin.http.Context
import io.javalin.http.UploadedFile
import java.io.File
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun main() {
    val username = "1fexd"
    val mp = "thisIsNotASecureMasterPassword"

    val mk = Crypto.pbkd2(mp.toCharArray(), username.toByteArray(), Crypto.PBKDF2Algorithm.SHA256)
//    val smk = HKDF.fromHmacSha256().expand(mk, null, 512)
    val smk = mk

    val sk = Crypto.generateAESKey(256)
    val pskIv = Crypto.generateRandom(Crypto.ivBitLength)
    val psk = Crypto.encryptAES(sk, smk, pskIv)

    val mph = Crypto.pbkd2(String(mk).toCharArray(), mp.toByteArray())

    startTestKoin(database = "database.sqlite", keepDatabase = true)

    signup(username, mph, psk, pskIv)
    val browserExtension = linkDevice(username, mph, DeviceType.BrowserExtension)
    val app = linkDevice(username, mph, DeviceType.App)

}

fun uploadFile(token: String) {
    val inputFile = File("input.json")
    val uploadPart = object : MockPart() {
        override fun getInputStream(): InputStream {
            return inputFile.inputStream()
        }

        override fun getContentType(): String {
            return "application/json"
        }

        override fun getName(): String {
            return inputFile.name
        }

        override fun getSubmittedFileName(): String {
            return inputFile.name
        }

        override fun getSize(): Long {
            return inputFile.length()
        }
    }

    val uploadedFile = UploadedFile(uploadPart)

    UploadBackupEndpoint.handle(object : MockContext() {
        override fun header(header: String): String {
            return token
        }

        override fun json(obj: Any): Context {
            return this
        }

        override fun status(status: Int): Context {
            return this
        }

        override fun uploadedFile(fileName: String): UploadedFile {
            return uploadedFile
        }
    })
}

@OptIn(ExperimentalEncodingApi::class)
fun signup(username: String, mph: ByteArray, psk: ByteArray, pskIv: ByteArray) {
    SignupEndpoint.handle(
        object : MockContext() {
            override fun status(status: Int): Context {
                return this
            }

            override fun json(obj: Any): Context {
                return this
            }
        },
        SignupEndpoint.SignupRequest(username, Base64.encode(mph), Base64.encode(psk), Base64.encode(pskIv))
    )
}

@OptIn(ExperimentalEncodingApi::class)
fun linkDevice(username: String, mph: ByteArray, deviceType: DeviceType): String {
    var token: String? = null
    LinkDeviceEndpoint.handle(object : MockContext() {
        override fun status(status: Int): Context {
            return this
        }

        override fun json(obj: Any): Context {
            token = (obj as LinkDeviceEndpoint.LinkDeviceResponse).token
            return this
        }
    }, LinkDeviceEndpoint.LinkDeviceRequest(username, Base64.encode(mph), deviceType))

    return token!!
}
