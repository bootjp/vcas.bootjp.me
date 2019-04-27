package me.bootjp.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Live : Table(name = "live") {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val title: Column<String> = varchar("title", 256)
    val liveId: Column<String> = varchar("live_id", 128)
    val owner: Column<String> = varchar("owner", 256)
    val start: Column<DateTime> = datetime("start")
    val image: Column<String> = varchar("image", 256)
    val description: Column<String> = varchar("description", 1024)
}
