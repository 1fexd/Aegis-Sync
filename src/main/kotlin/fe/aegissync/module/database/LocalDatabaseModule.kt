package fe.aegissync.module.database

import fe.aegissync.config.LocalDatabaseConfig
import fe.koin.helper.ext.singleConfig
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import java.io.File

val localDatabaseModule = module {
    singleConfig<LocalDatabaseConfig, Database> { _, config ->
        val path = config.path
        val db = if (path != null) {
            config.type.database.file(File(config.path).apply { this.parentFile?.mkdir() })
        } else {
            config.type.database.memory()
        }

        db.connect()
    }
}
