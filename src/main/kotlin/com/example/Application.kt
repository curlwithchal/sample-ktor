package com.example

import com.example.plugins.configureSerialization
import com.example.plugins.configureTemplating
import io.ktor.server.application.*

fun main(args: Array<String>) {
    // Change Port Via Application

    /*embeddedServer(
        Netty,
        port = 8888,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)*/

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureTemplating()
    configureSerialization()
    configureRouting()
}
