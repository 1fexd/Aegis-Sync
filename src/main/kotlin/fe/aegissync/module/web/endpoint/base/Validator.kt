package fe.aegissync.module.web.endpoint.base

import io.javalin.http.Context
import io.javalin.websocket.WsContext


interface Validator<T> {
    fun validate(ctx: Context): T?
    fun validate(ctx: WsContext): T?
}

class StringPathParamValidator(private val param: String) : Validator<String> {
    override fun validate(ctx: Context) = ctx.pathParam(param)
    override fun validate(ctx: WsContext) = ctx.pathParam(param)
}

class TypedPathParamValidator<T>(val name: String, val type: (String) -> T?, private val failResponse: Response) :
    Validator<T> {

    private fun validate(value: String): T? {
        return type.invoke(value)
    }

    override fun validate(ctx: Context): T? {
        return validate(ctx.pathParam(name)).also {
            if (it == null) failResponse.send(ctx)
        }
    }

    override fun validate(ctx: WsContext): T? {
        return validate(ctx.pathParam(name)).also {
//            if (it == null) failResponse.send(ctx)
        }
    }
}
