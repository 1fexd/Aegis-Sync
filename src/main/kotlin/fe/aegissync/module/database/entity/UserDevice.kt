package fe.aegissync.module.database.entity

import fe.aegissync.util.TokenHandler
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import java.time.LocalDateTime

enum class DeviceType {
    App, BrowserExtension
}

object UserDevices : IntIdTable("UserDevice") {
    val user = reference("user", Users)
    val deviceType = enumeration<DeviceType>("deviceType")
    val token = varchar("token", 128).clientDefault { TokenHandler.getStandardCharsetString(128) }
    val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }

    fun getDeviceAndUser(deviceToken: String): ResultRow? {
        return UserDevices.innerJoin(Users).select {
            token eq deviceToken
        }.firstOrNull()
    }
}

class UserDevice(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDevice>(UserDevices)

    var user by UserDevices.user
    var deviceType by UserDevices.deviceType
    var token by UserDevices.token
    var createdAt by UserDevices.createdAt
}
