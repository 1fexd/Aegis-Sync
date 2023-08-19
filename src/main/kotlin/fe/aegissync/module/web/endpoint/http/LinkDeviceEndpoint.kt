package fe.aegissync.module.web.endpoint.http

import fe.aegissync.crypto.Crypto
import fe.aegissync.module.database.entity.*
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.http.ApiRestEndpoint
import fe.aegissync.module.web.endpoint.base.Response
import fe.aegissync.module.web.endpoint.base.send
import fe.koin.exposed.ext.rawTransaction
import fe.koin.exposed.ext.transactionOrNull
import io.javalin.http.Context
import io.javalin.http.HandlerType
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


object LinkDeviceEndpoint : ApiRestEndpoint<LinkDeviceEndpoint.LinkDeviceRequest>(
    LinkDeviceRequest::class.java,
    "link",
    roles = arrayOf(WebRole.Default),
    method = HandlerType.POST
), KoinComponent {
    private val logger = KotlinLogging.logger(LinkDeviceEndpoint::class.simpleName!!)
    private val database by inject<Database>()

    data class LinkDeviceRequest(
        val username: String,
        val masterPasswordHash: String,
        val type: DeviceType,
    )

    data class LinkDeviceResponse(
        val token: String?,
    )

    @OptIn(ExperimentalEncodingApi::class)
    override fun handle(ctx: Context, body: LinkDeviceRequest) {
        val user = database.transactionOrNull { User.find { Users.username eq body.username }.firstOrNull() } ?: run {
            logger.error("No user with username ${body.username} found")
            ctx.send(Response.InvalidAuthentication())
            return
        }

        val masterPasswordHash = String(Base64.decode(body.masterPasswordHash)).toCharArray()
        val hashedMasterPasswordHash = Crypto.pbkd2(masterPasswordHash, user.hashedMasterPasswordHashSalt)

        if (!hashedMasterPasswordHash.contentEquals(user.hashedMasterPasswordHash)) {
            logger.error("Invalid password for user ${body.username}")
            ctx.send(Response.InvalidAuthentication.Default)
            return
        }

        try {
            val device = database.rawTransaction {
                UserDevice.new {
                    this.deviceType = body.type
                    this.user = user.id
                }
            }

            ctx.json(LinkDeviceResponse(device.token))
        } catch (e: Exception) {
            logger.error(e) {
                "Something went wrong while creating new device for user ${body.username}"
            }
            ctx.send(Response.InternalError.Default)
        }
    }
}
