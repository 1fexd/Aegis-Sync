package fe.aegissync.module.database.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


object UserBackups : IntIdTable("UserBackup") {
    val user = reference("user", Users)
    val fileName = varchar("fileName", 128)
    val uploadedAt = datetime("uploadedAt").clientDefault { LocalDateTime.now() }
    val size = long("size")
}

class UserBackup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserBackup>(UserBackups)

    var user by UserBackups.user
    var fileName by UserBackups.fileName
    var uploadedAt by UserBackups.uploadedAt
    var size by UserBackups.size
}
