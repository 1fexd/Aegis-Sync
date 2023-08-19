package fe.aegissync.module.web.endpoint.http

import fe.aegissync.config.StorageDirectory
import fe.aegissync.module.database.entity.UserBackups
import fe.aegissync.module.database.entity.UserDevices
import fe.aegissync.module.database.entity.Users
import fe.aegissync.module.web.WebAccessManager
import fe.aegissync.module.web.WebRole
import fe.aegissync.module.web.endpoint.base.http.ApiEndpoint
import fe.aegissync.module.web.endpoint.base.Response
import fe.aegissync.module.web.endpoint.base.send
import fe.aegissync.util.TokenHandler
import fe.koin.exposed.ext.rawTransaction
import fe.koin.exposed.ext.transactionOrNull
import io.javalin.http.Context
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.nio.file.Files


object UploadBackupEndpoint : ApiEndpoint(
    "upload",
    roles = arrayOf(WebRole.Device),
), KoinComponent {
    private val logger = KotlinLogging.logger(UploadBackupEndpoint::class.simpleName!!)

    private val database by inject<Database>()
    private val storageDiretory by inject<StorageDirectory>()

    private const val fileUploadName = "backupFile"

    override fun handle(ctx: Context) {
        val deviceToken = ctx.header(WebAccessManager.deviceTokenHeader)!!
        val row = database.transactionOrNull {
            UserDevices.getDeviceAndUser(deviceToken)
        }

        if (row == null) {
            logger.error("No user found for token $deviceToken")
            ctx.send(Response.InvalidAuthentication.Default)
            return
        }

        val userId = row[Users.id]
        val storageUsed = row[Users.storageUsed]
        val storageSize = row[Users.storageSize]

        val uploadedFile = ctx.uploadedFile(fileUploadName)

        if (uploadedFile == null) {
            logger.error("User with device $deviceToken did not upload a file")
            ctx.send(Response.InvalidBody("No file uploaded!"))
            return
        }

        val size = uploadedFile.size()
        if (storageUsed + size > storageSize) {
            logger.error("User with device $deviceToken is out of storage")
            ctx.send(Response.InvalidBody("Out of storage!"))
            return
        }

        val fileName = TokenHandler.getStandardCharsetString(128)
        val directory = File(storageDiretory.path).also { it.mkdirs() }

        val newFile = File(directory, fileName)
        try {
            uploadedFile.content().use { input ->
                Files.copy(input, newFile.toPath())
            }
        } catch (e: Exception) {
            logger.error(e) {
                "Something went wrong while saving file from user with device $deviceToken"
            }

            newFile.delete()
            ctx.send(Response.InternalError.Default)
            return
        }

        try {
            database.rawTransaction {
                UserBackups.insert {
                    it[UserBackups.user] = userId
                    it[UserBackups.fileName] = fileName
                    it[UserBackups.size] = size
                }

                Users.update(where = { Users.id eq userId }) {
                    it[Users.storageUsed] = storageUsed + size
                }
            }

            ctx.status(200)
        } catch (e: Exception) {
            logger.error(e) {
                "Something went wrong while saving backup file to database and updating user size for user with device $deviceToken"
            }

            newFile.delete()
            ctx.send(Response.InternalError.Default)
        }
    }
}
