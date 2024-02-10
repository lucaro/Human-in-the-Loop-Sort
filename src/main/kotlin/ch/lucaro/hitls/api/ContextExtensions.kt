package ch.lucaro.hitls.api

import io.javalin.http.Context

fun Context.session(): String = this.attribute<String>("session") ?: ""