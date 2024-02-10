package ch.lucaro.hitls.api

import ch.lucaro.hitls.api.config.Config
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.http.Cookie
import io.javalin.http.SameSite
import io.javalin.http.staticfiles.Location
import io.javalin.rendering.template.JavalinJte

object API {

    private const val SESSION_COOKIE_NAME = "SESSIONID"
    private const val SESSION_COOKIE_LIFETIME = 60 * 60 * 24 //a day
    private val SESSION_TOKEN_CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'
    private const val SESSION_TOKEN_LENGTH = 32

    private val logger: KLogger = KotlinLogging.logger {}
    private var javalin: Javalin? = null
    private lateinit var jobManager: SortJobManager

    fun init(config: Config) {

        JavalinJte.init();

        jobManager = SortJobManager(config)

        this.javalin = Javalin.create {

            it.staticFiles.add(
                "static", Location.CLASSPATH
            )

        }.before { ctx ->

            //get or create session cookie
            val sessionId = ctx.cookie(SESSION_COOKIE_NAME)
                ?: List(SESSION_TOKEN_LENGTH) { SESSION_TOKEN_CHAR_POOL.random() }.joinToString("")

            //update cookie
            val cookie = Cookie(
                SESSION_COOKIE_NAME,
                sessionId,
                maxAge = SESSION_COOKIE_LIFETIME,
                secure = true,
                sameSite = SameSite.NONE
            )
            ctx.cookie(cookie)

            //set to context
            ctx.attribute("session", sessionId)

        }.routes {


            get("/") { ctx ->


                val userSession = UserSessionManager[ctx.session()]

                if (!userSession.taskStarted && ctx.queryParam("consent")?.toBooleanStrictOrNull() == true) {

                    logger.info { "starting new job for user '${userSession.sessionId}'" }
                    //TODO start sort job

                }

                ctx.render("main.jte", mapOf("state" to userSession.pageState))
            }

            get("/img/{job}/{img}") {ctx ->

                val jobName = ctx.pathParam("job")
                val imgName = ctx.pathParam("img")

                val imageFile = jobManager.getImage(jobName, imgName)

                if (imageFile != null) {
                    ctx.header("Cache-Control", "max-age=31622400")
                    ctx.writeSeekableStream(imageFile.inputStream(), "image/${imageFile.extension.lowercase()}")
                } else {
                    ctx.status(404)
                    ctx.result("Not found")
                }


            }



        }.start(config.port)


    }

    fun stop() {
        this.javalin?.stop()
        this.javalin = null
        this.jobManager.flushAll()
    }

}