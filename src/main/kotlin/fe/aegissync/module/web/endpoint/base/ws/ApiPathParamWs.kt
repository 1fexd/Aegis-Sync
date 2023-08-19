package fe.aegissync.module.web.endpoint.base.ws

import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.Validator
import io.javalin.websocket.*

abstract class ApiPathParam1Endpoint<X>(
    vararg paths: String,
    roles: Array<WebRole>,
    private val xValidator: Validator<X>,
) : ApiWsBaseHandler(*paths, roles = roles) {
    override fun handleConnect(ctx: WsConnectContext) {
        xValidator.validate(ctx)?.let {
            this.handleConnect(ctx, it)
        }
    }

    abstract fun handleConnect(ctx: WsConnectContext, x: X)

    override fun handleMessage(ctx: WsMessageContext) {
        xValidator.validate(ctx)?.let {
            this.handleMessage(ctx, it)
        }
    }

    abstract fun handleMessage(ctx: WsMessageContext, x: X)

    override fun handleBinaryMessage(ctx: WsBinaryMessageContext) {
        xValidator.validate(ctx)?.let {
            this.handleBinaryMessage(ctx, it)
        }
    }

    abstract fun handleBinaryMessage(ctx: WsBinaryMessageContext, x: X)

    override fun handleClose(ctx: WsCloseContext) {
        xValidator.validate(ctx)?.let {
            this.handleClose(ctx, it)
        }
    }

    abstract fun handleClose(ctx: WsCloseContext, x: X)

    override fun handleError(ctx: WsErrorContext) {
        xValidator.validate(ctx)?.let {
            this.handleError(ctx, it)
        }
    }

    abstract fun handleError(ctx: WsErrorContext, x: X)
}

abstract class ApiPathParam2Endpoint<X, Y>(
    vararg paths: String,
    roles: Array<WebRole>,
    private val xValidator: Validator<X>,
    private val yValidator: Validator<Y>,
) : ApiWsBaseHandler(*paths, roles = roles) {

    private fun handle(ws: WsContext, fn: (X, Y) -> Unit) {
        val xVal = xValidator.validate(ws)
        val yVal = yValidator.validate(ws)

        if (xVal != null && yVal != null) {
            fn.invoke(xVal, yVal)
        }
    }

    override fun handleConnect(ctx: WsConnectContext) {
        handle(ctx) { x, y -> handleConnect(ctx, x, y) }
    }

    abstract fun handleConnect(ctx: WsConnectContext, x: X, y: Y)

    override fun handleMessage(ctx: WsMessageContext) {
        handle(ctx) { x, y -> handleMessage(ctx, x, y) }
    }

    abstract fun handleMessage(ctx: WsMessageContext, x: X, y: Y)

    override fun handleBinaryMessage(ctx: WsBinaryMessageContext) {
        handle(ctx) { x, y -> handleBinaryMessage(ctx, x, y) }
    }

    abstract fun handleBinaryMessage(ctx: WsBinaryMessageContext, x: X, y: Y)

    override fun handleClose(ctx: WsCloseContext) {
        handle(ctx) { x, y -> handleClose(ctx, x, y) }
    }

    abstract fun handleClose(ctx: WsCloseContext, x: X, y: Y)

    override fun handleError(ctx: WsErrorContext) {
        handle(ctx) { x, y -> handleError(ctx, x, y) }
    }

    abstract fun handleError(ctx: WsErrorContext, x: X, y: Y)
}
