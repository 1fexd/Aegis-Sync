package fe.aegissync.config

import fe.koin.exposed.database.LocalDatabaseType
import fe.koin.helper.config.KoinConfig
import fe.koin.helper.config.KoinRootConfig
import kotlinx.serialization.Serializable

@Serializable
data class Config(
//    val databaseConfig: LocalDatabaseConfig,
    val databaseConfig: LocalDatabaseConfig,
    val storageDirectory: StorageDirectory,
    val webConfig: WebConfig
) : KoinRootConfig<Config>

@Serializable
data class StorageDirectory(
    val path: String
)

@Serializable
data class LocalDatabaseConfig(
    val type: LocalDatabaseType,
    val path: String? = null,
) : KoinConfig<LocalDatabaseConfig>

@Serializable
data class WebConfig(
    val host: String? = "localhost",
    val port: Int
) : KoinConfig<WebConfig>
