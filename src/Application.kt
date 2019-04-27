package me.bootjp

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import kotlinx.css.Float

import me.bootjp.db.Live
import me.bootjp.db.Live.description
import me.bootjp.db.Live.id
import me.bootjp.db.Live.image
import me.bootjp.db.Live.liveId
import me.bootjp.db.Live.owner
import me.bootjp.db.Live.slice
import me.bootjp.db.Live.start
import me.bootjp.db.Live.title
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    Database.connect("jdbc:mysql://localhost:3306/vcas", driver = "com.mysql.jdbc.Driver",
        user = "vcas", password = "vcas")

    routing {
        get("/") {

            call.respondHtml {
                head {
                    meta {
                        link(
                            href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css",
                            rel = "stylesheet"
                        )
                        link(href = "/styles.css", rel = "stylesheet")


                    }
                }

                body {
                    div("container-fluid ") {
                        p { +"今の所「バーチャルキャスト」のタグがついているニコ生のものだけを対象としています．" }
                        p { a(href = "https://twitter.com/notify_vcas") { +"新着番組通知TwitterBot" } }
                    }


                    transaction {
                        SchemaUtils.create (Live)
                        Live.selectAll().forEach {
                            div("col-md-4 live") {
                                p { a("https://nico.ms/${it[Live.liveId]}")  {+ it[Live.title] }}
                                img(src = it[Live.image])
                                div {
                                    p("descriptions") { +it[description]}
                                }
                                div("clear")
                                p {+ it[owner]}
                            }
                        }
                    }
                }
            }
        }

        get("/html-dsl") {
            call.respondHtml {
//                meta {
//
//                }
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {

                rule(".container-fluid"){
                    paddingRight = 15.px
                    paddingLeft = 15.px
                    marginRight = LinearDimension.auto
                    marginLeft = LinearDimension.auto


                }
                rule("img") {
                    backgroundColor = Color("aliceblue")
                    width = 100.px
                    height = 100.px
                }
//                                object-fit: contain;
                rule(".day") {
                    position = Position.relative
                    width =100.pct
                    marginLeft = 20.px
                }


                rule(".live") {
                    backgroundColor = Color("aliceblue")
                    paddingRight = 15.px
                    wordBreak = WordBreak.breakAll
                    height = 370.px
                    border = "solid #fff"
                }
                rule(".thumbnail") {
                    float = Float.left
                }
                rule(".clear") {
                    clear = Clear.both
                }
                rule(".descriptions") {
                    paddingLeft = 10.px
                    fontSize = 90.pct
                }

            }
        }
    }
}

private operator fun ResultRow.get(title: String): String {
    return title
}


fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
