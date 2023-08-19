package fe.aegissync.module.web

import fe.aegissync.config.WebConfig
import fe.koin.helper.init.KoinInit2
import io.javalin.Javalin

object JavalinInit : KoinInit2<Javalin, WebConfig> {
    override fun init(t: Javalin, t2: WebConfig) {
        t.start(t2.host, t2.port)
    }
}

