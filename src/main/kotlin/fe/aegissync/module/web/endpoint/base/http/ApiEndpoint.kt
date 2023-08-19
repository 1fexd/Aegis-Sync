package fe.aegissync.module.web.endpoint.base.http

import io.javalin.apibuilder.ApiBuilder
import io.javalin.http.Context
import io.javalin.http.HandlerType
import io.javalin.security.RouteRole

abstract class ApiEndpoint(
    private vararg val paths: String,
    val roles: Array<out RouteRole>,
    val method: HandlerType = HandlerType.GET
) {
    abstract fun handle(ctx: Context)
    fun paths() = if (paths.isNotEmpty()) paths else arrayOf("")

    init {
        this.register()
    }

    private fun register() {
        if (!this.method.isHttpMethod()) throw IllegalArgumentException("Method must be http method!")

        this.paths().forEach { path ->
            val prefixedPath = ApiBuilder.prefixPath(path)
            ApiBuilder.staticInstance().addHandler(this.method, prefixedPath, this::handle, *this.roles)
            println(
                "[Route] %-6s %-30s %s".format(this.method, prefixedPath, this.javaClass.simpleName)
            )
        }
    }
}
