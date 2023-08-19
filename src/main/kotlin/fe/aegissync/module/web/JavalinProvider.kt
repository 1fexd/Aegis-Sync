package fe.aegissync.module.web

import fe.aegissync.module.web.endpoint.http.CheckUsernameAvailableEndpoint
import fe.aegissync.module.web.endpoint.http.LinkDeviceEndpoint
import fe.aegissync.module.web.endpoint.http.SignupEndpoint
import fe.aegissync.module.web.endpoint.http.UploadBackupEndpoint
import fe.aegissync.module.web.endpoint.ws.WsMessageListener
import fe.aegissync.module.web.endpoint.ws.WsMessageListener.handleAll
import fe.koin.helper.provider.KoinProvider01
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.path
import mu.KotlinLogging
import java.time.LocalDateTime

object JavalinProvider : KoinProvider01<Javalin> {
    private val logger = KotlinLogging.logger("Web server")

    override fun provide(): Javalin {
        val app = Javalin.create { cfg ->
            cfg.showJavalinBanner = false
            cfg.requestLogger.http { ctx, executionTimeMs ->
                logger.info(
                    "%s: %s %s -> %s (took %.2fms)".format(
                        LocalDateTime.now().toString(),
                        ctx.method(),
                        ctx.path(),
                        ctx.status(),
                        executionTimeMs
                    )
                )
            }

            cfg.accessManager(WebAccessManager)
        }

        app.routes {
            CheckUsernameAvailableEndpoint
            SignupEndpoint
            LinkDeviceEndpoint
            UploadBackupEndpoint

//            path("message") {
            WsMessageListener
//            }

//            val prefixedPath = ApiBuilder.prefixPath("message")
//            ApiBuilder.staticInstance().ws(prefixedPath, { cfg ->
//                cfg.handleAll(WsMessageListener)
//            }, WebRole.Device)
        }

//        app.ws("/message") {cfg ->
//            cfg.onConnect {
//                println(it)
//            }
//        }
//
//        app.before { ctx ->
//            ctx.header("Access-Control-Max-Age", "86400")
//            ctx.header("Access-Control-Allow-Origin", "*")
//            ctx.header("Access-Control-Allow-Headers", "Authorization,Content-Type")
//            ctx.header("Access-Control-Allow-Credentials", "true")
//            ctx.header("Vary", "Origin")
//        }

        return app
    }
}
