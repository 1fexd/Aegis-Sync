package fe.aegissync.test

import fe.aegissync.crypto.Crypto
import fe.aegissync.module.database.entity.DeviceType
import fe.aegissync.module.web.endpoint.ws.WsMessageListener
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.ExperimentalEncodingApi


class Client2Socket : WebSocketListener() {

    override fun onMessage(webSocket: okhttp3.WebSocket, bytes: okio.ByteString) {

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val message =
            Json.decodeFromString(WsMessageListener.WebSocketMessage.serializer(), text)
        println(message)
    }

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        println("Openend!")
    }

    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun main() {
    val token =
        "fMd1PukLWXbno4R2WuSE5IhCSjxnPMtRjm9C8Hu7W2DD0Kwbz8bXqK2h1DVdMeJFne1JINFN4lsQNpamPdIZqOrNhOQuYQHVzWov3O38hiZ6PnqYedgeHsRyMstnBLx5"

    val client = OkHttpClient.Builder().build()
    val websocket = client.newWebSocket(
        Request.Builder().url("ws://localhost:23456/message").addHeader("Device-Token", token).build(),
        Client2Socket()
    )

//    websocket
}
