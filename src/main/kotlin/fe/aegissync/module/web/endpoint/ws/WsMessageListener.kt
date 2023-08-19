package fe.aegissync.module.web.endpoint.ws

import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.web.WebAccessManager
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.ws.ApiWsBaseHandler
import fe.aegissync.module.ws.WebSocketClientHandler
import io.javalin.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object WsMessageListener : ApiWsBaseHandler(
    "message",
    roles = arrayOf(WebRole.Device),
), KoinComponent {
    private val webSocketClientHandler by inject<WebSocketClientHandler>()

    @Serializable
    sealed interface WebSocketMessage {
        @Serializable
        data class RequestForDomain(val domain: String) : WebSocketMessage

        @Serializable
        data class CurrentTokenForDomain(val domain: String, val twoFactorToken: String) : WebSocketMessage
    }

    override fun handleConnect(ctx: WsConnectContext) {
        val deviceToken = ctx.header(WebAccessManager.deviceTokenHeader)!!
        webSocketClientHandler.connect(deviceToken, ctx)
    }

    override fun handleMessage(ctx: WsMessageContext) {
        val message = Json.decodeFromString(WebSocketMessage.serializer(), ctx.message())
        val type = when (message) {
            is WebSocketMessage.CurrentTokenForDomain -> DeviceType.BrowserExtension
            is WebSocketMessage.RequestForDomain -> DeviceType.App
        }

        webSocketClientHandler.forwardMessage(ctx, message, type)
    }

    override fun handleBinaryMessage(ctx: WsBinaryMessageContext) {
    }

    override fun handleClose(ctx: WsCloseContext) {
        webSocketClientHandler.disconnect(ctx)
    }

    override fun handleError(ctx: WsErrorContext) {
        webSocketClientHandler.disconnect(ctx)
    }
}
