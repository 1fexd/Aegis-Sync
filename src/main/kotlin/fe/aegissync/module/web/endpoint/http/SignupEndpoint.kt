package fe.aegissync.module.web.endpoint.http

import fe.aegissync.crypto.Crypto
import fe.aegissync.module.database.entity.User
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.http.ApiRestEndpoint
import fe.aegissync.module.web.endpoint.base.Response
import fe.aegissync.module.web.endpoint.base.send
import fe.koin.exposed.ext.rawTransaction
import io.javalin.http.Context
import io.javalin.http.HandlerType
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


object SignupEndpoint : ApiRestEndpoint<SignupEndpoint.SignupRequest>(
    SignupRequest::class.java,
    "signup",
    roles = arrayOf(WebRole.Default),
    method = HandlerType.POST
), KoinComponent {
    private val database by inject<Database>()

    data class SignupRequest(
        val username: String,
        val masterPasswordHash: String,
        val protectedSymetricKey: String,
        val protectedSymetricKeyIv: String
    )

    @OptIn(ExperimentalEncodingApi::class)
    override fun handle(ctx: Context, body: SignupRequest) {
        val masterPasswordHash = String(Base64.decode(body.masterPasswordHash)).toCharArray()

        val salt = Crypto.generateRandom()
        val hashedMasterPasswordHash = Crypto.pbkd2(masterPasswordHash, salt, Crypto.PBKDF2Algorithm.SHA256)

        try {
            database.rawTransaction {
                User.new {
                    this.username = body.username
                    this.hashedMasterPasswordHash = hashedMasterPasswordHash
                    this.hashedMasterPasswordHashSalt = salt
                    this.protectedSymetricKey = Base64.decode(body.protectedSymetricKey)
                    this.protectedSymetricKeyIV = Base64.decode(body.protectedSymetricKeyIv)
                }
            }

            ctx.status(200)
        } catch (e: Exception) {
            if (e is ExposedSQLException && e.errorCode == 19) {
                ctx.send(Response.InvalidBody("Username taken"))
            } else {
                ctx.send(Response.InternalError.Default)
            }
        }
    }
}
