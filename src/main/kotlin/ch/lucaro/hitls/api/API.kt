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
import java.util.UUID

object API {

    private const val SESSION_COOKIE_NAME = "SESSIONID"
    private const val SESSION_COOKIE_LIFETIME = 60 * 60 * 24 //a day
    private val SESSION_TOKEN_CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'
    private const val SESSION_TOKEN_LENGTH = 32

    private val logger: KLogger = KotlinLogging.logger {}
    private var javalin: Javalin? = null
    lateinit var jobManager: SortJobManager
        private set

    fun init(config: Config) {

        jobManager = SortJobManager(config)

        this.javalin = Javalin.create {

            it.staticFiles.add(
                "static", Location.CLASSPATH
            )
            it.fileRenderer(
                JavalinJte()
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

        }.get("/") { ctx ->

            val userSession = UserSessionManager[ctx.session()]

            if (!userSession.taskStarted && ctx.queryParam("consent")
                    ?.toBooleanStrictOrNull() == true
            ) { //accept button clicked

                logger.info { "starting new job for user '${userSession.sessionId}'" }
                userSession.start()

            }

            val queryParams = ctx.queryParamMap()

            if (queryParams.containsKey("o1") && queryParams.containsKey("o2")) { //answer provided
                try {
                    val o1 = UUID.fromString(queryParams["o1"]!!.first())
                    val o2 = UUID.fromString(queryParams["o2"]!!.first())
                    userSession.vote(o1, o2)
                } catch (e: Exception) {
                    logger.error(e) { "error during processing of response" }
                }
            }

            ctx.render("main.jte", mapOf("session" to userSession))

        }.get("/img/{job}/{img}") { ctx ->

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


        }.start(config.port)

    }

    fun stop() {
        this.javalin?.stop()
        this.javalin = null
        this.jobManager.flushAll()
        this.jobManager.writeCompleted()
    }

}