package fe.aegissync.module.web.endpoint.http

import fe.aegissync.module.database.entity.User
import fe.aegissync.module.database.entity.Users
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.http.ApiPathParam1Endpoint
import fe.aegissync.module.web.endpoint.base.StringPathParamValidator
import fe.koin.exposed.ext.transactionOrNull
import io.javalin.http.Context
import org.jetbrains.exposed.sql.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object CheckUsernameAvailableEndpoint : ApiPathParam1Endpoint<String>(
    "available/{username}",
    roles = arrayOf(WebRole.Default),
    xValidator = StringPathParamValidator("username")
), KoinComponent {
    private val database by inject<Database>()

    data class UsernameAvailableResponse(val available: Boolean)

    override fun handle(ctx: Context, x: String) {
        val hasUsername = database.transactionOrNull { User.find { Users.username eq x }.firstOrNull() } == null
        ctx.json(UsernameAvailableResponse(hasUsername))
    }
}
