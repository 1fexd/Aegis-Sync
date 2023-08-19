package fe.aegissync.module.web.endpoint.base.http


import fe.aegissync.module.web.endpoint.base.Response
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.security.RouteRole
import io.javalin.validation.BodyValidator

abstract class ApiRestEndpoint<T>(
    private val clazz: Class<out T>,
    vararg path: String,
    roles: Array<out RouteRole>,
    method: HandlerType
) : ApiEndpoint(*path, roles = roles, method = method) {

    override fun handle(ctx: Context) {
        val body = ctx.bodyValidator(clazz).getOrNull() ?: run {
            Response.InvalidBody().send(ctx)
            return
        }

        this.handle(ctx, body)
    }

    abstract fun handle(ctx: Context, body: T)
}

fun <T> BodyValidator<T>.getOrNull(): T? {
    return runCatching { this.get() }.getOrNull()
}
