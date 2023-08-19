package fe.aegissync.module.web.endpoint.ws

import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.web.WebAccessManager
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.ws.ApiWsBaseHandler
import fe.aegissync.module.ws.WebSocketClientHandler
import io.javalin.websocket.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

object WsMessageListener : ApiWsBaseHandler(
    "message",
    roles = arrayOf(WebRole.Device),
), KoinComponent {
    private val logger = KotlinLogging.logger(WsMessageListener::class.simpleName!!)
    private val webSocketClientHandler by inject<WebSocketClientHandler>()

    @Serializable
    sealed class WebSocketMessage {
        @Serializable
        @SerialName("RequestForDomain")
        data class RequestForDomain(val id: String, val domain: String) : WebSocketMessage()

        @Serializable
        @SerialName("CurrentToken")
        data class CurrentToken(
            val id: String,
            val twoFactorToken: String
        ) : WebSocketMessage()

        @Serializable
        @SerialName("DenySharing")
        data class DenySharing(val id: String) : WebSocketMessage()
    }

    override fun handleConnect(ctx: WsConnectContext) {
        ctx.enableAutomaticPings(30, TimeUnit.SECONDS)

        val deviceToken = ctx.header(WebAccessManager.deviceTokenHeader)!!
        logger.info("Connect: Client ${ctx.sessionId} with device token $deviceToken")
        webSocketClientHandler.connect(deviceToken, ctx)
    }

    override fun handleMessage(ctx: WsMessageContext) {
        val message = Json.decodeFromString(WebSocketMessage.serializer(), ctx.message())
        val type = when (message) {
            is WebSocketMessage.CurrentToken -> DeviceType.BrowserExtension
            is WebSocketMessage.DenySharing -> DeviceType.BrowserExtension
            is WebSocketMessage.RequestForDomain -> DeviceType.App
        }

        webSocketClientHandler.forwardMessage(ctx, message, type)
    }

    override fun handleBinaryMessage(ctx: WsBinaryMessageContext) {
    }

    override fun handleClose(ctx: WsCloseContext) {
        logger.info("Close: Client ${ctx.sessionId}: ${ctx.reason()}")
        webSocketClientHandler.disconnect(ctx)
    }

    override fun handleError(ctx: WsErrorContext) {
        logger.error("Error: Client ${ctx.sessionId}")
        webSocketClientHandler.disconnect(ctx)
    }
}
