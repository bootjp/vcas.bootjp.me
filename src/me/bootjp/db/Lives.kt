package me.bootjp.db

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.Column
import org.joda.time.DateTime

object Lives : IntIdTable(name = "live") {
    val title: Column<String> = varchar("title", 256)
    val liveId: Column<String> = varchar("live_id", 128)
    val owner: Column<String> = varchar("owner", 256)
    val start = datetime("start")
    val image: Column<String> = varchar("image", 256)
    val description: Column<String> = varchar("description", 1024)
}


class Live(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Live>(Lives)
    var title by Lives.title
    var liveId by Lives.liveId
    var owner by Lives.owner
    var start by Lives.start
    var image by Lives.image
    var description by Lives.description


}