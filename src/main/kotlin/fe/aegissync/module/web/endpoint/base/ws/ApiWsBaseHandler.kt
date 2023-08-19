package fe.aegissync.module.web.endpoint.base.ws

import io.javalin.apibuilder.ApiBuilder
import io.javalin.security.RouteRole
import io.javalin.websocket.*

abstract class ApiWsBaseHandler(
    private vararg val paths: String,
    val roles: Array<out RouteRole>,
) : WsConnectHandler, WsMessageHandler, WsBinaryMessageHandler, WsCloseHandler, WsErrorHandler {

    fun paths() = if (paths.isNotEmpty()) paths else arrayOf("")

    init {
        this.register()
    }

    private fun register() {
        this.paths().forEach { path ->
            val prefixedPath = ApiBuilder.prefixPath(path)
            ApiBuilder.staticInstance().ws(prefixedPath, { it.handleAll(this) }, *this.roles)
            println("[WS] %-9s %-30s %s".format("", prefixedPath, this.javaClass.simpleName))
        }
    }

    fun <T> WsConfig.handleAll(
        customHandler: T
    ) where T : WsConnectHandler, T : WsMessageHandler, T : WsBinaryMessageHandler, T : WsCloseHandler, T : WsErrorHandler {
        this.onConnect(customHandler)
        this.onMessage(customHandler)
        this.onBinaryMessage(customHandler)
        this.onClose(customHandler)
        this.onError(customHandler)
    }
}
