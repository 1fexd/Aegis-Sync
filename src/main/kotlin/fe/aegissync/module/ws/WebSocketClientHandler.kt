package fe.aegissync.module.ws

import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.database.entity.UserDevices
import fe.aegissync.module.database.entity.Users
import fe.aegissync.module.web.endpoint.ws.WsMessageListener
import fe.koin.exposed.ext.transactionOrNull
import fe.koin.helper.util.singleModule
import io.javalin.websocket.WsContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database

val webSocketClientHandlerModule = singleModule {
    WebSocketClientHandler(get())
}

class WebSocketClientHandler(val database: Database) {
    private val logger = KotlinLogging.logger(WebSocketClientHandler::class.simpleName!!)

    data class ConnectedClient(val token: String, val ctx: WsContext, val deviceType: DeviceType)

    private val connectedClients = mutableMapOf<Int, MutableMap<String, ConnectedClient>>()
    private val reverseContextMapping = mutableMapOf<String, Int>()

    fun connect(deviceToken: String, ctx: WsContext): Boolean {
        logger.info("Client ${ctx.sessionId} is connecting")

        val row = database.transactionOrNull {
            UserDevices.getDeviceAndUser(deviceToken)
        } ?: return false

        val userId = row[Users.id].value
        val devices = connectedClients.getOrPut(userId) { mutableMapOf() }
        if (devices[deviceToken] != null) return false

        devices[ctx.sessionId] = ConnectedClient(deviceToken, ctx, row[UserDevices.deviceType])
        reverseContextMapping[ctx.sessionId] = userId
        return true
    }

    fun disconnect(ctx: WsContext): Boolean {
        logger.info("Client ${ctx.sessionId} is disconnecting")
        val userId = reverseContextMapping.remove(ctx.sessionId) ?: return false
        for ((_, clients) in connectedClients) {
            clients.remove(ctx.sessionId)
        }

        if (connectedClients[userId]?.isEmpty() == true) {
            connectedClients.remove(userId)
        }


        return true
    }

    fun forwardMessage(ctx: WsContext, message: WsMessageListener.WebSocketMessage, type: DeviceType): Boolean {
        logger.info("${ctx.sessionId} wants to forward message $message to $type ($reverseContextMapping, $connectedClients)")
        val userId = reverseContextMapping[ctx.sessionId] ?: return false
        val clients = connectedClients[userId] ?: return false

        logger.info("$clients")

        val msg = Json.encodeToString(WsMessageListener.WebSocketMessage.serializer(), message)
        clients.filter { it.value.deviceType == type }.forEach { (_, client) ->
            logger.info("Sending $msg to ${client.ctx.sessionId} (${client.deviceType})")
            client.ctx.send(msg)
        }

        return true
    }
}
