package fe.aegissync.test

import fe.aegissync.config.StorageDirectory
import fe.aegissync.databaseInit
import fe.aegissync.module.ws.webSocketClientHandlerModule
import fe.koin.exposed.database.LocalDatabaseType
import fe.koin.helper.util.singleModule
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import org.jetbrains.exposed.sql.Database
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import java.io.File

fun startTestKoin(database: String = "test.sqlite", keepDatabase: Boolean = true): KoinApplication {
    val testDb = File(database)
    if (!keepDatabase) testDb.delete()

    val koinApplication = startKoin {
        modules(
            singleModule { LocalDatabaseType.SQLite.database.file(testDb).connect() },
            singleModule { StorageDirectory("test_backup_dir") },
            webSocketClientHandlerModule,
        )
    }

    ApiBuilder.setStaticJavalin(Javalin(null, null))

    databaseInit.init(koinApplication.koin.get<Database>())
    return koinApplication
}
