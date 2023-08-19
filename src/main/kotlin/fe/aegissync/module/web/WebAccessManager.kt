package fe.aegissync.module.web

import fe.aegissync.module.web.endpoint.base.Response
import fe.aegissync.module.web.endpoint.base.send
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.security.AccessManager
import io.javalin.security.RouteRole

object WebAccessManager : AccessManager {
    const val deviceTokenHeader = "Device-Token"

    @Throws(Exception::class)
    override fun manage(handler: Handler, ctx: Context, routeRoles: Set<RouteRole>) {
        if (routeRoles.contains(WebRole.Default)) {
            handler.handle(ctx)
            return
        }

        val deviceToken = ctx.header(deviceTokenHeader)
        if (routeRoles.contains(WebRole.Device) && deviceToken != null) {
            handler.handle(ctx)
            return
        }

        ctx.send(Response.InvalidAuthentication.Default)
    }

}
