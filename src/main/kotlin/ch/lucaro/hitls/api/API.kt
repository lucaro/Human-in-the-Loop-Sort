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
        UserSessionManager.config = config

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

            if (userSession.page == UserSession.Page.START && ctx.queryParam("id") != null) { //store external id if set
                userSession.userId = ctx.queryParam("id")!!
            }

            if (!userSession.taskStarted && ctx.queryParam("consent")
                    ?.toBooleanStrictOrNull() == true
            ) { //accept button clicked

                logger.info { "starting new job for user '${userSession.sessionId}'" }
                userSession.start()

            }

            val queryParams = ctx.queryParamMap()

            if ((userSession.page == UserSession.Page.COMPARE || userSession.page == UserSession.Page.CHECK)
                && queryParams.containsKey("o1") && queryParams.containsKey("o2")) { //answer provided
                try {
                    val o1 = UUID.fromString(queryParams["o1"]!!.first())
                    val o2 = UUID.fromString(queryParams["o2"]!!.first())
                    userSession.vote(o1, o2)

                    if (userSession.page == UserSession.Page.CHECK && userSession.attentionCheck.complete) {
                        userSession.page = if (userSession.attentionCheck.succeeded) {
                            UserSession.Page.COMPARE
                        } else {
                            UserSession.Page.FAILED
                        }
                    }

                } catch (e: Exception) {
                    logger.error(e) { "error during processing of response" }
                }
            }

            ctx.render("main.jte", mapOf("session" to userSession, "config" to config))

        }.get("/img/{job}/{img}") { ctx -> //images to compare

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


        }.get("/status"){ctx -> //admin overview page

            if(ctx.queryParam("secret") != config.statusSecret) {
                ctx.status(403)
                ctx.result("Forbidden")
                return@get
            }

            ctx.render("status.jte", mapOf("jobManager" to jobManager, "sessionManager" to UserSessionManager))

        }.start(config.port)

    }

    fun stop() {
        this.javalin?.stop()
        this.javalin = null
        this.jobManager.flushAll()
        this.jobManager.writeCompleted()
    }

}