package fe.aegissync.module.web.endpoint.base.http


import fe.aegissync.module.web.endpoint.base.Validator
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.security.RouteRole

abstract class ApiPathParam1Endpoint<X>(
    vararg paths: String,
    roles: Array<out RouteRole>,
    private val xValidator: Validator<X>,
    method: HandlerType = HandlerType.GET
) : ApiEndpoint(*paths, roles = roles, method = method) {
    override fun handle(ctx: Context) {
        xValidator.validate(ctx)?.let {
            this.handle(ctx, it)
        }
    }

    abstract fun handle(ctx: Context, x: X)
}

abstract class ApiPathParam2Endpoint<X, Y>(
    vararg paths: String,
    roles: Array<out RouteRole>,
    private val xValidator: Validator<X>,
    private val yValidator: Validator<Y>,
    method: HandlerType = HandlerType.GET
) : ApiEndpoint(*paths, roles = roles, method = method) {
    override fun handle(ctx: Context) {
        val xVal = xValidator.validate(ctx)
        val yVal = yValidator.validate(ctx)

        if (xVal != null && yVal != null) {
            this.handle(ctx, xVal, yVal)
        }
    }

    abstract fun handle(ctx: Context, x: X, y: Y)
}
