package me.bootjp

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import kotlinx.css.Float
import me.bootjp.db.*

import me.bootjp.db.Lives.description
import me.bootjp.db.Lives.owner
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.*
import java.text.*
import java.time.format.*
import java.time.ZonedDateTime




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
                    link(
                        href = "https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css",
                        rel = "stylesheet"
                    ) {
                        attributes["integrity"] = "ha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB"
                        attributes["crossorigin"] = "anonymous"
                    }
                    link(href = "/styles.css", rel = "stylesheet")
                    meta {
                        attributes["charset"] = "utf-8"
                    }
                    meta {
                        attributes["http-equiv"] = "X-UA-Compatible"
                        attributes["content"] = "IE=edge"
                    }
                    meta {
                        attributes["format-detection"] = "X-UA-Compatible"
                        attributes["content"] = "IE=edge"
                    }
                    meta(name = "format-detection", content = "telephone=no")
                    meta(name = "viewport", content = "width=device-width")
                    meta {
                        attributes["property"] = "og:title"
                        attributes["content"] = "バーチャルキャスト番組表"
                    }
                    meta {
                        attributes["property"] = "og:type"
                        attributes["content"] = "article"
                    }
                    meta {
                        attributes["property"] = "og:url"
                        attributes["content"] = "https://vcas.bootjp.me/"
                    }
                    meta {
                        attributes["property"] = "og:image"
                        attributes["content"] = "https://vcas.bootjp.me/ilust.jpg"
                    }
                    meta {
                        attributes["property"] = "og:site_name"
                        attributes["content"] = "バーチャルキャスト番組表"
                    }
                    meta {
                        attributes["property"] = "og:description"
                        attributes["content"] = "バーチャルキャストで放送中｜放送予定の番組一覧を確認できます．"
                    }
                    meta {
                        attributes["property"] = "twitter:card"
                        attributes["content"] = "summary"
                    }
                    meta {
                        attributes["property"] = "twitter:site"
                        attributes["content"] = "@bootjp"
                    }
                    title("バーチャルキャスト番組表")
                }

                body {
                    div("container-fluid") {
                        p { +"今の所「バーチャルキャスト」のタグがついているニコ生のものだけを対象としています．" }
                        p { a(href = "https://twitter.com/notify_vcas") { +"新着番組通知TwitterBot" } }
                    }

                    div("center") {
                        h2 { +"これから始まるバーチャルキャストの番組表" }
                        p { +"今後予定されている放送枠を直近順で表示中" }
                    }
                    div("row") {
                        transaction {
                            SchemaUtils.create(Lives)
                            Live.find {
                                Lives.start greater DateTime.now()
                            }.forEach {
                                    div("col-md-4 live") {
                                    p { a("https://nico.ms/${it.liveId}") { +it.title } }
                                    p { + it.start.toString( "yyyy年MM月dd日 HH:mm〜") }
                                    img(src = it.image)
                                    div("descriptions") {
                                        p { +it.description }
                                    }
                                    div("clear")
                                    p { +it.owner }
                                }
                            }
                        }
                        div("row align-items-center footer") {
                            ul {
                                li {a(href = "https://twitter.com/bootjp") { + "@bootjp"}}
                                li {a(href = "https://github.com/bootjp/virtualcast_schedule_table") { + "this site source code."}}
                            }
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
                rule(".center") {
                    textAlign = TextAlign.center
                }
                rule(".descriptions") {
                    paddingLeft = 10.px
                    fontSize = 90.pct
                }

                rule(".footer") {
                    marginRight = LinearDimension.auto
                    marginLeft = LinearDimension.auto
                    textAlign = TextAlign.center

                }
                rule("ul") {
                    listStyleType = ListStyleType.none
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
