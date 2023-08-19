package fe.aegissync

import fe.aegissync.config.Config
import fe.aegissync.module.database.entity.*
import fe.aegissync.module.database.localDatabaseModule
import fe.aegissync.module.web.JavalinInit
import fe.aegissync.module.web.JavalinProvider
import fe.aegissync.module.ws.webSocketClientHandlerModule
import fe.koin.exposed.init.DatabaseInit
import fe.koin.helper.ConfigBasedKoinApp
import fe.koin.helper.InitializerScope
import fe.koin.helper.ProviderScope
import fe.koin.helper.config.ConfigScope
import fe.koin.helper.start
import fe.kotlinx.cli.util.ExistingFileArgType
import kotlinx.cli.ArgParser
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.koin.mp.KoinPlatformTools

val databaseInit = DatabaseInit(Users, UserDevices, UserBackups)
object AegisSync : ConfigBasedKoinApp<Config> {
    override val setupConfig: ConfigScope<Config> = {
        config(it::databaseConfig)
        config(it::webConfig)
    }

    override val modules = arrayOf(localDatabaseModule, webSocketClientHandlerModule)

    override val providers: ProviderScope = {
        provider(JavalinProvider)
    }

    override val initializers: InitializerScope = {
        init(JavalinInit)
        init(databaseInit)
    }
}


@OptIn(ExperimentalSerializationApi::class)
fun main(args: Array<String>) {
    val parser = ArgParser("aegissync")
    val configPath by parser.argument(ExistingFileArgType)

    parser.parse(args)

//    val version = readVersion()
//    logger.info("Launching version %s", version.full)

    val koinApplication = AegisSync.start(configPath.inputStream().use { Json.decodeFromStream<Config>(it) })
    KoinPlatformTools.defaultContext().startKoin(koinApplication)

}

