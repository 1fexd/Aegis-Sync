package fe.aegissync.test

import fe.aegissync.module.web.endpoint.ws.WsMessageListener
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi


class Client1Socket : WebSocketListener() {
    override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
        val message = Json.decodeFromString(WsMessageListener.WebSocketMessage.serializer(), text)
        println(message)
    }

    override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
        super.onOpen(webSocket, response)
    }

    override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }
}

fun main() {
    val token =
        "yvuryvcyGXhuDnfPr8PTdBSQChbIXRsSVW2xobdPhHRcDgS1ldGdQ7bayBQv1Ph8xJ9WneAPTU78vK7Sd5mtdiKEzgIdOa0ZbPmLlXn72iZGoe8s6oLfsXOhxYbX9DRg"
    val client = OkHttpClient.Builder().build()

    val webSocket = client.newWebSocket(
        Request.Builder().url("ws://localhost:23456/message").addHeader("Device-Token", token).build(),
        Client1Socket()
    )


    println("Sending request..")
    webSocket.send(
        Json.encodeToString(
            WsMessageListener.WebSocketMessage.serializer(),
            WsMessageListener.WebSocketMessage.RequestForDomain(UUID.randomUUID().toString(), "google.com")
        )
    )

    println("Request sent, idling..")
}
