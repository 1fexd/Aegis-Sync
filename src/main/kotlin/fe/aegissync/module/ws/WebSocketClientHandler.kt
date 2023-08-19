package fe.aegissync.module.ws

import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.database.entity.UserDevices
import fe.aegissync.module.database.entity.Users
import fe.aegissync.module.web.endpoint.ws.WsMessageListener
import fe.koin.exposed.ext.transactionOrNull
import fe.koin.helper.util.singleModule
import io.javalin.websocket.WsContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database

val webSocketClientHandlerModule = singleModule {
    WebSocketClientHandler(get())
}

class WebSocketClientHandler(val database: Database) {
    data class ConnectedClient(val token: String, val ctx: WsContext, val deviceType: DeviceType)

    private val connectedClients = mutableMapOf<Int, MutableMap<String, ConnectedClient>>()
    private val reverseContextMapping = mutableMapOf<String, Int>()

    fun connect(deviceToken: String, ctx: WsContext): Boolean {
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
        val userId = reverseContextMapping.remove(ctx.sessionId) ?: return false
        val clients = connectedClients.remove(userId) ?: return false
        if (clients.isEmpty()) connectedClients.remove(userId)

        return true
    }

    fun forwardMessage(ctx: WsContext, message: WsMessageListener.WebSocketMessage, type: DeviceType): Boolean {
        println("${ctx.sessionId} forwards message $message to $type ($reverseContextMapping, $connectedClients)")
        val userId = reverseContextMapping[ctx.sessionId] ?: return false
        val clients = connectedClients[userId] ?: return false
        println(clients)
        clients.filter { it.value.deviceType == type }.forEach { (_, client) ->
            val msg = Json.encodeToString(WsMessageListener.WebSocketMessage.serializer(), message)
            println("Sending $msg to ${client.ctx.sessionId} (${client.deviceType}, $type)")
            client.ctx.send(Json.encodeToString(WsMessageListener.WebSocketMessage.serializer(), message))
        }

        return true
    }
}
