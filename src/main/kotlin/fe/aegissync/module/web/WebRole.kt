package fe.aegissync.module.web

import io.javalin.security.RouteRole

enum class WebRole : RouteRole {
    Default, Device
}
