package fe.aegissync.module.database.entity

import fe.aegissync.crypto.Crypto
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable("User") {
    val username = varchar("username", 64).uniqueIndex()
    val hashedMasterPasswordHash = binary("hashedMasterPasswordHash", Crypto.masterPasswordHashBitLength)
    val hashedMasterPasswordHashSalt = binary("hashedMasterPasswordHashSalt", Crypto.saltBitLength)
    val protectedSymetricKey = binary("protectedSymetricKey", Crypto.protectedSymetricKeyBitLength)
    val protectedSymetricKeyIV = binary("protectedSymetricKeyIV", Crypto.ivBitLength)

    // 100M
    val storageSize = long("storageSize").default(104900000L)
    val storageUsed = long("storageUsed").default(0L)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var hashedMasterPasswordHash by Users.hashedMasterPasswordHash
    var hashedMasterPasswordHashSalt by Users.hashedMasterPasswordHashSalt
    var protectedSymetricKey by Users.protectedSymetricKey
    var protectedSymetricKeyIV by Users.protectedSymetricKeyIV
    var storageSize by Users.storageSize
    var storageUsed by Users.storageUsed
}
